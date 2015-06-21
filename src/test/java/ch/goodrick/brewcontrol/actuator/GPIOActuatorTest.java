package ch.goodrick.brewcontrol.actuator;

import org.junit.Test;

import ch.goodrick.brewcontrol.common.PhysicalQuantity;

import com.pi4j.io.gpio.RaspiPin;

public class GPIOActuatorTest {

	@Test(expected=UnsatisfiedLinkError.class)
	public void test() {
		GPIOActuator gpioa = new GPIOActuator(RaspiPin.GPIO_04, PhysicalQuantity.TEMPERATURE);
	}

}
