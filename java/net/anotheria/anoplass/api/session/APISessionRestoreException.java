package net.anotheria.anoplass.api.session;

/**
 * APISession restore exception.
 *
 * @author h3ll
 */

public class APISessionRestoreException extends APISessionManagerException {
	/**
	 * Basic serial version uid.
	 */
	private static final long serialVersionUID = 7950018642764698952L;


	/**
	 * Constructor.
	 *
	 * @param sessionId id of session
	 * @param cause     exception
	 */
	public APISessionRestoreException(String sessionId, Throwable cause) {
		super("Distributed APISession[" + sessionId + "] restore failed.", cause);
	}

	/**
	 * Constructor.
	 *
	 * @param sessionId APISessionId
	 * @param message   cause message
	 */
	public APISessionRestoreException(String sessionId, String message) {
		super("Distributed APISession[" + sessionId + "] restore failed. Cause " + message);
	}
}
