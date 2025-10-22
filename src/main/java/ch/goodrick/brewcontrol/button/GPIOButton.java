package ch.goodrick.brewcontrol.button;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

/**
 * Raspberry Pi GPIO button using Pi4J v2 (BCM numbering). Button connected
 * between GPIO (e.g., BCM 4) and 3.3V, internal pull-down enabled.
 */
public class GPIOButton extends Button {

	private static final Logger log = LoggerFactory.getLogger(GPIOButton.class);

	// Shared Pi4J context (auto-detects Raspberry Pi)
	private static final Context PI4J = Pi4J.newAutoContext();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				log.info("Shutting down Pi4J context (GPIOButton)...");
				PI4J.shutdown();
			} catch (Exception e) {
				log.warn("Pi4J shutdown hook failed (GPIOButton)", e);
			}
		}, "pi4j-shutdown-gpiobutton"));
	}

	private final DigitalInput buttonInput;

	/**
	 * @param bcmPin BCM pin number (e.g., 4 for GPIO4)
	 */
	public GPIOButton(int bcmPin) {
		DigitalInputConfig config = DigitalInput.newConfigBuilder(PI4J).id("button-" + bcmPin)
				.name("GPIO Button " + bcmPin).address(bcmPin) // BCM numbering
				.pull(PullResistance.PULL_DOWN) // HIGH when pressed
				.debounce(5_000L) // ~5 ms (µs in Pi4J 2)
				// .provider("raspberrypi-digital-input") // optional; auto-selected
				.build();

		this.buttonInput = PI4J.create(config);

		// initial state
		updateStateFrom(buttonInput.state());

		// event listener
		buttonInput.addListener(event -> {
			DigitalState state = event.state();
			log.debug("GPIOButton event on BCM {} -> {}", bcmPin, state);
			updateStateFrom(state);
		});

		log.info("Created GPIOButton on BCM {}", bcmPin);
	}

	private void updateStateFrom(DigitalState state) {
		if (state == null)
			return;
		if (state.isHigh()) {
			setState(ButtonState.ON);
		} else {
			setState(ButtonState.OFF);
		}
	}
}
