package ch.goodrick.brewcontrol.sensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import ch.goodrick.brewcontrol.actuator.Actuator;
import ch.goodrick.brewcontrol.actuator.FakeActuator;
import ch.goodrick.brewcontrol.common.PhysicalQuantity;

public class SensorFakeTest {
	FakeSensor s;

	@Test
	public void test() {
		Actuator fa = new FakeActuator(PhysicalQuantity.TEMPERATURE);
		s = new FakeSensor(fa);
		fa.on();
		assertEquals(s.getID(), "FAKE_SENSOR");
		try {
			assertEquals(s.getValue(), 20.1d, 0.01d);
			assertEquals(s.getValue(), 20.2d, 0.01d);
		} catch (IOException e) {
			fail("IOException ocurred.");
		}
		fa.off();
		assertEquals(s.getID(), "FAKE_SENSOR");
		try {
			assertEquals(s.getValue(), 20.199d, 0.0005d);
			assertEquals(s.getValue(), 20.198d, 0.0005d);
		} catch (IOException e) {
			fail("IOException ocurred.");
		}
		assertEquals(s.getPhysicalQuantity(), PhysicalQuantity.TEMPERATURE);
	}

}
