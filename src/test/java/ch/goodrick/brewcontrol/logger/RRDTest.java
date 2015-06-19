package ch.goodrick.brewcontrol.logger;

import static org.junit.Assert.fail;

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
}
