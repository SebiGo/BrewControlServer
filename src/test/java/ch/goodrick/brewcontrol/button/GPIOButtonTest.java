package ch.goodrick.brewcontrol.button;

import org.junit.Test;

import com.pi4j.io.gpio.RaspiPin;

public class GPIOButtonTest {

	@Test(expected = UnsatisfiedLinkError.class)
	public void test() {
		new GPIOButton(RaspiPin.GPIO_00);
	}
}
