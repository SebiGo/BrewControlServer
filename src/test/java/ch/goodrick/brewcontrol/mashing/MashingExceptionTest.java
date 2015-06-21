package ch.goodrick.brewcontrol.mashing;

import org.junit.Test;

public class MashingExceptionTest {

	@Test(expected = MashingException.class)
	public void test() throws MashingException {
		throw new MashingException("test");
	}

}
