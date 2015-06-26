package ch.goodrick.brewcontrol.mashing;

import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.goodrick.brewcontrol.actuator.Actuator;
import ch.goodrick.brewcontrol.actuator.FakeActuator;
import ch.goodrick.brewcontrol.common.StateChangeListener;
import ch.goodrick.brewcontrol.common.StateChangeListenerInterface;
import ch.goodrick.brewcontrol.sensor.SensorThread;

/**
 * This class executes a rest i.e. heats up to the expected temperature and
 * waits for the defined time.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class RestExecuter extends StateChangeListener<StateChangeListenerInterface<RestState>, RestState> implements Runnable {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Rest rest;
	private final Actuator heater;
	private final SensorThread temperatureSensor;
	private int timeIntervalInMS = 1000; // TODO move to config file
	private final double tolerance = 0.3; // tolerance in °C
	private boolean run = true;

	// first Integer: Delta °C in centidegrees, i.e. 10 = 1°C
	// second Integer: % of heating time
	private SortedMap<Integer, Integer> temperatureAdjust;

	@SafeVarargs
	public RestExecuter(Rest rest, Actuator heater, SensorThread temperatureSensor, StateChangeListenerInterface<RestState>... listener) {
		this.rest = rest;
		this.heater = heater;
		this.temperatureSensor = temperatureSensor;
		addListener(listener);

		// adjust timing in simulation mode
		if (heater instanceof FakeActuator) {
			timeIntervalInMS = 5000;
		}

		temperatureAdjust = new TreeMap<Integer, Integer>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}
		});

		// set up temperature adjustment
		// reduce heater to (first value) at (second value) centiDegrees before
		// reaching rest temperature
		// TODO move to config file
		temperatureAdjust.put(25, 100);
		temperatureAdjust.put(15, 50);
		temperatureAdjust.put(10, 35);
		temperatureAdjust.put(5, 25);
		temperatureAdjust.put(1, 20);
		temperatureAdjust.put(0, 15);
		temperatureAdjust.put(-1, 0);
	}

	@Override
	public void run() {
		// perform the heat cycle
		heatCycle();
	}

	/**
	 * This method executes a heat cycle for a rest.
	 */
	protected void heatCycle() {
		log.info(rest.getName() + " is starting.");

		// keep running in three states
		heatRunnerActive();

		// notify listeners that rest is over
		notifyListeners(rest.getState());

		// keep up heating while status is WAITING_COMPLETE
		heatRunnerComplete();

		// remove all listeners before finishing
		clearListeners();

		if (run) {
			log.info("Rest " + rest.getName() + " has been finished.");
		} else {
			log.info("Rest " + rest.getName() + " has been forcefully terminated.");
		}
	}

	/**
	 * This method keeps running while the rest is in state WAITING_COMPLETE.
	 */
	protected void heatRunnerComplete() {
		while (run && rest.getState().equals(RestState.WAITING_COMPLETE)) {
			try {
				heat();
			} catch (InterruptedException e) {
				log.error("Heating adjustment was interrupted!");
			}
		}
	}

	/**
	 * this method keeps running while the rest is in the three states HEATING,
	 * ACTIVE and INACTIVE.
	 */
	protected void heatRunnerActive() {
		while (run && (rest.getState().equals(RestState.HEATING) || rest.getState().equals(RestState.ACTIVE) || rest.getState().equals(RestState.INACTIVE))) {

			try {
				setStatus();
				heat();
			} catch (InterruptedException e) {
				log.error("Heating adjustment was interrupted!");
			}
		}
	}

	/**
	 * Switch on heater for a percentage of the given time
	 */
	private void heat() throws InterruptedException {
		Integer delta = new Double((rest.getTemperature() - temperatureSensor.getTemperature()) * 10).intValue();
		for (Integer rTemp : temperatureAdjust.keySet()) {
			if (delta > rTemp) {
				Integer percent = new Double(new Double(temperatureAdjust.get(rTemp)) / 100 * timeIntervalInMS).intValue();
				heater.off();
				Thread.sleep(timeIntervalInMS - percent);
				heater.on();
				Thread.sleep(percent);
				return;
			}
		}
		// we are above all limits, switch heater off and wait!
		heater.off();
		Thread.sleep(timeIntervalInMS);
	}

	/**
	 * Sets the status of the rest according to what is happening.
	 */
	private void setStatus() {
		// adjust the status of the rest
		if (rest.getState().equals(RestState.INACTIVE)) {
			// set status to heating
			rest.setState(RestState.HEATING);
			log.info(rest.getName() + " is now in state " + rest.getState());
		} else if (rest.getState().equals(RestState.HEATING) && temperatureSensor.getTemperature() >= (rest.getTemperature() - tolerance)) {
			// set status to active
			rest.setState(RestState.ACTIVE);
			log.info(rest.getName() + " is now in state " + rest.getState());
		} else if (rest.getState().equals(RestState.ACTIVE)
				&& (new GregorianCalendar().getTimeInMillis() - rest.getActive().getTimeInMillis()) / 1000 / 60 >= rest.getDuration()) {
			// time is up :)
			if (rest.isContinueAutomatically()) {
				rest.setState(RestState.COMPLETED);
			} else {
				rest.setState(RestState.WAITING_COMPLETE);
			}
			log.info(rest.getName() + " is now in state " + rest.getState());
		}
	}

	/**
	 * Add a listener to be notified on every rest state change.
	 * 
	 * @param listener
	 *            the listener to be notified.
	 */


	public void terminate() {
		run = false;
	}
}
