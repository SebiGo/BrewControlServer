package ch.goodrick.brewcontrol.actuator;

import ch.goodrick.brewcontrol.common.PhysicalQuantity;

/**
 * The generic interface for any actuators.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public interface Actuator {

	/**
	 * Switch the actuator on.
	 */
	public void on();

	/**
	 * Switch the actuator off.
	 */
	public void off();

	/**
	 * The current status of the actuator.
	 */
	public ActuatorStatus getStatus();

	/**
	 * The physical quantity this actuator regulates.
	 */
	public PhysicalQuantity getPhysicalQuantity();
}
