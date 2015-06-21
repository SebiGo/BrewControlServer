package ch.goodrick.brewcontrol.button;

import java.io.IOException;

import org.junit.Test;

import com.pi4j.device.piface.PiFaceSwitch;

public class PiFaceButtonTest {

	@Test(expected = NullPointerException.class)
	public void test() throws IOException {
		new PiFaceButton(null, PiFaceSwitch.S1);
	}

}
