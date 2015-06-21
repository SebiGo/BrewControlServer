package ch.goodrick.brewcontrol.mashing;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import ch.goodrick.brewcontrol.actuator.Actuator;
import ch.goodrick.brewcontrol.actuator.FakeActuator;
import ch.goodrick.brewcontrol.button.Button;
import ch.goodrick.brewcontrol.button.VirtualButton;
import ch.goodrick.brewcontrol.common.PhysicalQuantity;
import ch.goodrick.brewcontrol.sensor.FakeSensor;
import ch.goodrick.brewcontrol.sensor.Sensor;

public class MashingTest {

	@Test
	public void testMashingTermination() throws MashingException {
		Mashing mashing = Mashing.getInstance();
		mashing.setFirstRest(null);
		mashing.startMashing();
		mashing.continueRest();
	}

	@Test(expected = MashingException.class)
	public void testStartUninitialisedMashing() throws MashingException {
		Mashing mashing = Mashing.getInstance();
		mashing.startMashing();
	}

	@Test
	public void test() throws IOException, InterruptedException {
		String name = "test";
		Mashing mashing = Mashing.getInstance();
		mashing.terminate();
		Actuator actuator = new FakeActuator(PhysicalQuantity.TEMPERATURE);
		Sensor sensor = new FakeSensor(actuator);
		Button button = new VirtualButton();
		mashing.initMashing(sensor, actuator, button);
		mashing.setName(name);

		assertEquals(actuator, mashing.getActuator());
		assertEquals(name, mashing.getName());
		assertEquals(sensor.getID(), mashing.getTemperatureSensor().getID());
		assertEquals(sensor.getPhysicalQuantity(), mashing.getTemperatureSensor().getPhysicalQuantity());
		assertTrue(mashing.getTemperatureSensor().getValue() > 0d);

		mashing.addRest(new Rest(name, 1d, 1, true));
		assertNotNull(mashing.getFirstRest());

		try {
			mashing.startMashing();
			assertNotNull(mashing.getTempLogger());
		} catch (MashingException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testTerminate() throws IOException, InterruptedException {
		String name = "test";
		Mashing mashing = Mashing.getInstance();
		mashing.terminate();
		Actuator actuator = new FakeActuator(PhysicalQuantity.TEMPERATURE);
		Sensor sensor = new FakeSensor(actuator);
		Button button = new VirtualButton();
		mashing.initMashing(sensor, actuator, button);
		mashing.setName(name);
		mashing.addRest(new Rest(name, 100d, 1, true));

		try {
			assertEquals(RestState.INACTIVE, mashing.getFirstRest().getState());
			mashing.startMashing();
			Thread.sleep(50);
			assertEquals(RestState.HEATING, mashing.getFirstRest().getState());
			mashing.terminate();
			assertEquals(RestState.INACTIVE, mashing.getFirstRest().getState());
		} catch (MashingException e) {
			fail(e.getMessage());
		}
	}
}
