package ch.goodrick.brewcontrol.service;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "info", namespace = "http://www.goodrick.ch")
@XmlType(name = "info", namespace = "http://www.goodrick.ch", propOrder = { "version" })
public class InfoVO {
	private String version;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
