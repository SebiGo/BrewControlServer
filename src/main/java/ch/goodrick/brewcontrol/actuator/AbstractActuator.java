package ch.goodrick.brewcontrol.actuator;

import ch.goodrick.brewcontrol.common.PhysicalQuantity;

/**
 * Abstract implementation of an actuator. Collects all the generic methods used
 * in most button implementations.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public abstract class AbstractActuator implements Actuator {

	// private Logger log = LoggerFactory.getLogger(this.getClass());
	private PhysicalQuantity physicalQuantity;

	// this is used for the FakeSensor to read the Actuator status.
	public static ActuatorStatus status;

	/**
	 * Constructs a new Actuator.
	 * 
	 * @param physicalQuantity
	 *            the physical quanitity this actuator regulates.
	 */
	public AbstractActuator(PhysicalQuantity physicalQuantity) {
		this.physicalQuantity = physicalQuantity;
	}

	@Override
	public void on() {
		status = ActuatorStatus.ON;
	}

	@Override
	public void off() {
		status = ActuatorStatus.OFF;
	}

	@Override
	public PhysicalQuantity getPhysicalQuantity() {
		return physicalQuantity;
	}

	@Override
	public ActuatorStatus getStatus() {
		return status;
	}

}
