package ch.goodrick.brewcontrol.sensor;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import ch.goodrick.brewcontrol.actuator.Actuator;
import ch.goodrick.brewcontrol.actuator.FakeActuator;
import ch.goodrick.brewcontrol.common.PhysicalQuantity;

public class SensorThreadTest {

	@Test
	public void test() throws IOException, InterruptedException {
		Actuator a = new FakeActuator(PhysicalQuantity.TEMPERATURE);
		a.on();
		CalibratedTemperatureSensor fs = new CalibratedTemperatureSensor() {
			@Override
			public Double getValue() throws IOException {
				return 25d;
			}
			@Override
			public String getID() {
				return "local";
			}
		};
		final Set<Double> checkListener = new HashSet<Double>();
		final Set<Double> checkAboveListener = new HashSet<Double>();
		final Set<Double> checkBelowListener = new HashSet<Double>();
		SensorThread st = SensorThread.startTemperatureThread(1, new FakeSensor(a));
		TemperatureChangeListenerInterface sl = new TemperatureChangeListenerInterface() {
			@Override
			public void onStateChangedEvent(Double value) {
				checkListener.add(value);
			}
		};
		st.addListener(sl);
		st.addListenerAbove(new TemperatureChangeListenerInterface() {
			@Override
			public void onStateChangedEvent(Double value) {
				checkAboveListener.add(value);
			}
		}, Double.MIN_VALUE);
		st.addListenerAbove(new TemperatureChangeListenerInterface() {
			@Override
			public void onStateChangedEvent(Double value) {
				checkAboveListener.add(value);
			}
		}, Double.MAX_VALUE);
		st.addListenerAbove(new TemperatureChangeListenerInterface() {
			@Override
			public void onStateChangedEvent(Double value) {
				checkAboveListener.add(value);
			}
		}, Double.MAX_VALUE);
		st.addListenerBelow(new TemperatureChangeListenerInterface() {
			@Override
			public void onStateChangedEvent(Double value) {
				checkBelowListener.add(value);
			}
		}, Double.MIN_VALUE);
		st.addListenerBelow(new TemperatureChangeListenerInterface() {
			@Override
			public void onStateChangedEvent(Double value) {
				checkBelowListener.add(value);
			}
		}, Double.MAX_VALUE);
		Thread.sleep(1000);
		assertTrue(fs.getValue() <= checkListener.iterator().next());
		System.out.println("size "+checkAboveListener.size()+" - "+checkBelowListener.size());
		assertTrue(checkBelowListener.size() > 0);
		assertTrue(checkBelowListener.size() > 0);
		st.removeListener(sl);
		st.clearThresholdListener();
		checkListener.clear();
		checkAboveListener.clear();
		checkBelowListener.clear();
		Thread.sleep(2);
		assertTrue(checkListener.isEmpty());
		assertTrue(checkAboveListener.isEmpty());
		assertTrue(checkBelowListener.isEmpty());
		st.teminate();
	}

}
