package net.anotheria.anoplass.api;

/**
 * APIInitException, for init - errors. 
 *
 * @author h3llka
 */
public class APIInitException extends APIException {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param message string message
	 */
	public APIInitException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message string message
	 * @param cause throwable cause
	 */
	public APIInitException(String message, Exception cause) {
		super(message, cause);
	}
}
