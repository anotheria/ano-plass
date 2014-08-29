package net.anotheria.anoplass.api.generic.login;

/**
 * All instances of this interface registered at the login api will be called by login api after each logout.
 * @author lrosenberg
 *
 */
public interface LogoutPostProcessor {
	void postProcessLogout(String userId);

}
