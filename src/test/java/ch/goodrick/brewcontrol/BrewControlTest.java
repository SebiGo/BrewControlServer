package ch.goodrick.brewcontrol;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.pi4j.io.exception.IOAlreadyExistsException;

public class BrewControlTest {

	@Test(expected = IOAlreadyExistsException.class)
	public void testMainGPIO() throws Exception {
		String[] argv = new String[1];
		argv[0] = "gpio";
		BrewControl.main(argv);
		fail();
	}

	@Test(expected = UnknownParameterException.class)
	public void testMain() throws Exception {
		String[] argv = new String[1];
		argv[0] = "bla";
		BrewControl.main(argv);
	}

	@Test
	public void testMainNoParam() throws Exception {
		new BrewControl();
		String[] argv = new String[0];
		BrewControl.main(argv);
	}

}
