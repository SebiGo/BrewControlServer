package ch.goodrick.brewcontrol.button;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class FakeButtonTest {
	FakeButton fb = new FakeButton();

	@Test
	public void testWithListener() {
		final Set<Boolean> clicked = new HashSet<Boolean>();
		ButtonListener listener = new ButtonListener() {

			@Override
			public void onStateChanged(ButtonState state) {
				clicked.add(true);
			}
		};
		fb.addListener(listener);
		fb.setState(ButtonState.ON);
		assertTrue(fb.isOn());
		assertFalse(fb.isOff());
		assertEquals(fb.getState(), ButtonState.ON);
		fb.notifyListeners();
		assertTrue(clicked.contains(true));
		fb.removeListener(listener);
		fb.setState(ButtonState.OFF);
		assertFalse(fb.isOn());
		assertTrue(fb.isOff());
		assertTrue(fb.isState(ButtonState.OFF));
		assertEquals(fb.getState(), ButtonState.OFF);
		
	}

	@Test
	public void testWithoutListener() {
		final Set<Boolean> clicked = new HashSet<Boolean>();
		ButtonListener listener = new ButtonListener() {

			@Override
			public void onStateChanged(ButtonState state) {
				clicked.add(true);
			}
		};
		fb.setState(ButtonState.ON);
		assertTrue(fb.isOn());
		assertFalse(fb.isOff());
		assertEquals(fb.getState(), ButtonState.ON);
		fb.notifyListeners();
		assertFalse(clicked.contains(true));
		fb.removeListener(listener);

	}

}
