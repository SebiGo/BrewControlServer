package ch.goodrick.brewcontrol.logger;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import ch.goodrick.brewcontrol.common.PhysicalQuantity;

/**
 * RRD is difficult to test in a unit test, since it would take a very long
 * time. Every log sample must not arrive before a set amount of time.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class RRDTest {

	@Test(expected = IllegalArgumentException.class)
	public void log() {
		// RRD is tested
		try {
			RRD rrd = new RRD("test", PhysicalQuantity.TEMPERATURE);
			rrd.log(Double.MAX_VALUE);
			rrd.log(1d);
		} catch (IOException e) {
			fail("IOException while creating class RRD.");
		}
	}

	@Test
	public void getGraph() throws IOException, InterruptedException {
		RRD rrd = new RRD("test", PhysicalQuantity.TEMPERATURE);
		// Thread.sleep(1500);
		// rrd.log(1d);
		// assertEquals("test.png", rrd.getGraph().getName());
	}

	@Test(expected=IOException.class)
	public void getGraphException() throws IOException {
		RRD rrd = new RRD("test", PhysicalQuantity.TEMPERATURE);
		rrd.getGraph();
	}

	@Test
	public void getNonEmptyFile() throws IOException, InterruptedException {
		RRD rrd = new RRD("test", PhysicalQuantity.TEMPERATURE);
		Thread.sleep(1000);
		rrd.log(1d);
		assertEquals(new File("test.png"), rrd.getGraph());
	}
}
