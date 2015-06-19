package ch.goodrick.brewcontrol.sensor;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A thread for constantly reading sensor values and dispatching it to
 * registered listeners.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class SensorThread implements Runnable, SensorListener {
	private double temperature;
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private int interval;
	private Sensor sensor;
	private HashSet<SensorListener> listener = new HashSet<SensorListener>();
	private HashMap<Double, HashSet<SensorListener>> listenerAbove = new HashMap<Double, HashSet<SensorListener>>();
	private HashMap<Double, HashSet<SensorListener>> listenerBelow = new HashMap<Double, HashSet<SensorListener>>();
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

				// notify all regular listeners
				for (SensorListener l : listener) {
					l.onSensorEvent(value);
				}

				// notify all above listeners
				for (Double aboveValue : listenerAbove.keySet()) {
					if (value >= aboveValue) {
						for (SensorListener listener : listenerAbove.get(aboveValue)) {
							listener.onSensorEvent(value);
						}
					}
				}
				// notify all below listeners
				for (Double belowValue : listenerBelow.keySet()) {
					if (value < belowValue) {
						for (SensorListener listener : listenerBelow.get(belowValue)) {
							listener.onSensorEvent(value);
						}
					}
				}
				log.debug(sensor.getID() + ": " + value + "" + sensor.getPhysicalQuantity().getUnit());

				Thread.sleep(interval);
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
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
	public void addListenerAbove(SensorListener listener, Double exceedValue) {
		if (listenerAbove.containsKey(exceedValue)) {
			listenerAbove.get(exceedValue).add(listener);
		} else {
			HashSet<SensorListener> set = new HashSet<SensorListener>();
			set.add(listener);
			listenerAbove.put(exceedValue, set);
		}
	}

	/**
	 * Add a listener to be notified once the value is below a given value.
	 * 
	 * @param listener
	 *            the listener to be notified
	 * @param exceedValue
	 *            the value below which the listener gets called.
	 */
	public void addListenerBelow(SensorListener listener, Double exceedValue) {
		if (listenerBelow.containsKey(exceedValue)) {
			listenerBelow.get(exceedValue).add(listener);
		} else {
			HashSet<SensorListener> set = new HashSet<SensorListener>();
			set.add(listener);
			listenerBelow.put(exceedValue, set);
		}
	}

	/**
	 * Add a listener to be notified on every temperature value that is produced
	 * by the temperature thread.
	 * 
	 * @param listener
	 *            the listener to be notified.
	 */
	public void addListener(SensorListener... listener) {
		for (SensorListener l : listener) {
			this.listener.add(l);
		}
	}

	/**
	 * Remove a listener for all permanent listeners (i.e. not above and below
	 * values).
	 * 
	 * @param listener
	 *            the listener to be removed.
	 */
	public void removeListener(SensorListener... listener) {
		for (SensorListener l : listener) {
			this.listener.remove(l);
		}
	}

	/**
	 * Delete all threshold listeners (i.e. above and below values).
	 */
	public void clearThresholdListener() {
		listenerAbove = new HashMap<Double, HashSet<SensorListener>>();
		listenerBelow = new HashMap<Double, HashSet<SensorListener>>();
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
	public void onSensorEvent(Double value) {
		temperature = value;
	}

}
