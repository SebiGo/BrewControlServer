package ch.goodrick.brewcontrol.service;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.goodrick.brewcontrol.mashing.RestState;

public class RestVOTest {

	@Test
	public void testGetHeating() {
		RestVO rvo = new RestVO();
		rvo.setHeating(1);
		assertEquals(1, rvo.getHeating());
	}

	@Test
	public void testGetActive() {
		RestVO rvo = new RestVO();
		rvo.setActive(1);
		assertEquals(1, rvo.getActive());
	}

	@Test
	public void testGetCompleted() {
		RestVO rvo = new RestVO();
		rvo.setCompleted(1);
		assertEquals(1, rvo.getCompleted());
	}

	@Test
	public void testGetPriority() {
		RestVO rvo = new RestVO();
		rvo.setPriority(1);
		assertEquals(1, rvo.getPriority());
	}

	@Test
	public void testGetStatus() {
		RestVO rvo = new RestVO();
		rvo.setStatus(RestState.HEATING);
		assertEquals(RestState.HEATING, rvo.getStatus());
	}

}
