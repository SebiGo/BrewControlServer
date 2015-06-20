package ch.goodrick.brewcontrol;

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
import ch.goodrick.brewcontrol.actuator.GPIOActuator;
import ch.goodrick.brewcontrol.actuator.PiFaceRelayActuator;
import ch.goodrick.brewcontrol.button.FakeButton;
import ch.goodrick.brewcontrol.button.GPIOButton;
import ch.goodrick.brewcontrol.button.PiFaceButton;
import ch.goodrick.brewcontrol.button.VirtualButton;
import ch.goodrick.brewcontrol.common.OperationMode;
import ch.goodrick.brewcontrol.common.PhysicalQuantity;
import ch.goodrick.brewcontrol.mashing.Mashing;
import ch.goodrick.brewcontrol.mashing.Rest;
import ch.goodrick.brewcontrol.sensor.FakeSensor;
import ch.goodrick.brewcontrol.sensor.SensorDS18B20;
import ch.goodrick.brewcontrol.service.MashingService;
import ch.goodrick.brewcontrol.service.RestService;

import com.pi4j.device.piface.PiFace;
import com.pi4j.device.piface.PiFaceRelay;
import com.pi4j.device.piface.PiFaceSwitch;
import com.pi4j.device.piface.impl.PiFaceDevice;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiChannel;

// TODO 3 calibration of sensor
// TODO 3 websocket & client side rendering
// TODO 3 UI detect if server is present at all
// TODO 3 Bug Server URL change
// TODO 9 config file for parameters
// TODO 9 save/load recipe
// TODO 9 actuator for motor

/**
 * This is the main code for the BrewControl software. It contains the static
 * main method to launch the program.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class BrewControl {
	static Logger log = LoggerFactory.getLogger(BrewControl.class);
	/**
	 * The main method for BrewCrontol intended to start the standalone
	 * BrewControl server
	 * 
	 * @param argv
	 *            no arguments required, leave empty
	 * @throws Exception
	 */
	public static void main(String[] argv) throws Exception {
		OperationMode opMode = null;

		if (argv.length != 1) {
			String modes = "";
			for (OperationMode mode : OperationMode.values()) {
				modes += mode.getCommandlineOption() + "|";
			}
			log.error("Please supply mode [" + modes.substring(0, modes.length() - 1) + "]");
			System.exit(-1);
		} else {
			opMode = OperationMode.getModeFromCommandlineOption(argv[0]);
		}

		Mashing.getInstance().setName("Weizen");
		Mashing.getInstance().addRest(new Rest("Eiweissrast", 30d, 1, Boolean.FALSE));
		Mashing.getInstance().addRest(new Rest("Maltoserast", 35d, 50, Boolean.TRUE));
		Mashing.getInstance().addRest(new Rest("Verzuckerungsrast", 40d, 25, Boolean.FALSE));
		Mashing.getInstance().addRest(new Rest("Abmaischen", 78d, 1, Boolean.FALSE));

		Rest start = Mashing.getInstance().getRest();

		while (start != null) {
			log.info(start.getName() + " at " + start.getTemperature() + "" + PhysicalQuantity.TEMPERATURE.getUnit() + " for " + (start.getDuration())
					+ " min (" + ((start.isContinueAutomatically()) ? "" : "don't ") + "continue automatically).");
			start = start.getNextRest();
		}

		startServices();

		switch (opMode) {
		case SIMULATE:
			Actuator a = new FakeActuator(PhysicalQuantity.TEMPERATURE);
			Mashing.getInstance().initMashing(new FakeSensor(a), a, new FakeButton(), new VirtualButton());
			break;
		case GPIO:
			SensorDS18B20 s1 = new SensorDS18B20();
			s1.calibrate(0.9, 99.25, 434);
			Mashing.getInstance().initMashing(s1, new GPIOActuator(RaspiPin.GPIO_04, PhysicalQuantity.TEMPERATURE), new VirtualButton(),
					new GPIOButton(RaspiPin.GPIO_01));
			break;
		case PIFACE:
			final PiFace piface = new PiFaceDevice(PiFace.DEFAULT_ADDRESS, SpiChannel.CS0);
			SensorDS18B20 s2 = new SensorDS18B20();
			s2.calibrate(0.9, 99.25, 434);
			Mashing.getInstance().initMashing(s2, new PiFaceRelayActuator(piface, PiFaceRelay.K0, PhysicalQuantity.TEMPERATURE),
					new VirtualButton(), new PiFaceButton(piface, PiFaceSwitch.S1));
			break;
		}
	}

	/**
	 * Helper method to start all rest services on a Jettison server as supplied
	 * by the Apache cxf libraries.
	 * 
	 * @throws Exception
	 */
	private static void startServices() throws Exception {
		List<JSONProvider> providers = new ArrayList<JSONProvider>();
		JSONProvider jsonProvider = new JSONProvider();
		// jsonProvider.setSerializeAsArray(false);
		// jsonProvider.setConvertTypesToStrings(true);
		Map<String, String> map = new HashMap<String, String>();
		map.put("http://www.goodrick.ch", "brewcontrol");
		jsonProvider.setNamespaceMap(map);
		providers.add(jsonProvider);

		JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean();
		factoryBean.setResourceClasses(RestService.class, MashingService.class);
		factoryBean.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
		factoryBean.setAddress("http://" + getNetworkAddress() + ":8080/");
		factoryBean.setProviders(providers);
		factoryBean.create();
	}

	/**
	 * Retrieve an external network address for this server to connect the
	 * Jettison server to.
	 * 
	 * @return
	 */
	private static String getNetworkAddress() {
		Enumeration<NetworkInterface> nics;
		try {
			nics = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			return "localhost";
		}
		while (nics.hasMoreElements()) {
			NetworkInterface nic = (NetworkInterface) nics.nextElement();
			Enumeration<InetAddress> addresses = nic.getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress address = (InetAddress) addresses.nextElement();
				if (!address.isLoopbackAddress() && address.getHostAddress().startsWith("10.")) {
					return address.getHostAddress();
				}
			}
		}
		return "localhost";
	}

}
