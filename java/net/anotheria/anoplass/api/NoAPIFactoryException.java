package net.anotheria.anoplass.api;

/**
 * NoAPIFactoryException should be used in APIFinder, when API not founded.
 *
 * @author h3llka
 */
public class NoAPIFactoryException extends RuntimeException{

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param message string message
	 */
	public NoAPIFactoryException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message string message
	 * @param cause throwable cause
	 */
	public NoAPIFactoryException(String message, Exception cause) {
		super(message, cause);
	}
}
