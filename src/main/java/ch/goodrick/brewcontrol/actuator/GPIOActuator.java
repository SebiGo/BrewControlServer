package ch.goodrick.brewcontrol.actuator;

import ch.goodrick.brewcontrol.common.PhysicalQuantity;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

/**
 * A representation of a Raspberry Pi GPIO pin connected actuator. Please make
 * sure you select a GPIO Pin that is available (e.g. not configured as an SPI
 * channel).
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class GPIOActuator extends AbstractActuator implements Actuator {
	// create gpio controller
	final GpioController gpio = GpioFactory.getInstance();

	// provision gpio pin #01 as an output pin and turn on
	final GpioPinDigitalOutput pin;

	/**
	 * Constructs a new GPIO pin connected Actuator.
	 * 
	 * @param pin
	 *            the GPIO pin used for this acuator.
	 * @param physicalQuantity
	 *            the physical quantity this acutator regulates.
	 */
	public GPIOActuator(Pin pin, PhysicalQuantity physicalQuantity) {
		super(physicalQuantity);
		this.pin = gpio.provisionDigitalOutputPin(pin, "MyLED", PinState.LOW);
		this.pin.setShutdownOptions(true, PinState.LOW);
	}

	@Override
	public void on() {
		pin.high();
		super.on();
	}

	@Override
	public void off() {
		pin.low();
		super.off();
	}

	@Override
	public PhysicalQuantity getPhysicalQuantity() {
		return PhysicalQuantity.TEMPERATURE;
	}
}
