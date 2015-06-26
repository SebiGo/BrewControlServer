package ch.goodrick.brewcontrol.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.UUID;

import org.junit.Test;

import ch.goodrick.brewcontrol.actuator.Actuator;
import ch.goodrick.brewcontrol.actuator.FakeActuator;
import ch.goodrick.brewcontrol.button.Button;
import ch.goodrick.brewcontrol.button.FakeButton;
import ch.goodrick.brewcontrol.button.VirtualButton;
import ch.goodrick.brewcontrol.common.PhysicalQuantity;
import ch.goodrick.brewcontrol.mashing.Mashing;
import ch.goodrick.brewcontrol.mashing.MashingException;
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
		assertEquals(mvo.getTemperature(), mvoOut.getTemperature(), 0.01d);
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

	@Test
	public void testContinueMashing() throws IOException {
		Mashing mashing = Mashing.getInstance();
		mashing.terminate();
		MashingService ms = new MashingService();
		ms.continueMashing();

	}

	@Test
	public void testSaveName() {
		MashingService ms = new MashingService();
		MashingVO mvo = new MashingVO();
		mvo.setName("new");
		mvo.setAltitude(0);
		mvo.setMeasuredTemperatureBoilingWater(100d);
		mvo.setMeasuredTemperatureIceWater(1d);
		ms.saveName(mvo.getName(), mvo);
		assertEquals(mvo.getName(), ((MashingVO) (ms.getMashing().getEntity())).getName());
	}

	@Test
	public void testNewName() {
		MashingService ms = new MashingService();
		MashingVO mvo = new MashingVO();
		mvo.setName("new");
		mvo.setAltitude(0);
		mvo.setMeasuredTemperatureBoilingWater(100d);
		mvo.setMeasuredTemperatureIceWater(1d);
		ms.newName(mvo);
		assertEquals(mvo.getName(), ((MashingVO) (ms.getMashing().getEntity())).getName());
	}
	
	@Test
	public void testDeleteRest() {
		MashingService ms = new MashingService();
		ms.deleteRest(UUID.randomUUID());
		assertEquals(HttpURLConnection.HTTP_BAD_METHOD, ms.deleteRest(UUID.randomUUID()).getStatus());
	}

	@Test
	public void testStartDublicateMashing() {
		Mashing mashing = Mashing.getInstance();
		mashing.addRest(new Rest(test, 1d, 1000, Boolean.FALSE));

		MashingService ms = new MashingService();
		ms.startMashing();

		assertEquals(HttpURLConnection.HTTP_UNAVAILABLE, ms.startMashing().getStatus());
	}

	@Test(expected = MashingException.class)
	public void testGetGraph1() throws MashingException {
		Mashing mashing = Mashing.getInstance();
		mashing.addRest(new Rest(test, 1d, 1000, Boolean.FALSE));
		mashing.startMashing();
		MashingService ms = new MashingService();
		assertNotNull(ms.getGraph());
	}

	@Test
	public void testGetGraph2() throws IOException {
		Mashing mashing = Mashing.getInstance();
		FakeActuator a = new FakeActuator(PhysicalQuantity.TEMPERATURE);
		mashing.initMashing(new FakeSensor(a), a, new FakeButton());
		mashing.addRest(new Rest(test, 1d, 1000, Boolean.FALSE));
		try {
			mashing.startMashing();
		} catch (MashingException e) {
			// ignore
		}
		MashingService ms = new MashingService();
		assertNotNull(ms.getGraph());
	}
}
