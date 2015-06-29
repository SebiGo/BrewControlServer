package ch.goodrick.brewcontrol.mashing;

import java.util.Comparator;
import java.util.GregorianCalendar;
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
	private final double tolerance = 0.3; // tolerance in °C

	private int timeIntervalInMS = 1000; // TODO move to config file
	private boolean run = true;

	// first Integer: Delta °C in decidegrees, i.e. 10 = 1°C
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
		// reduce heater to (first value) at (second value) decidegrees before
		// reaching rest temperature
		temperatureAdjust.put(15, 100);
		temperatureAdjust.put(10, 50);
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
				heat(true);
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
		Double startTemperature = temperatureSensor.getTemperature();
		boolean deltaTWasExecuted = (rest.getTemperature() - startTemperature < 10);
		while (run && (rest.getState().equals(RestState.HEATING) || rest.getState().equals(RestState.ACTIVE) || rest.getState().equals(RestState.INACTIVE))) {
			try {
				// measure deltaT 7°C before rest temperature unless difference
				// is less than 10°C
				if (Mashing.getInstance().getHysteresis() < 0 && rest.getTemperature() - startTemperature > 10
						&& temperatureSensor.getTemperature() > rest.getTemperature() - 7d) {
					log.info("Measuring hysteresis.");
					Mashing.getInstance().setHysteresis(hysteresis());
					log.info("Measuring finished, hysteresis is " + Mashing.getInstance().getHysteresis() + "°C.");
				}

				// Switch off if we reached rest temperature less deltaT.
				if (!deltaTWasExecuted && temperatureSensor.getTemperature() > rest.getTemperature() - Mashing.getInstance().getHysteresis()) {
					log.info("Switching off heater since we will reach rest temperature according to hysteresis measurement.");
					deltaTWasExecuted = true;
					// use the hysteresis method and update the hysteresis value
					// while we're at it.
					Mashing.getInstance().setHysteresis(hysteresis());
					log.info("Start pulsing heater.");
				}
				setStatus();
				heat(deltaTWasExecuted);
			} catch (InterruptedException e) {
				log.error("Heating adjustment was interrupted!");
			}
		}
	}

	/**
	 * this method switches off the heater and measures how much the temperature
	 * increases "deltaT".
	 * 
	 * @return how much temperature is added once the heater is switched off.
	 * @throws InterruptedException
	 */
	private Double hysteresis() throws InterruptedException {
		Double temperature = temperatureSensor.getTemperature();
		Double initialTemperature = temperature;
		heater.off();
		// we go slower than usual otherwise we might get fooled by too fast
		// readings
		Thread.sleep(timeIntervalInMS * 10);
		while (true) {
			if (temperatureSensor.getTemperature() <= temperature) {
				// we've reached the peak, return result.
				return temperature - initialTemperature;
			}
			temperature = temperatureSensor.getTemperature();
			Thread.sleep(timeIntervalInMS * 10);
		}
	}

	/**
	 * Switch on heater for a percentage of the given time
	 */
	private void heat(boolean deltaTWasExecuted) throws InterruptedException {
		// just heat if deltaT was not yet executed.
		if (!deltaTWasExecuted) {
			heater.on();
			Thread.sleep(timeIntervalInMS);
			return;
		}
		// start pulsing
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
	 * Terminate the current rest
	 */
	public void terminate() {
		run = false;
	}
}
