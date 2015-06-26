package ch.goodrick.brewcontrol.mashing;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.goodrick.brewcontrol.actuator.Actuator;
import ch.goodrick.brewcontrol.actuator.FakeActuator;
import ch.goodrick.brewcontrol.common.StateChangeListenerInterface;
import ch.goodrick.brewcontrol.common.PhysicalQuantity;
import ch.goodrick.brewcontrol.sensor.FakeSensor;
import ch.goodrick.brewcontrol.sensor.Sensor;
import ch.goodrick.brewcontrol.sensor.SensorThread;

public class RestExecuterTest {

	@Test
	public void test() {
		Rest rest = new Rest("", 21d, 1, true);
		Actuator heater = new FakeActuator(PhysicalQuantity.TEMPERATURE);
		Sensor sensor = new FakeSensor(heater);
		SensorThread temperatureSensor = SensorThread.startTemperatureThread(1, sensor);
		RestExecuter re = new RestExecuter(rest, heater, temperatureSensor, new StateChangeListenerInterface<RestState>() {
			@Override
			public void onStateChangedEvent(RestState state) {
				fail("too early!");
			}
		});
		(new Thread(re)).start();
		re.terminate();
		// TODO can we make some assertions?
	}

}
