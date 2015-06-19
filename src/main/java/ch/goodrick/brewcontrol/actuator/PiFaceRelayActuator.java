package ch.goodrick.brewcontrol.actuator;

import java.io.IOException;

import ch.goodrick.brewcontrol.common.PhysicalQuantity;

import com.pi4j.device.piface.PiFace;
import com.pi4j.device.piface.PiFaceRelay;

/**
 * A representation of a Raspberry Pi PiFace pin connected actuator. Please make
 * sure your Piface is properly configured on your Raspberry Pi.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class PiFaceRelayActuator extends AbstractActuator implements Actuator {

	private final PiFaceRelay relay;
	private final PiFace piface;

	/**
	 * Constructs a new PiFace output connected Actuator.
	 * 
	 * @param relay
	 *            the PiFace relay the actuator is connected to.
	 * @param physicalQuantity
	 *            the physical quantity this actuator regulates.
	 * @throws IOException
	 *             if the supporting library throws an IOException.
	 */
	public PiFaceRelayActuator(PiFace piface, PiFaceRelay relay, PhysicalQuantity physicalQuantity) throws IOException {
		super(physicalQuantity);
		this.piface = piface;
		this.relay = relay;
	}

	@Override
	public void on() {
		piface.getRelay(relay).close();
		super.on();
	}

	@Override
	public void off() {
		piface.getRelay(relay).open();
		super.off();
	}

	@Override
	public PhysicalQuantity getPhysicalQuantity() {
		return PhysicalQuantity.TEMPERATURE;
	}
}
