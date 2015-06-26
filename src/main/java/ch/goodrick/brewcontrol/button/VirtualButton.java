package ch.goodrick.brewcontrol.button;

/**
 * A virtual button that can be clicked by software. It can be used by a service
 * or as an internal way to communicate with any button consuming classes.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class VirtualButton extends Button {

	/**
	 * Triggers a click on a button (i.e. a fast on and off).
	 */
	public void click() {
		on();
		off();
	}

	/**
	 * switches the button on and notifies listeners.
	 */
	public void on() {
		setState(ButtonState.ON);
	}

	/**
	 * switches the button on and notifies listeners.
	 */
	public void off() {
		setState(ButtonState.OFF);
	}
}
