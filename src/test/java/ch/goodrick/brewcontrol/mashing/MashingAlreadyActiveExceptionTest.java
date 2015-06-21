package ch.goodrick.brewcontrol.mashing;

import org.junit.Test;

public class MashingAlreadyActiveExceptionTest {

	@Test(expected = MashingAlreadyActiveException.class)
	public void test() throws MashingAlreadyActiveException {
		throw new MashingAlreadyActiveException();
	}

}
