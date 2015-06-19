package ch.goodrick.brewcontrol.mashing;

import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * This Class is the wrapper for a rest. It contains all relevant parameters
 * required for a rest and a state engine.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class Rest {
	// private Logger log = LoggerFactory.getLogger(this.getClass());
	private UUID uuid;
	private String restName;
	private Double temperature;
	private Integer duration;
	private Boolean continueAutomatically;
	private Rest nextRest = null;
	private RestState status = RestState.INACTIVE;
	private GregorianCalendar heating;
	private GregorianCalendar active;
	private GregorianCalendar completed;

	/**
	 * Constructs a rest with all required parameters.
	 * 
	 * @param restName
	 *            the name of the rest.
	 * @param temperature
	 *            the temperature of the rest.
	 * @param duration
	 *            the duration of the rest in minutes.
	 * @param continueAutomatically
	 *            true, if the next rest should start automatically, once the
	 *            duration is up, false if a manual interception is required.
	 */
	public Rest(String restName, Double temperature, Integer duration, Boolean continueAutomatically) {
		this.setUuid(UUID.randomUUID());
		this.restName = restName;
		this.temperature = temperature;
		this.duration = duration;
		this.continueAutomatically = continueAutomatically;
	}

	public GregorianCalendar getHeating() {
		return heating;
	}

	public GregorianCalendar getActive() {
		return active;
	}

	public GregorianCalendar getCompleted() {
		return completed;
	}

	public UUID getUuid() {
		return uuid;
	}

	private void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * This method returns true, if the rest is in any of the status HEATING,
	 * ACTIVE or WAITING_COMPLETE.
	 * 
	 * @return true if the status is HEATING, ACTIVE or WAITING_COMPLETE.
	 */
	public Boolean isStarted() {
		return (status == RestState.HEATING || status == RestState.ACTIVE || status == RestState.WAITING_COMPLETE);
	}

	/**
	 * Through this method the status of the rest can be changed. It contains
	 * the status engine, only legal status changes are allowed, otherwise
	 * 
	 * @param status
	 *            the new status.
	 * @throws IllegalRestStateException
	 *             if the new status is not allowed.
	 */

	protected void setState(RestState status) throws IllegalRestStateException {
		switch (this.status) {
		case INACTIVE:
			if (status == RestState.HEATING) {
				this.status = status;
				this.heating = new GregorianCalendar();
			} else if (status == RestState.ACTIVE) {
				this.status = status;
				this.heating = new GregorianCalendar();
				this.active = new GregorianCalendar();
			} else {
				throw new IllegalRestStateException(this.status + " --> " + status);
			}
			break;
		case HEATING:
			if (status == RestState.ACTIVE) {
				this.status = status;
				this.active = new GregorianCalendar();
			} else {
				throw new IllegalRestStateException(this.status + " --> " + status);
			}
			break;
		case ACTIVE:
			if (status == RestState.WAITING_COMPLETE) {
				this.status = status;
			} else if (status == RestState.COMPLETED) {
				this.status = status;
				this.completed = new GregorianCalendar();
			} else {
				throw new IllegalRestStateException(this.status + " --> " + status);
			}
			break;
		case WAITING_COMPLETE:
			if (status == RestState.COMPLETED) {
				this.status = status;
				this.completed = new GregorianCalendar();
			} else {
				throw new IllegalRestStateException(this.status + " --> " + status);
			}
			break;
		case COMPLETED:
			if (status == RestState.INACTIVE) {
				this.status = status;
				this.active = null;
				this.heating = null;
				this.completed = null;
			} else {
				throw new IllegalRestStateException(this.status + " --> " + status);
			}
			break;
		}
	}

	public RestState getState() {
		return status;
	}

	public String getName() {
		return restName;
	}

	public void setRestName(String restName) {
		this.restName = restName;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Boolean isContinueAutomatically() {
		return continueAutomatically;
	}

	public void setContinueAutomatically(Boolean continueAutomatically) {
		this.continueAutomatically = continueAutomatically;
	}

	public Rest getNextRest() {
		return nextRest;
	}

	public void setNextRest(Rest nextRest) {
		this.nextRest = nextRest;
	}

}
