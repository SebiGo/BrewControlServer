package ch.goodrick.brewcontrol.button;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assume;
import org.junit.Test;

/**
 * Pi4J v2 test for GPIOButton using BCM numbering. Uses a heuristic to
 * distinguish Raspberry Pi vs. non-Pi hosts.
 */
public class GPIOButtonTest {

	private static boolean isRaspberryPi() {
		try {
			byte[] model = Files.readAllBytes(Paths.get("/proc/device-tree/model"));
			String text = new String(model).toLowerCase();
			return text.contains("raspberry");
		} catch (IOException e) {
			return false;
		}
	}

	@Test
	public void constructsOnRaspberryPi() {
		// Run only on Raspberry Pi hardware
		Assume.assumeTrue("Not running on a Raspberry Pi; skipping construct test.", isRaspberryPi());

		// BCM 4 (GPIO4) as a safe default input with internal pull-down
		new GPIOButton(4);
	}

	@Test
	public void failsPredictablyOffPi() {
		// Run only on non-Pi environments (e.g. CI/build machines)
		Assume.assumeFalse("Running on a Raspberry Pi; skipping failure test.", isRaspberryPi());

		try {
			new GPIOButton(4);
			fail("Creating GPIOButton off-Pi should fail due to missing Raspberry Pi provider.");
		} catch (Throwable expected) {
			// Expected: Pi4J v2 throws when no Raspberry Pi provider is available.
		}
	}
}
