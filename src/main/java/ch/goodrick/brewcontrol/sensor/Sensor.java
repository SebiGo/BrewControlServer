package ch.goodrick.brewcontrol.sensor;

import java.io.IOException;

import ch.goodrick.brewcontrol.common.PhysicalQuantity;

/**
 * The generic sensor interface for all sensors.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public interface Sensor {

	/**
	 * Get the id of the sensor.
	 * 
	 * @return Return the ID of the sensor. The format is sensor specific.
	 */
	public String getID();

	/**
	 * Get the current value of the sensor, i.e. trigger a read on the physical
	 * sensor. Might be slow.
	 * 
	 * @return the value of the sensor
	 * @throws IOException
	 *             if there was a problem accessing the sensor
	 */
	public Double getValue() throws IOException;

	/**
	 * Get the physical quantity of the server.
	 * 
	 * @return the physical Quantity
	 */
	public PhysicalQuantity getPhysicalQuantity();

	/**
	 * The temperature measured in ice water.
	 * 
	 * @return the temperature measured in ice water
	 */
	public double getTempIceWater();

	/**
	 * The temperature measured in boiling water.
	 * 
	 * @return the temperature measured in boiling water
	 */
	public double getTempBoilingWater();

	/**
	 * The altitude for the calibration measurements.
	 * 
	 * @return the altitude for the calibration measurements
	 */
	public long getAltitude();

	/**
	 * Set the calibration values.
	 * 
	 * @param tempIceWater
	 *            the temperature measured in ice water
	 * @param tempBoilingWater
	 *            the temperature measured in boiling water
	 * @param altitude
	 *            the altitude for the calibration measurements
	 */
	public void calibrate(double tempIceWater, double tempBoilingWater, long altitude);
}
