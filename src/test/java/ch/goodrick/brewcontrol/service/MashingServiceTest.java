package ch.goodrick.brewcontrol.service;

import org.junit.Test;

import ch.goodrick.brewcontrol.mashing.Mashing;
import ch.goodrick.brewcontrol.mashing.Rest;

public class MashingServiceTest {
	final String test = "test";

	@Test
	public void test() {
		Mashing.getInstance().setName(test);
		Mashing.getInstance().addRest(new Rest(test, 57d, 1, Boolean.FALSE));
		// FIXME more tests
	}

}
