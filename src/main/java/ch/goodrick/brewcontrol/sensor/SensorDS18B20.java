package ch.goodrick.brewcontrol.sensor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.goodrick.brewcontrol.common.PhysicalQuantity;

/**
 * A software representation and driver for the DS18B20 1-wire temperature
 * sensor. The sensor needs to be connected to pin GPIO_04, w1-gpio and w1-therm
 * kernel modules need to be loaded and the /boot/config.txt needs the line
 * dtoverlay=w1-gpio.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class SensorDS18B20 extends CalibrateTemperatureSensor implements Sensor {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	private final File sensorFile;
	private final File valueFile;
	private final static String SENSOR_FOLDER = "/sys/bus/w1/devices";

	/**
	 * Construct the object and look for a specific sensor id. (In case there
	 * are more than one sensors connected to the 1-wire bus.)
	 * 
	 * @param id
	 *            the id of the sensor.
	 * @throws IOException
	 */
	public SensorDS18B20(String id) throws IOException {
		sensorFile = connectSensor(id);
		log.info("Using sensor DS18B20 with ID: " + sensorFile.getName());
		valueFile = deriveValueFile(sensorFile);
	}

	/**
	 * Construct the object with any first found sensor on the 1-wire bus.
	 * 
	 * @throws IOException
	 */
	public SensorDS18B20() throws IOException {
		sensorFile = connectSensor();
		log.info("Using sensor DS18B20 with ID: " + sensorFile.getName());
		valueFile = deriveValueFile(sensorFile);
	}

	@Override
	public String getID() {
		return sensorFile.getName();
	}

	@Override
	public synchronized Double getValue() throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(valueFile))) {
			String tmp = reader.readLine();
			int index = -1;
			while (tmp != null) {
				index = tmp.indexOf("t=");
				if (index >= 0) {
					break;
				}
				tmp = reader.readLine();
			}
			if (index < 0) {
				throw new IOException("Could not read sensor " + getID());
			}
			return getCalibratedValue(Integer.parseInt(tmp.substring(index + 2)) / 1000d);
		}
	}

	@Override
	public PhysicalQuantity getPhysicalQuantity() {
		return PhysicalQuantity.TEMPERATURE;
	}

	private static File deriveValueFile(File sensorFile) {
		return new File(sensorFile, "w1_slave");
	}

	/**
	 * Select sensor with specific id
	 * 
	 * @return sensor with specific id
	 * @throws IOException
	 */
	private File connectSensor(String id) throws IOException {
		File sensorFolder = new File(SENSOR_FOLDER);
		if (!sensorFolder.exists()) {
			throw new IOException("Could not find w1 devices! Please ensure that mods w1-gpio and w1-therm are loaded.");
		}
		for (File f : sensorFolder.listFiles()) {
			if (f.getName().equals(id)) {
				return f;
			}
		}
		throw new IOException("Could not find w1 device " + id + ".");
	}

	/**
	 * Select first available sensor
	 * 
	 * @return first available sensor
	 * @throws IOException
	 */
	private File connectSensor() throws IOException {
		File sensorFolder = new File(SENSOR_FOLDER);
		if (!sensorFolder.exists()) {
			throw new IOException(
					"Could not find w1 devices! Please ensure that mods w1-gpio and w1-therm are loaded and dtoverlay=w1-gpio in /boot/config.txt is set.");
		}
		for (File f : sensorFolder.listFiles()) {
			if (f.getName().startsWith("w1_bus_master")) {
				continue;
			}
			return f;
		}
		throw new IOException("Could not find any w1 device.");
	}
}
