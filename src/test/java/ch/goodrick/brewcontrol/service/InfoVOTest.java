package ch.goodrick.brewcontrol.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InfoVOTest {

	@Test
	public void testGetVersion() {
		String test = "test";
		InfoVO ivo = new InfoVO();
		ivo.setVersion(test);
		assertEquals(test, ivo.getVersion());
	}

}
