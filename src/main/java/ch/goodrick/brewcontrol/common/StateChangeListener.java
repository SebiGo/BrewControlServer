package ch.goodrick.brewcontrol.common;

import java.util.HashSet;
import java.util.Set;

/**
 * This class implements a generic state change listener controller.
 * 
 * @author sebastian@goodrick.ch
 *
 * @param <E>
 *            the ListenerInterface that Listeners need to implement
 * @param <F>
 *            the event class that contains more information on the status
 *            event.
 */
public abstract class StateChangeListener<E extends StateChangeListenerInterface<F>, F> {
	private final Set<E> listener = new HashSet<E>();

	/**
	 * Add a listener to be notified on every rest state change.
	 * 
	 * @param listener
	 *            the listener to be notified.
	 */
	@SuppressWarnings("unchecked")
	public void addListener(E... listener) {
		for (E l : listener) {
			this.listener.add(l);
		}
	}

	/**
	 * Notify all listeners about an event.
	 * 
	 * @param event
	 *            the event
	 */
	public void notifyListeners(F event) {
		// notify all regular listeners

		// Avoid ConcurrentModificationException, copy to Array
		@SuppressWarnings("unchecked")
		E[] myListenerArray = (E[]) listener.toArray();

		for (E listener : myListenerArray) {
			listener.onStateChangedEvent(event);
		}
	}

	/**
	 * Remove a certain listener. Note: You will still get notified of any
	 * events that occurred before removing the listener.
	 * 
	 * @param listener
	 *            the listener to be removed.
	 */
	@SuppressWarnings("unchecked")
	public void removeListener(E... listener) {
		for (E bl : listener) {
			this.listener.remove(bl);
		}
	}

	/**
	 * Delete all listeners.
	 */
	public void clearListeners() {
		listener.clear();
	}

}
