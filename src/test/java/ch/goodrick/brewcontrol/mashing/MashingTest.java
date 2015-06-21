package ch.goodrick.brewcontrol.mashing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
	public void test() throws IOException {
		String name = "test";
		Mashing mashing = Mashing.getInstance();
		Actuator actuator = new FakeActuator(PhysicalQuantity.TEMPERATURE);
		Sensor sensor = new FakeSensor(actuator);
		Button button = new VirtualButton();
		mashing.initMashing(sensor, actuator, button);
		mashing.setName(name);

		assertEquals(mashing.getActuator(), actuator);
		assertEquals(mashing.getName(), name);

		mashing.addRest(new Rest(name, 1d, 1, true));
		assertNotNull(mashing.getFirstRest());

		try {
			mashing.startMashing();
		} catch (MashingException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testTerminate() throws IOException, InterruptedException {
		String name = "test";
		Mashing mashing = Mashing.getInstance();
		Actuator actuator = new FakeActuator(PhysicalQuantity.TEMPERATURE);
		Sensor sensor = new FakeSensor(actuator);
		Button button = new VirtualButton();
		mashing.initMashing(sensor, actuator, button);
		mashing.setName(name);
		mashing.addRest(new Rest(name, 100d, 1, true));
		
		try {
			assertEquals(RestState.INACTIVE, mashing.getFirstRest().getState());
			mashing.startMashing();
			Thread.sleep(10);
			assertEquals(RestState.HEATING, mashing.getFirstRest().getState());
			mashing.terminate();
			assertEquals(RestState.INACTIVE, mashing.getFirstRest().getState());
		} catch (MashingException e) {
			fail(e.getMessage());
		}
	}
}
