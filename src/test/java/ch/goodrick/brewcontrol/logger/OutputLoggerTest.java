package ch.goodrick.brewcontrol.logger;

import org.junit.Test;

public class OutputLoggerTest {

	@Test
	public void log() {
		OutputLogger ol = new OutputLogger();
		ol.log(0d);
	}

}
