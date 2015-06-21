package ch.goodrick.brewcontrol.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import ch.goodrick.brewcontrol.actuator.Actuator;
import ch.goodrick.brewcontrol.actuator.FakeActuator;
import ch.goodrick.brewcontrol.button.Button;
import ch.goodrick.brewcontrol.button.VirtualButton;
import ch.goodrick.brewcontrol.common.PhysicalQuantity;
import ch.goodrick.brewcontrol.mashing.Mashing;
import ch.goodrick.brewcontrol.mashing.Rest;
import ch.goodrick.brewcontrol.sensor.FakeSensor;
import ch.goodrick.brewcontrol.sensor.Sensor;

public class MashingServiceTest {
	final String test = "test";

	@Test
	public void test() throws InterruptedException, IOException {
		String name = "test";
		Mashing mashing = Mashing.getInstance();
		mashing.terminate();
		Actuator actuator = new FakeActuator(PhysicalQuantity.TEMPERATURE);
		Sensor sensor = new FakeSensor(actuator);
		Button button = new VirtualButton();
		mashing.initMashing(sensor, actuator, button);
		mashing.setName(test);
		mashing.addRest(new Rest(test, 1d, 1, Boolean.FALSE));
		MashingService ms = new MashingService();
		MashingVO mvo = new MashingVO();
		mvo.setName(name);
		mvo.setAltitude(0);
		mvo.setMeasuredTemperatureBoilingWater(100d);
		mvo.setMeasuredTemperatureIceWater(1d);
		ms.saveName(name, mvo);
		ms.startMashing();
		Thread.sleep(10);
		MashingVO mvoOut = (MashingVO) ms.getMashing().getEntity();
		assertEquals(Mashing.getInstance().getRest().getUuid(), mvoOut.getActiveRest());
		assertEquals(mvo.getMeasuredTemperatureBoilingWater(), mvoOut.getMeasuredTemperatureBoilingWater(), 0.1d);
		assertEquals(mvo.getMeasuredTemperatureIceWater(), mvoOut.getMeasuredTemperatureIceWater(), 0.1d);
		assertEquals(mvo.getAltitude(), mvoOut.getAltitude());
		assertEquals(mvo.getName(), mvoOut.getName());
		assertEquals(Mashing.getInstance().getRest().getUuid(), mvoOut.getActiveRest());
	}

	@Test
	public void testTerminate() throws InterruptedException, IOException {
		String name = "test";
		Mashing mashing = Mashing.getInstance();
		mashing.terminate();
		Actuator actuator = new FakeActuator(PhysicalQuantity.TEMPERATURE);
		Sensor sensor = new FakeSensor(actuator);
		Button button = new VirtualButton();
		mashing.initMashing(sensor, actuator, button);
		mashing.setName(test);
		mashing.addRest(new Rest(test, 1d, 1, Boolean.FALSE));
		MashingService ms = new MashingService();
		ms.startMashing();
		Thread.sleep(10);
		MashingVO mvo = (MashingVO) ms.getMashing().getEntity();
		assertEquals(Mashing.getInstance().getRest().getUuid(), mvo.getActiveRest());
		ms.terminateMashing();
		mvo = (MashingVO) ms.getMashing().getEntity();
		assertEquals(null, mvo.getActiveRest());
	}

}
