package ch.goodrick.brewcontrol.button;

/**
 * The generic button interface.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public interface Button {

	/**
	 * On Status of the button.
	 * 
	 * @return true if the button is currently on (i.e. pressed)
	 */
	boolean isOn();

	/**
	 * Off Status of the button.
	 * 
	 * @return true if the button is currently off (i.e. not pressed)
	 */
	boolean isOff();

	/**
	 * Get the state of the button.
	 * 
	 * @return ButtonStatus for the button.
	 */
	ButtonState getState();

	/**
	 * Queries the state of the button.
	 * 
	 * @param state
	 *            the state to check the Button for.
	 * @return true if the state equals the submitted state.
	 */
	boolean isState(ButtonState state);

	/**
	 * Adds a button listener to the button. The Listener is called on every
	 * state change.
	 * 
	 * @param listener
	 *            the listener to be called back.
	 */
	void addListener(ButtonListener... listener);

	/**
	 * Removes the listener from the button.
	 * 
	 * @param listener
	 *            the listener to be removed and no longer called.
	 */
	void removeListener(ButtonListener... listener);
}
