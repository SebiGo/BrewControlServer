package ch.goodrick.brewcontrol.common;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.goodrick.brewcontrol.UnknownParameterException;

public class OperationModeTest {

	@Test
	public void testGetCommandlineOption() throws UnknownParameterException {
		assertEquals(OperationMode.SIMULATE, OperationMode.getModeFromCommandlineOption(OperationMode.SIMULATE.getCommandlineOption()));
		assertEquals(OperationMode.GPIO, OperationMode.getModeFromCommandlineOption(OperationMode.GPIO.getCommandlineOption()));
		assertEquals(OperationMode.PIFACE, OperationMode.getModeFromCommandlineOption(OperationMode.PIFACE.getCommandlineOption()));
	}

	@Test(expected=UnknownParameterException.class)
	public void testGetModeFromCommandlineOption() throws UnknownParameterException {
		assertEquals(OperationMode.PIFACE, OperationMode.getModeFromCommandlineOption("blubb"));
	}

}
