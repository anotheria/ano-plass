package net.anotheria.anoplass.api.generic.login;

/**
 * Called before each logout. Technically can prevent a logout by throwing a processor exception. 
 * @author lrosenberg
 */
public interface LogoutPreProcessor {
	/**
	 * Pre LogOut.
	 *
	 * @param userId user id
	 * @throws ProcessorException
	 */
	void preProcessLogout(String userId) throws ProcessorException;
}
