package ch.goodrick.brewcontrol.sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.goodrick.brewcontrol.BrewControl;
import ch.goodrick.brewcontrol.common.PhysicalQuantity;

public abstract class CalibratedTemperatureSensor implements Sensor {
	static Logger log = LoggerFactory.getLogger(BrewControl.class);
	private double delta0 = 0;
	private double correctionFactor = 1;

	private double tempIceWater = 0;
	private double tempBoilingWater = 100;
	private long altitude = 0;

	public double getTempIceWater() {
		return tempIceWater;
	}

	public double getTempBoilingWater() {
		return tempBoilingWater;
	}

	public long getAltitude() {
		return altitude;
	}

	/**
	 * Rule of the thumb: 1°C less per 300 m
	 * 
	 * @param altitude
	 *            the altitude for the expected value
	 * @return the boiling temperature
	 */
	private static double getExpectedBoilingTemperature(long altitude) {
		return 100 - new Double(altitude) / 300;
	}

	public void calibrate(double tempIceWater, double tempBoilingWater, long altitude) {
		this.altitude = altitude;
		this.tempBoilingWater = tempBoilingWater;
		this.tempIceWater = tempIceWater;
		double delta100 = getExpectedBoilingTemperature(altitude) - tempBoilingWater;
		delta0 = 0 - tempIceWater;
		correctionFactor = (delta100 - delta0) / tempBoilingWater;
	}

	/**
	 * Calibrates the passed value with a linear function.
	 * 
	 * @param value
	 *            the actual sensor reading
	 * @return a calibrated value for the passed value
	 */
	protected double getCalibratedValue(double value) {
		return correctionFactor * value + delta0 + value;
	}

	@Override
	public PhysicalQuantity getPhysicalQuantity() {
		return PhysicalQuantity.TEMPERATURE;
	}
}
