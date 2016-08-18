package net.anotheria.anoplass.api.generic.login;

/**
 * All instances of this interface registered at the login api will be called by login api after each logout.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public interface LogoutPostProcessor {
	/**
	 * <p>postProcessLogout.</p>
	 *
	 * @param userId a {@link java.lang.String} object.
	 */
	void postProcessLogout(String userId);

}
