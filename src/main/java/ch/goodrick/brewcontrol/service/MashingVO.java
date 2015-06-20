package ch.goodrick.brewcontrol.service;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "mashing", namespace = "http://www.goodrick.ch")
@XmlType(name = "mashing", namespace = "http://www.goodrick.ch", propOrder = { "name", "temperature", "activeRest", "altitude", "measuredTemperatureIceWater",
		"measuredTemperatureBoilingWater" })
public class MashingVO {
	private String name;
	private double temperature;
	private UUID activeRest;
	private long altitude;
	private double measuredTemperatureIceWater;
	private double measuredTemperatureBoilingWater;

	public long getAltitude() {
		return altitude;
	}

	public void setAltitude(long altitude) {
		this.altitude = altitude;
	}

	public double getMeasuredTemperatureIceWater() {
		return measuredTemperatureIceWater;
	}

	public void setMeasuredTemperatureIceWater(double measuredTemperatureIceWater) {
		this.measuredTemperatureIceWater = measuredTemperatureIceWater;
	}

	public double getMeasuredTemperatureBoilingWater() {
		return measuredTemperatureBoilingWater;
	}

	public void setMeasuredTemperatureBoilingWater(double measuredTemperatureBoilingWater) {
		this.measuredTemperatureBoilingWater = measuredTemperatureBoilingWater;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getActiveRest() {
		return activeRest;
	}

	public void setActiveRest(UUID activeRest) {
		this.activeRest = activeRest;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

}
