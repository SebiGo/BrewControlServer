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
		s.calibrate(0d, 100d, 0);
		fa.on();
		assertEquals(s.getID(), "FAKE_SENSOR");
		try {
			assertEquals(20.1d, s.getValue(), 0.01d);
			assertEquals(20.2d, s.getValue(), 0.01d);
		} catch (IOException e) {
			fail("IOException ocurred.");
		}
		fa.off();
		assertEquals(s.getID(), "FAKE_SENSOR");
		try {
			assertEquals(20.199d, s.getValue(), 0.0005d);
			assertEquals(20.198d, s.getValue(), 0.0005d);
		} catch (IOException e) {
			fail("IOException ocurred.");
		}
		assertEquals(s.getPhysicalQuantity(), PhysicalQuantity.TEMPERATURE);
	}

	@Test
	public void testCalibration() throws IOException {
		CalibratedTemperatureSensor s = new CalibratedTemperatureSensor() {
			@Override
			public Double getValue() throws IOException {
				return getCalibratedValue(100d);
			}
			@Override
			public String getID() {
				return "internal";
			}
		};
		s.calibrate(0d, 96.66d, 1000);
		assertEquals(100d, s.getValue(), 0.1d);
		s = new CalibratedTemperatureSensor() {
			@Override
			public Double getValue() throws IOException {
				return getCalibratedValue(0d);
			}
			@Override
			public String getID() {
				return "internal";
			}
		};
		s.calibrate(0d, 96.66d, 1000);
		assertEquals(0d, s.getValue(), 0.1d);
	}
}
