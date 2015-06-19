package ch.goodrick.brewcontrol.mashing;

/**
 * This exception is a generic mashing process exception.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class MashingException extends Exception {

	private static final long serialVersionUID = 1L;

	public MashingException() {
	};

	public MashingException(String message) {
		super(message);
	}

}
