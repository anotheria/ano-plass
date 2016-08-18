package net.anotheria.anoplass.api.generic.login;

/**
 * Called before each logout. Technically can prevent a logout by throwing a processor exception.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public interface LogoutPreProcessor {
	/**
	 * Pre LogOut.
	 *
	 * @param userId user id
	 * @throws net.anotheria.anoplass.api.generic.login.ProcessorException if any.
	 */
	void preProcessLogout(String userId) throws ProcessorException;
}
