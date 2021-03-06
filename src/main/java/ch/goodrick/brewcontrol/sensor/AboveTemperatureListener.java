package ch.goodrick.brewcontrol.sensor;

/**
 * This class lets you register listeners that get called whenever a temperature
 * is on or above a certain value.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class AboveTemperatureListener extends ParameterisedTemperatureListener {

	/**
	 * Notifies all registered listeners if the temperature is on or above value
	 * 
	 * @param value
	 *            the current reading of the temperature
	 */
	@Override
	public void notifyListeners(Double value) {
		for (Double aboveValue : getListeners().keySet()) {
			if (value <= aboveValue) {
				for (TemperatureChangeListenerInterface listener : getListeners().get(aboveValue)) {
					listener.onStateChangedEvent(value);
				}
			}
		}
	}

}
