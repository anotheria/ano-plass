package net.anotheria.anoplass.api.session;

/**
 * APISession creation exception.
 *
 * @author h3ll
 */

public class APISessionCreationException extends APISessionManagerException {

	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = 782178141803904199L;


	/**
	 * Constructor.
	 *
	 * @param sessionId id of session
	 * @param cause	 exception
	 */
	public APISessionCreationException(String sessionId, Throwable cause) {
		super("Distributed APISession[" + sessionId + "] creation failed.", cause);
	}
}
