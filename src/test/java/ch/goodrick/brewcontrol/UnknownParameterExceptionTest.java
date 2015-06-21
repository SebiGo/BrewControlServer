package ch.goodrick.brewcontrol;

import org.junit.Test;

public class UnknownParameterExceptionTest {

	@Test(expected = UnknownParameterException.class)
	public void test() throws UnknownParameterException {
		throw new UnknownParameterException();
	}

}
