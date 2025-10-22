package ch.goodrick.brewcontrol.sensor;

import java.io.IOException;

import org.junit.Test;

public class SensorDS18B20Test {

//	@Test(expected=IOException.class)
//	public void testNoParam() throws IOException {
//		new SensorDS18B20();
//	}

	@Test(expected=IOException.class)
	public void testParam() throws IOException {
		new SensorDS18B20("id");
	}
	
}
