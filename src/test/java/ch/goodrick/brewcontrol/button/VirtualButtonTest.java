package ch.goodrick.brewcontrol.button;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class VirtualButtonTest {
	VirtualButton b = new VirtualButton();

	@Test
	public void testWithListener() {
		final Set<Boolean> clicked = new HashSet<Boolean>();
		ButtonListener listener = new ButtonListener() {

			@Override
			public void onStateChanged(ButtonState state) {
				clicked.add(ButtonState.ON.equals(state));
			}
		};
		b.addListener(listener);

		b.on();
		assertTrue(b.isOn());
		assertFalse(b.isOff());
		assertEquals(b.getState(), ButtonState.ON);
		b.notifyListeners();
		assertTrue(clicked.contains(true));

		b.off();
		assertFalse(b.isOn());
		assertTrue(b.isOff());
		assertEquals(b.getState(), ButtonState.OFF);
		assertTrue(clicked.contains(true));

		b.removeListener(listener);
	}

	@Test
	public void testWithoutListener() {
		final Set<Boolean> clicked = new HashSet<Boolean>();
		ButtonListener listener = new ButtonListener() {

			@Override
			public void onStateChanged(ButtonState state) {
				clicked.add(ButtonState.ON.equals(state));
			}
		};

		b.on();
		assertTrue(b.isOn());
		assertFalse(b.isOff());
		assertEquals(b.getState(), ButtonState.ON);
		b.notifyListeners();
		assertFalse(clicked.contains(true));
		b.removeListener(listener);
	}

}
