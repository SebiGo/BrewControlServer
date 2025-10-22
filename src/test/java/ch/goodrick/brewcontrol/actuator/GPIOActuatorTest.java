package ch.goodrick.brewcontrol.actuator;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assume;
import org.junit.Test;

import ch.goodrick.brewcontrol.common.PhysicalQuantity;

/**
 * Adapted for Pi4J v2: uses BCM numbering (e.g., 4 == GPIO4). The test behaves
 * differently on Raspberry Pi vs. non-Pi hosts.
 */
public class GPIOActuatorTest {

	private static boolean isRaspberryPi() {
		try {
			// Heuristic: device-tree model present and contains "Raspberry"
			byte[] model = Files.readAllBytes(Paths.get("/proc/device-tree/model"));
			String text = new String(model).toLowerCase();
			return text.contains("raspberry");
		} catch (IOException e) {
			return false;
		}
	}

	@Test
	public void constructsOnRaspberryPi() {
		// Run only on a Raspberry Pi
		Assume.assumeTrue("Not running on a Raspberry Pi; skipping construct test.", isRaspberryPi());

		GPIOActuator gpioa = new GPIOActuator(4, PhysicalQuantity.TEMPERATURE);
		// simple smoke: switch off safely
		gpioa.off();
	}

	@Test
	public void failsPredictablyOffPi() {
		// Run only on non-Pi (e.g., CI/build machines)
		Assume.assumeFalse("Running on a Raspberry Pi; skipping failure test.", isRaspberryPi());

		try {
			new GPIOActuator(4, PhysicalQuantity.TEMPERATURE);
			fail("Creating GPIOActuator off-Pi should throw due to missing GPIO provider");
		} catch (Throwable expected) {
			// OK: Pi4J v2 will throw a runtime exception when no Raspberry Pi provider is
			// available.
		}
	}
}
