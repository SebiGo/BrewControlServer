package ch.goodrick.brewcontrol.common;

import ch.goodrick.brewcontrol.UnknownParameterException;
import ch.goodrick.brewcontrol.mashing.MashingException;

/**
 * This enum contains all operation modes for the server, i.e. wiring modes for
 * the hardware.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public enum OperationMode {
	/**
	 * Simulation mode: Used to simulate input and output.
	 */
	SIMULATE("simulate"),
	/**
	 * GPIO-Mode: Sensor, Button and heater directly connected to the Raspberry
	 * Pi
	 */
	GPIO("gpio"),
	/**
	 * PiFace-Mode: Heater and buttons on PiFace, sensor on GPIO_04.
	 */
	PIFACE("piface");

	private String mode;

	private OperationMode(String mode) {
		this.mode = mode;
	}

	public String getCommandlineOption() {
		return mode;
	}

	/**
	 * This converts a string into an an enum. 
	 * @param cmdOption  the string to be converted int an enum.
	 * @return
	 * @throws UnknownParameterException
	 */
	public static OperationMode getModeFromCommandlineOption(String cmdOption) throws UnknownParameterException {
		for (OperationMode mode : values()) {
			if (mode.getCommandlineOption().equals(cmdOption)) {
				return mode;
			}
		}
		throw new UnknownParameterException();
	}

}
