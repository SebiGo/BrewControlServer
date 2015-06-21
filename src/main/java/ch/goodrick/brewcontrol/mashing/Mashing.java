package ch.goodrick.brewcontrol.mashing;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.goodrick.brewcontrol.actuator.Actuator;
import ch.goodrick.brewcontrol.button.Button;
import ch.goodrick.brewcontrol.button.ButtonListener;
import ch.goodrick.brewcontrol.button.ButtonState;
import ch.goodrick.brewcontrol.button.VirtualButton;
import ch.goodrick.brewcontrol.logger.RRD;
import ch.goodrick.brewcontrol.sensor.Sensor;
import ch.goodrick.brewcontrol.sensor.SensorListener;
import ch.goodrick.brewcontrol.sensor.SensorThread;

/**
 * This is the central class for the mashing process. It executes rests and
 * connects to sensors, actuators and buttons.
 * <p>
 * This class is a singleton.
 * 
 * @author sebastian@goodrick.ch
 */
public class Mashing {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private VirtualButton virtualButton = new VirtualButton();
	private static Mashing instance;
	private String name = "BrewControl";
	private boolean active = false;
	private RRD tempLogger;
	private double currentTemperature;

	private Rest firstRest = null;
	private Sensor temperatureSensor;
	private Actuator actuator;
	private Button[] buttons;
	private SensorThread tempThread;
	private List<RestExecuter> threads = new Vector<RestExecuter>();

	public double getCurrentTemperature() {
		return currentTemperature;
	}

	private void setCurrentTemperature(double currentTemperature) {
		this.currentTemperature = currentTemperature;
	}

	public RRD getTempLogger() {
		return tempLogger;
	}

	public void setTempLogger(RRD tempLogger) {
		this.tempLogger = tempLogger;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Actuator getActuator() {
		return actuator;
	}

	public Rest getFirstRest() {
		return firstRest;
	}

	public void setFirstRest(Rest firstRest) {
		this.firstRest = firstRest;
	}

	public Sensor getTemperatureSensor() {
		return temperatureSensor;
	}

	private void setTemperatureSensor(Sensor sensor) {
		temperatureSensor = sensor;
	}

	private Mashing() {
	}

	/**
	 * Get the instance of this singleton.
	 * 
	 * @return
	 */
	public static Mashing getInstance() {
		if (instance == null) {
			instance = new Mashing();
		}
		return instance;
	}

	/**
	 * Adds a rest to the mashing process. The new rest will end up as the last
	 * rest.
	 * 
	 * @param rest
	 *            the rest to be added
	 */
	public void addRest(Rest rest) {
		if (getFirstRest() == null) {
			setFirstRest(rest);
		} else {
			Rest currentRest = getFirstRest();
			while (currentRest.getNextRest() != null) {
				currentRest = currentRest.getNextRest();
			}
			currentRest.setNextRest(rest);
		}
	}

	/**
	 * This method initialises the singleton mashing object in one go with the
	 * most relevant parameters required for the mashing process.
	 * 
	 * @param sensor
	 *            the sensor used for temperature readings in the mashing
	 *            process
	 * @param actuator
	 *            the acuator used to switch on the heating device in the
	 *            mashing process
	 * @param buttons
	 *            the buttons used to continue the mashing process
	 * @throws IOException
	 */
	public void initMashing(Sensor sensor, Actuator actuator, Button... buttons) throws IOException {
		setTemperatureSensor(sensor);
		this.actuator = actuator;
		this.buttons = buttons;

		// start temperatureThread & log temperatures
		tempThread = SensorThread.startTemperatureThread(1000, getTemperatureSensor());
		tempLogger = new RRD(name, getTemperatureSensor().getPhysicalQuantity());
		tempThread.addListener(new SensorListener() {
			@Override
			public void onSensorEvent(Double value) {
				tempLogger.log(value);
				setCurrentTemperature(value);
			}
		});
	}

	/**
	 * This method is used to start the mashing process.
	 * 
	 * @throws MashingException
	 *             if there is any problem with the mashing process itself.
	 * @throws IOException
	 *             if there is a problem with reading the sensor.
	 */
	public void startMashing() throws MashingException {

		// check if we were properly initialised
		if (getTemperatureSensor() == null || getActuator() == null || buttons == null) {
			throw new MashingException("Mashing is not yet ready for execution, supply sensor, acutator and button");
		}

		// only start if mashing is inactive!
		if (active) {
			throw new MashingAlreadyActiveException();
		}
		active = true;

		// reset RRD database
		try {
			tempLogger = new RRD(name, getTemperatureSensor().getPhysicalQuantity());
		} catch (IOException e) {
			log.warn("Could not reset RRD temperature logger database.");
		}

		executeRest(getFirstRest(), actuator, buttons);
	}

	/**
	 * Internal method for executing the mashing process.
	 * 
	 * @param rest
	 * @param tempThread
	 * @param heater
	 * @param buttons
	 * @throws IllegalRestStateException
	 */
	private void executeRest(final Rest rest, final Actuator heater, final Button... buttons) throws IllegalRestStateException {

		// stop executing after last rest
		if (rest == null) {
			active = false;
			threads.clear();
			return;
		}

		// execute the rest
		RestExecuter re = new RestExecuter(rest, heater, tempThread, new RestStateChangeListener() {
			@Override
			public void onStateChangedEvent(RestState state) {
				if (state.equals(RestState.WAITING_COMPLETE)) {
					// wait for buttons to be pressed, do some
					// "button magic" (nice word for bad code)
					// and enable listeners on the buttons
					Button[] myButtons = new Button[buttons.length + 1];
					System.arraycopy(buttons, 0, myButtons, 0, buttons.length);
					myButtons[buttons.length] = virtualButton;
					final Button[] fButtons = myButtons;
					for (Button b : fButtons) {
						b.addListener(new ButtonListener() {

							@Override
							public void onStateChanged(ButtonState state) {
								// if one of the buttons was
								// pressed, remove all button
								// listeners.
								for (Button b : fButtons) {
									b.removeListener(this);
								}
								rest.setState(RestState.COMPLETED);
								executeRest(rest.getNextRest(), heater, buttons);
							}
						});
					}
				} else if (state.equals(RestState.COMPLETED)) {
					executeRest(rest.getNextRest(), heater, buttons);
				}
			}
		});
		// remember the restExecuter
		threads.add(re);
		// start it
		new Thread(re).start();
	}

	/**
	 * This method continues the mashing process. (It is called by the mashing
	 * rest service.)
	 */
	public void continueRest() {
		virtualButton.click();
	}

	/**
	 * This method returns the first rest in the mashing process. (Hook for the
	 * mashing rest service.)
	 * 
	 * @return
	 */
	public Rest getRest() {
		return firstRest;
	}

	public void terminate() {
		if (active) {
			for (RestExecuter re : threads) {
				re.terminate();
			}
		}
		active = false;
	}
}
