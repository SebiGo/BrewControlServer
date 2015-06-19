package ch.goodrick.brewcontrol.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class PhysicalQuantityTest {

	@Test
	public void test() {
		assertEquals(PhysicalQuantity.TEMPERATURE.getUnit(), "\u00B0C");
		assertEquals(PhysicalQuantity.RPM.getUnit(), "rpm");
		assertEquals(PhysicalQuantity.TEMPERATURE.getID(), 1);
		assertEquals(PhysicalQuantity.RPM.getID(), 2);
	}

}
