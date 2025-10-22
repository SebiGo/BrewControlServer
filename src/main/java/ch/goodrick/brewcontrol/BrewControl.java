package ch.goodrick.brewcontrol;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.goodrick.brewcontrol.actuator.Actuator;
import ch.goodrick.brewcontrol.actuator.FakeActuator;
import ch.goodrick.brewcontrol.actuator.GPIOActuator; // Pi4J v2-basierte Implementierung
import ch.goodrick.brewcontrol.button.FakeButton;
import ch.goodrick.brewcontrol.button.GPIOButton; // Pi4J v2-basierte Implementierung
import ch.goodrick.brewcontrol.button.VirtualButton;
import ch.goodrick.brewcontrol.common.OperationMode;
import ch.goodrick.brewcontrol.common.PhysicalQuantity;
import ch.goodrick.brewcontrol.mashing.Mashing;
import ch.goodrick.brewcontrol.mashing.Rest;
import ch.goodrick.brewcontrol.sensor.FakeSensor;
import ch.goodrick.brewcontrol.sensor.SensorDS18B20;
import ch.goodrick.brewcontrol.service.InfoService;
import ch.goodrick.brewcontrol.service.MashingService;
import ch.goodrick.brewcontrol.service.RestService;

/**
 * Main-Klasse für BrewControl. Startet Rest-Services und initialisiert Mashing
 * je nach Modus.
 *
 * Autor: sebastian@goodrick.ch
 */
public class BrewControl {
	static Logger log = LoggerFactory.getLogger(BrewControl.class);

	/**
	 * @param argv genau ein Argument: Betriebsmodus
	 */
	public static void main(String[] argv) throws Exception {
		OperationMode opMode = null;

		if (argv.length != 1) {
			StringBuilder modes = new StringBuilder();
			for (OperationMode mode : OperationMode.values()) {
				modes.append(mode.getCommandlineOption()).append('|');
			}
			log.error("Please supply mode [{}]", modes.substring(0, modes.length() - 1));
			return;
		} else {
			opMode = OperationMode.getModeFromCommandlineOption(argv[0]);
		}

		// Beispiel-Rasten vorbelegen
		Mashing.getInstance().setName("BrewControl");
		Mashing.getInstance().addRest(new Rest("Einmaischen", 57d, 0, Boolean.FALSE));
		Mashing.getInstance().addRest(new Rest("Eiweissrast", 55d, 15, Boolean.TRUE));
		Mashing.getInstance().addRest(new Rest("Maltoserast", 62d, 50, Boolean.TRUE));
		Mashing.getInstance().addRest(new Rest("Verzuckerungsrast", 72d, 1, Boolean.FALSE));
		Mashing.getInstance().addRest(new Rest("Abmaischen", 78d, 1, Boolean.FALSE));

		Rest start = Mashing.getInstance().getRest();
		while (start != null) {
			log.info("{} at {}{} for {} min ({}continue automatically).", start.getName(), start.getTemperature(),
					PhysicalQuantity.TEMPERATURE.getUnit(), start.getDuration(),
					(start.isContinueAutomatically()) ? "" : "don't ");
			start = start.getNextRest();
		}

		startServices();

		switch (opMode) {
		case SIMULATE: {
			Actuator a = new FakeActuator(PhysicalQuantity.TEMPERATURE);
			Mashing.getInstance().initMashing(new FakeSensor(a), a, new FakeButton(), new VirtualButton());
			break;
		}
		case GPIO: {
			// DS18B20-Temperatursensor nutzen
			SensorDS18B20 s1 = new SensorDS18B20();
			s1.calibrate(0, 100, 0);

			// Pi4J v2: BCM-Nummern verwenden (z.B. 4 == GPIO4)
			int heaterBcmPin = 4; // vorher RaspiPin.GPIO_04
			int buttonBcmPin = 1; // vorher RaspiPin.GPIO_01 (BCM1 = SDA1)

			GPIOActuator actuator = new GPIOActuator(heaterBcmPin, PhysicalQuantity.TEMPERATURE);
			GPIOButton hwButton = new GPIOButton(buttonBcmPin);

			Mashing.getInstance().initMashing(s1, actuator, new VirtualButton(), hwButton);
			break;
		}
		}
	}

	/**
	 * Startet die REST-Services via Apache CXF (JAX-RS).
	 */
	private static void startServices() {
		List<JSONProvider<?>> providers = new ArrayList<>();
		JSONProvider<?> jsonProvider = new JSONProvider<>();
		Map<String, String> map = new HashMap<>();
		map.put("http://www.goodrick.ch", "brewcontrol");
		jsonProvider.setNamespaceMap(map);
		providers.add(jsonProvider);

		JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean();
		factoryBean.setResourceClasses(RestService.class, MashingService.class, InfoService.class);
		factoryBean.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
		factoryBean.setAddress("http://" + getNetworkAddress() + ":8080/");
		factoryBean.setProviders(providers);
		factoryBean.create();
		log.info("REST services started at http://{}:8080/", getNetworkAddress());
	}

	/**
	 * Ermittelt eine externe IPv4-Adresse für das lokale System.
	 */
	private static String getNetworkAddress() {
		try {
			Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
			while (nics.hasMoreElements()) {
				NetworkInterface nic = nics.nextElement();
				Enumeration<InetAddress> addresses = nic.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress address = addresses.nextElement();
					if (!address.isLinkLocalAddress() && address instanceof Inet4Address) {
						return address.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			log.warn("Could not enumerate network interfaces", e);
		}
		return "localhost";
	}
}
