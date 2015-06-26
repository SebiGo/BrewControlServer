package ch.goodrick.brewcontrol.sensor;

/**
 * This class lets you register listeners that get called whenever a temperature
 * is on or below a certain value.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class BelowTemperatureListener extends ParameterisedTemperatureListener {

	/**
	 * Notifies all registered listeners if the temperature is on or below value
	 * 
	 * @param value
	 *            the current reading of the temperature
	 */
	@Override
	public void notifyListeners(Double value) {
		for (Double belowValue : getListeners().keySet()) {
			if (value >= belowValue) {
				for (TemperatureChangeListenerInterface listener : getListeners().get(belowValue)) {
					listener.onStateChangedEvent(value);
				}
			}
		}
	}

}
