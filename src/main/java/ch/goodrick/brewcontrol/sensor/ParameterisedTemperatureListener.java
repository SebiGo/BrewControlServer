package ch.goodrick.brewcontrol.sensor;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class implements all common classes for listeners that should get called
 * in relation to a registered temperature value.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public abstract class ParameterisedTemperatureListener {
	private HashMap<Double, HashSet<TemperatureChangeListenerInterface>> listeners = new HashMap<Double, HashSet<TemperatureChangeListenerInterface>>();

	/**
	 * Add a listener to be notified once the value is above a given value.
	 * 
	 * @param listener
	 *            the listener to be notified
	 * @param exceedValue
	 *            the value above which the listener gets called.
	 */
	public void addListener(TemperatureChangeListenerInterface listener, Double exceedValue) {
		if (listeners.containsKey(exceedValue)) {
			listeners.get(exceedValue).add(listener);
		} else {
			HashSet<TemperatureChangeListenerInterface> set = new HashSet<TemperatureChangeListenerInterface>();
			set.add(listener);
			listeners.put(exceedValue, set);
		}
	}

	/**
	 * Delete all listeners (i.e. above and below values).
	 */
	public void clearListener() {
		listeners.clear();
	}

	/**
	 * Get all listeners.
	 * 
	 * @return a HashHap containing all Listeners (in another Hashmap)
	 *         registered for all registered Temperatures.
	 */
	HashMap<Double, HashSet<TemperatureChangeListenerInterface>> getListeners() {
		return listeners;
	}

	/**
	 * notify all listeners, to be implemented by concrete classes.
	 */
	public abstract void notifyListeners(Double value);
}
