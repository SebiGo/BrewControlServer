package ch.goodrick.brewcontrol.service;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "mashing", namespace = "http://www.goodrick.ch")
@XmlType(name = "mashing", namespace = "http://www.goodrick.ch", propOrder = { "name", "temperature", "activeRest" })
public class MashingVO {
	private String name;
	private double temperature;
	private UUID activeRest;

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
