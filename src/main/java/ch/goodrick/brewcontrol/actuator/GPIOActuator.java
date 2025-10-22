package ch.goodrick.brewcontrol.actuator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalState;

import ch.goodrick.brewcontrol.common.PhysicalQuantity;

/**
 * Raspberry Pi GPIO actuator using Pi4J v2. Uses BCM numbering (e.g., GPIO4 ->
 * 4).
 */
public class GPIOActuator extends AbstractActuator implements Actuator {

	private static final Logger log = LoggerFactory.getLogger(GPIOActuator.class);

	// Shared Pi4J context (auto-detects Raspberry Pi and loads plugin-raspberrypi)
	private static final Context PI4J = Pi4J.newAutoContext();

	static {
		// Clean shutdown for all created IOs
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				log.info("Shutting down Pi4J context...");
				PI4J.shutdown();
			} catch (Exception e) {
				log.warn("Pi4J shutdown hook failed", e);
			}
		}, "pi4j-shutdown"));
	}

	private final DigitalOutput relay;

	/**
	 * @param bcmPin           BCM pin number (e.g., 4 for GPIO4).
	 * @param physicalQuantity the physical quantity this actuator regulates.
	 */
	public GPIOActuator(int bcmPin, PhysicalQuantity physicalQuantity) {
		super(physicalQuantity);

		String relayId = "actuator-" + bcmPin;

		DigitalOutputConfig config = DigitalOutput.newConfigBuilder(PI4J).id(relayId).name("GPIO Actuator " + bcmPin)
				.address(bcmPin) // BCM numbering
				.initial(DigitalState.LOW) // start safe/off
				.shutdown(DigitalState.LOW) // ensure off on shutdown
				.provider("raspberrypi-digital-output") // from pi4j-plugin-raspberrypi
				.build();

		this.relay = PI4J.create(config);
		log.info("Created GPIOActuator on BCM {} for {}", bcmPin, physicalQuantity);
	}

	@Override
	public void on() {
		relay.high();
		log.debug("GPIOActuator -> ON ({}).", relay.id());
		super.on();
	}

	@Override
	public void off() {
		relay.low();
		log.debug("GPIOActuator -> OFF ({}).", relay.id());
		super.off();
	}

	/**
	 * Optional explicit cleanup if you dispose this actuator before JVM exit. Note:
	 * DigitalOutput has no close(); deregister via Pi4J registry.
	 */
	public void close() {
		try {
			relay.low();
			if (PI4J.registry().exists(relay.id())) {
				PI4J.registry().remove(relay.id());
			}
			log.info("GPIOActuator deregistered ({}).", relay.id());
		} catch (Exception e) {
			log.warn("Error while deregistering GPIOActuator ({})", relay.id(), e);
		}
		// PI4J context bleibt aktiv; globaler Shutdown-Hook erledigt den Rest.
	}

	@Override
	public PhysicalQuantity getPhysicalQuantity() {
		return super.getPhysicalQuantity();
	}
}
