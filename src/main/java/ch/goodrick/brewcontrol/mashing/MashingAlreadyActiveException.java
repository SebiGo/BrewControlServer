package ch.goodrick.brewcontrol.mashing;

/**
 * This exception is thrown if an attempt is done to launch a mashing process
 * while there is another running mashing process.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class MashingAlreadyActiveException extends MashingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
