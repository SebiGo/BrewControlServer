package ch.goodrick.brewcontrol.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InfoServiceTest {

	@Test
	public void testGetInfo() {
		InfoService is = new InfoService();
		assertEquals(null, ((InfoVO) is.getInfo().getEntity()).getVersion());
	}

}
