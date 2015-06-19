package ch.goodrick.brewcontrol.mashing;

import static org.junit.Assert.*;

import org.junit.Test;

public class RestTest {

	@Test
	public void testMax() {
		Rest rest = new Rest("test", 1d, 1, true);
		assertFalse(rest.isStarted());
		assertTrue(rest.isContinueAutomatically());
		assertEquals(rest.getState(), RestState.INACTIVE);
		rest.setState(RestState.HEATING);
		assertEquals(rest.getState(), RestState.HEATING);
		rest.setState(RestState.ACTIVE);
		assertEquals(rest.getState(), RestState.ACTIVE);
		rest.setState(RestState.WAITING_COMPLETE);
		assertEquals(rest.getState(), RestState.WAITING_COMPLETE);
		rest.setState(RestState.COMPLETED);
		assertEquals(rest.getState(), RestState.COMPLETED);
	}

	@Test
	public void testMin() {
		Rest rest = new Rest("test", 1d, 1, true);
		assertEquals(rest.getState(), RestState.INACTIVE);
		rest.setState(RestState.ACTIVE);
		assertEquals(rest.getState(), RestState.ACTIVE);
		rest.setState(RestState.COMPLETED);
		assertEquals(rest.getState(), RestState.COMPLETED);
	}

	@Test(expected = IllegalRestStateException.class)
	public void testException() {
		Rest rest = new Rest("test", 1d, 1, true);
		assertEquals(rest.getState(), RestState.INACTIVE);
		rest.setState(RestState.COMPLETED);
	}

}
