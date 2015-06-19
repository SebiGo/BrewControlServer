package ch.goodrick.brewcontrol.sensor;

/**
 * Interface for any sensor listeners.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public interface SensorListener {
	/**
	 * This method will be called if any sensor event occurs.
	 * 
	 * @param value
	 */
	public void onSensorEvent(Double value);
}
