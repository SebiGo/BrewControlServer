package ch.goodrick.brewcontrol.mashing;

/**
 * All the status for a rest.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public enum RestState {
	/**
	 * Nothing has happened to the rest so far, the initial status of a rest.
	 * <p>
	 * Next allowed status: HEATING, ACTIVE.
	 */
	INACTIVE,
	/**
	 * The rest is heating up, countdown timer has not been started.
	 * <p>
	 * Next allowed status: ACTIVE
	 */
	HEATING,
	/**
	 * The rest is active and will be terminated once the countdown timer is up.
	 * <p>
	 * Next allowed status: WAITING_COMPLETE, COMPLETED
	 */
	ACTIVE,
	/**
	 * The rest is finished, the rest will be completed, once it has been
	 * manually confirmed.
	 * <p>
	 * Next allowed status: COMPLETED
	 */
	WAITING_COMPLETE,
	/**
	 * The rest has been completed.
	 * <p>
	 * Next allowed status: ACTIVE (in order to reset the rest.)
	 */
	COMPLETED;
}
