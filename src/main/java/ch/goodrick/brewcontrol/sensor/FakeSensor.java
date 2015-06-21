package ch.goodrick.brewcontrol.sensor;

import java.io.IOException;

import ch.goodrick.brewcontrol.actuator.Actuator;
import ch.goodrick.brewcontrol.actuator.ActuatorStatus;

/**
 * This sensor is for developing and simulating purposes only. It connects to
 * the FakeActuator and with every read it in- or decreases its value by 0.1
 * depending on the fake actuator status.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class FakeSensor extends CalibratedTemperatureSensor {

	private Double temperature = 20d;
	private Actuator actuator;

	public FakeSensor(Actuator actuator) {
		this.actuator = actuator;
	}

	@Override
	public String getID() {
		return "FAKE_SENSOR";
	}

	@Override
	public Double getValue() throws IOException {
		if (actuator.getStatus() == ActuatorStatus.ON) {
			temperature += 0.1d;
		} else {
			temperature -= 0.001d;
		}
		return getCalibratedValue(temperature);
	}
}
