package ch.goodrick.brewcontrol.sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.goodrick.brewcontrol.BrewControl;

public abstract class CalibrateTemperatureSensor {
	static Logger log = LoggerFactory.getLogger(BrewControl.class);
	private double delta0 = 0;
	private double correctionFactor = 1;

	/**
	 * Rule of the thumb: 1°C less per 300 m
	 * 
	 * @param altitude
	 *            the altitude for the expected value
	 * @return the boiling temperature
	 */
	private static double getExpectedBoilingTemperature(long altitude) {
		return 100 - altitude / 300;
	}

	public void calibrate(double tempIceWater, double tempBoilingWater, long altitude) {
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
}
