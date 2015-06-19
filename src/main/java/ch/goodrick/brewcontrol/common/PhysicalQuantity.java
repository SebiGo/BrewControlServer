package ch.goodrick.brewcontrol.common;

/**
 * This is the enum for physical quantities used in the program.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public enum PhysicalQuantity {

	/**
	 * Temperature in °C
	 */
	TEMPERATURE(1, "\u00B0C"),
	/**
	 * Rounds per minute
	 */
	RPM(2, "rpm");

	private int id;
	private String unit;

	PhysicalQuantity(int id, String unit) {
		this.id = id;
		this.unit = unit;
	}

	/**
	 * The id of the enum value.
	 * 
	 * @return the id of the enum value.
	 */
	public int getID() {
		return id;
	}

	/**
	 * The unit used for this physical quantity.
	 * 
	 * @return the physical quantity sting representation of the enum value.
	 */
	public String getUnit() {
		return unit;
	}

}
