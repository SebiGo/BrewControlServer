package ch.goodrick.brewcontrol;

import static org.junit.Assert.fail;

import org.apache.cxf.service.factory.ServiceConstructionException;
import org.junit.Test;

public class BrewControlTest {

	@Test(expected = ServiceConstructionException.class)
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
