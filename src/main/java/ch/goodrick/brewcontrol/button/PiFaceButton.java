package ch.goodrick.brewcontrol.button;

import java.io.IOException;

import com.pi4j.component.switches.SwitchListener;
import com.pi4j.component.switches.SwitchState;
import com.pi4j.component.switches.SwitchStateChangeEvent;
import com.pi4j.device.piface.PiFace;
import com.pi4j.device.piface.PiFaceSwitch;

/**
 * A representation of a Raspberry Pi PiFace pin connected button. Please make
 * sure your Piface is properly configured on your Raspberry Pi.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class PiFaceButton extends AbstractButton implements Button {

	/**
	 * Constructs a software representation of a PiFace button.
	 * 
	 * @param piFaceSwitch
	 *            the switch of the PiFace to use.
	 * @throws IOException 
	 */
	public PiFaceButton(PiFace piface, PiFaceSwitch piFaceSwitch) throws IOException {

		// add a listener with the pi4j library to hide the library
		piface.getSwitch(piFaceSwitch).addListener(new SwitchListener() {

			@Override
			public void onStateChange(SwitchStateChangeEvent event) {
				if (event.getNewState() == SwitchState.ON) {
					setState(ButtonState.ON);
				} else {
					setState(ButtonState.OFF);
				}
				notifyListeners();
			}
		});
	}
}
