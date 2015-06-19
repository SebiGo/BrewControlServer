package ch.goodrick.brewcontrol.button;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * A representation of a Raspberry Pi GPIO pin connected button. Please make
 * sure you select a GPIO Pin that is available (e.g. not configured as an SPI
 * channel). The button needs to be connected between the GPIO pin you use for
 * initialising the object and 3.3V (Pin 1 on RaspberryPi Model B).
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class GPIOButton extends AbstractButton implements Button {

	/**
	 * Constructing a software representation of an GPIO connected hardware
	 * button.
	 * 
	 * @param pin
	 *            the GPIO pin that the button is connected to.
	 */
	public GPIOButton(Pin pin) {
		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();

		// provision gpio pin as an input pin with its internal pull down
		// resistor enabled
		final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(pin, PinPullResistance.PULL_DOWN);

		// register an anonymous listener with the pi4j library to connect to
		// our internal listeners.
		myButton.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				event.getState();
				if (event.getState() == PinState.HIGH) {
					setState(ButtonState.ON);
				} else {
					setState(ButtonState.OFF);
				}
				notifyListeners();
			}
		});
	}
}
