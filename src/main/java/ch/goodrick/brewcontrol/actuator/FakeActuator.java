package ch.goodrick.brewcontrol.actuator;

import ch.goodrick.brewcontrol.common.PhysicalQuantity;

/**
 * This actuator is for testing and developing purposes. Don't use it for
 * production.
 * 
 * @deprecated
 * @author sebastian@goodrick.ch
 *
 */
public class FakeActuator extends AbstractActuator implements Actuator {

	/**
	 * Create a new fake actuator.
	 * 
	 * @param physicalQuantity
	 *            the physical quantity the acutator regulates.
	 */
	public FakeActuator(PhysicalQuantity physicalQuantity) {
		super(physicalQuantity);
	}
}
