package net.anotheria.anoplass.api.generic.login.processors;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoplass.api.generic.login.LogoutPostProcessor;

/**
 * Cleans a session after logout to remove all objects that belong to a logged in user only. If the object is put under
 * the policy SURVIVE_LOGOUT it will survive the logout.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class SessionCleanupOnLogoutProcessor implements LogoutPostProcessor{

	/** {@inheritDoc} */
	@Override public void postProcessLogout(String userId) {
		APICallContext.getCallContext().getCurrentSession().cleanupOnLogout();
	}
	
}
