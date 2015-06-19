package ch.goodrick.brewcontrol.button;

import java.util.HashSet;

/**
 * This class implements all methods that are similar in most Button
 * implementations. It can be extended by actual real button implementations.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public abstract class AbstractButton implements Button {
	// private Logger log = LoggerFactory.getLogger(this.getClass());
	private HashSet<ButtonListener> listener = new HashSet<ButtonListener>();

	private ButtonState state;

	@Override
	public boolean isOn() {
		return state == ButtonState.ON;
	}

	@Override
	public boolean isOff() {
		return state == ButtonState.OFF;
	}

	@Override
	public ButtonState getState() {
		return state;
	}

	/**
	 * Set a new state for the button.
	 * 
	 * @param state
	 *            the new state for the button
	 */
	protected void setState(ButtonState state) {
		this.state = state;
	}

	@Override
	public boolean isState(ButtonState state) {
		return state == this.state;
	}

	/**
	 * Get all registered listeners as a HashSet.
	 * 
	 * @return all registered listeners.
	 */
	protected HashSet<ButtonListener> getListener() {
		return listener;
	}

	@Override
	public void addListener(ButtonListener... listener) {
		for (ButtonListener bl : listener) {
			this.listener.add(bl);
		}
	}

	@Override
	public void removeListener(ButtonListener... listener) {
		for (ButtonListener bl : listener) {
			this.listener.remove(bl);
		}
	}

	/**
	 * Notifies all registered Listeners of the current state.
	 */
	protected void notifyListeners() {
		for (ButtonListener bl : getListener()) {
			bl.onStateChanged(state);
		}
	}

}
