package ch.goodrick.brewcontrol.common;

/**
 * This class implements a generic state change ListenerInterface
 * 
 * @author sebastian@goodrick.ch
 *
 * @param <E>
 *            The event class that contains more information on the event.
 */
public interface StateChangeListenerInterface<E> {
	/**
	 * This method gets called if an event has occurred.
	 * 
	 * @param state
	 */
	public void onStateChangedEvent(E state);
}
