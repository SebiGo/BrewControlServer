package ch.goodrick.brewcontrol.logger;

import org.slf4j.LoggerFactory;

/**
 * This logger logs all logged data to the console as a debug log statement. You
 * can use this logger for development or debugging.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class OutputLogger implements Logger {
	private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void log(Double value) {
		log.debug("" + value + "");
	}
}
