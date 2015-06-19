package ch.goodrick.brewcontrol.actuator;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.goodrick.brewcontrol.common.PhysicalQuantity;

public class FakeActuatorTest {
	Actuator fa = new FakeActuator(PhysicalQuantity.TEMPERATURE);

	@Test
	public void test() {
		assertEquals(fa.getPhysicalQuantity(), PhysicalQuantity.TEMPERATURE);
		fa.on();
		assertEquals(fa.getStatus(), ActuatorStatus.ON);
		fa.off();
		assertEquals(fa.getStatus(), ActuatorStatus.OFF);
	}

}
