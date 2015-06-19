package ch.goodrick.brewcontrol.logger;

/**
 * This is the generic interface for a sensor value logger.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public interface Logger {
	/**
	 * Use this method to log a value.
	 * 
	 * @param value
	 *            the value to be logged.
	 */
	void log(Double value);
}
