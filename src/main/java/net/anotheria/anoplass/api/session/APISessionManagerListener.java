package net.anotheria.anoplass.api.session;

/**
 * Implementations of this interface may are notified of changes to the list of
 * active sessions APISessionManager.
 *
 * @author another
 * @version $Id: $Id
 */
public interface APISessionManagerListener {

	/**
	 * Notification that an api session will be invalidated.
	 *
	 * @param session APISession
	 */
	void apiSessionDestroyed(APISession session);
	
}
