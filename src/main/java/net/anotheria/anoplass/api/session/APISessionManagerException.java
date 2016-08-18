package net.anotheria.anoplass.api.session;

/**
 * APISessionManager  exception.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class APISessionManagerException extends Exception {

	/**
	 * Basic serial version uid.
	 */
	private static final long serialVersionUID = 9188563504474763273L;

	/**
	 * Constrictor.
	 *
	 * @param message string message parameter
	 */
	public APISessionManagerException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message string message
	 * @param cause   exception cause
	 */
	public APISessionManagerException(String message, Throwable cause) {
		super(message, cause);
	}
}
