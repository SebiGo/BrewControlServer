package ch.goodrick.brewcontrol.button;

/**
 * The abstract button class.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public abstract class Button extends ButtonListener {

	private ButtonState state;

	/**
	 * On Status of the button.
	 * 
	 * @return true if the button is currently on (i.e. pressed)
	 */
	boolean isOn() {
		return state == ButtonState.ON;
	}

	/**
	 * Off Status of the button.
	 * 
	 * @return true if the button is currently off (i.e. not pressed)
	 */
	boolean isOff() {
		return state == ButtonState.OFF;
	}

	/**
	 * Get the state of the button.
	 * 
	 * @return ButtonStatus for the button.
	 */
	ButtonState getState() {
		return state;
	}

	/**
	 * Set the state of the button.
	 * 
	 * @return ButtonStatus for the button.
	 */
	void setState(ButtonState state) {
		this.state = state;
		notifyListeners(state);
	}

	/**
	 * Queries the state of the button.
	 * 
	 * @param state
	 *            the state to check the Button for.
	 * @return true if the state equals the submitted state.
	 */
	boolean isState(ButtonState state) {
		return state == this.state;
	}

	/**
	 * Notifies listeners of current button state.
	 */
	void notifyListeners() {
		super.notifyListeners(state);
	}
}
