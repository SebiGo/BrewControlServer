package ch.goodrick.brewcontrol.mashing;

/**
 * This exception is thrown if the status change for the rest was illegal.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class IllegalRestStateException extends IllegalStateException {

	private static final long serialVersionUID = 1L;

	public IllegalRestStateException(String message) {
		super(message);
	}
}
