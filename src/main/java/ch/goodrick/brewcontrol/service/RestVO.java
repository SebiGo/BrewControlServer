package ch.goodrick.brewcontrol.service;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import ch.goodrick.brewcontrol.mashing.RestState;

@XmlRootElement(name = "rest", namespace = "http://www.goodrick.ch")
@XmlType(name = "rest", namespace = "http://www.goodrick.ch", propOrder = { "priority", "uuid", "name", "temperature", "duration", "continueAutomatically",
		"status", "heating", "active", "completed" })
// @XmlAccessorType(XmlAccessType.FIELD)
public class RestVO {
	private long priority;
	private UUID uuid;
	private String name;
	private double temperature;
	private int duration;
	private boolean continueAutomatically;
	private RestState status;
	private long heating;
	private long active;
	private long completed;

	public long getHeating() {
		return heating;
	}

	public void setHeating(long heating) {
		this.heating = heating;
	}

	public long getActive() {
		return active;
	}

	public void setActive(long active) {
		this.active = active;
	}

	public long getCompleted() {
		return completed;
	}

	public void setCompleted(long completed) {
		this.completed = completed;
	}

	public long getPriority() {
		return priority;
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String restName) {
		this.name = restName;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public boolean getContinueAutomatically() {
		return continueAutomatically;
	}

	public void setContinueAutomatically(boolean continueAutomatically) {
		this.continueAutomatically = continueAutomatically;
	}

	public RestState getStatus() {
		return status;
	}

	public void setStatus(RestState status) {
		this.status = status;
	}
}
