package ch.goodrick.brewcontrol.actuator;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import ch.goodrick.brewcontrol.common.PhysicalQuantity;

import com.pi4j.device.piface.PiFaceRelay;

public class PiFaceRelayActuatorTest {

	@Test(expected = NullPointerException.class)
	public void testOn() throws IOException {
		PiFaceRelayActuator pia = new PiFaceRelayActuator(null, PiFaceRelay.K0, PhysicalQuantity.TEMPERATURE);
		assertEquals(PhysicalQuantity.TEMPERATURE, pia.getPhysicalQuantity());
		pia.on();
	}

	@Test(expected = NullPointerException.class)
	public void testOff() throws IOException {
		PiFaceRelayActuator pia = new PiFaceRelayActuator(null, PiFaceRelay.K0, PhysicalQuantity.TEMPERATURE);
		assertEquals(PhysicalQuantity.TEMPERATURE, pia.getPhysicalQuantity());
		pia.off();
	}
}
