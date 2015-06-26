package ch.goodrick.brewcontrol.sensor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A thread for constantly reading sensor values and dispatching it to
 * registered listeners.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class SensorThread extends TemperatureListener implements Runnable, TemperatureChangeListenerInterface {
	private double temperature;
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private int interval;
	private Sensor sensor;
	private AboveTemperatureListener listenerAbove = new AboveTemperatureListener();
	private BelowTemperatureListener listenerBelow = new BelowTemperatureListener();
	private boolean run = true;

	/**
	 * The thread run() class. Don't call directly, use the constructor to start
	 * the thread.
	 */
	@Override
	public void run() {
		try {
			while (run) {

				Double value = sensor.getValue();
				notify(value);

				log.debug(sensor.getID() + ": " + value + "" + sensor.getPhysicalQuantity().getUnit());

				Thread.sleep(interval);
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}

	private void notify(Double value) {
		// notify all listeners
		notifyListeners(value);
		listenerAbove.notifyListeners(value);
		listenerBelow.notifyListeners(value);
	}

	private SensorThread(int sleep, Sensor sensor) {
		this.interval = sleep;
		this.sensor = sensor;
	}

	/**
	 * Retrieve last temperature sample from sensor
	 * 
	 * @return
	 */
	public double getTemperature() {
		return temperature;
	}

	/**
	 * Add a listener to be notified once the value is above a given value.
	 * 
	 * @param listener
	 *            the listener to be notified
	 * @param exceedValue
	 *            the value above which the listener gets called.
	 */
	public void addListenerAbove(TemperatureChangeListenerInterface listener, Double exceedValue) {
		listenerAbove.addListener(listener, exceedValue);
	}

	/**
	 * Add a listener to be notified once the value is below a given value.
	 * 
	 * @param listener
	 *            the listener to be notified
	 * @param exceedValue
	 *            the value below which the listener gets called.
	 */
	public void addListenerBelow(TemperatureChangeListenerInterface listener, Double exceedValue) {
		listenerBelow.addListener(listener, exceedValue);
	}

	/**
	 * Delete all threshold listeners (i.e. above and below values).
	 */
	public void clearThresholdListener() {
		listenerAbove.clearListener();
		listenerBelow.clearListener();
	}

	/**
	 * Constructs a new sensor thread that polls the sensor regularly.
	 * 
	 * @param interval
	 *            the poll interval in seconds.
	 * @param sensor
	 *            the sensor to be polled.
	 * @return
	 */
	public static SensorThread startTemperatureThread(int interval, Sensor sensor) {
		SensorThread thread = new SensorThread(interval, sensor);
		thread.addListener(thread);
		(new Thread(thread)).start();
		return thread;
	}

	/**
	 * Terminate the thread.
	 */
	public void teminate() {
		run = false;
	}

	@Override
	public void onStateChangedEvent(Double value) {
		temperature = value;
	}

}
