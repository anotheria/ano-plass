package net.anotheria.anoplass.api.generic.login;

/**
 * Login PostProcessors are called by the login api after each login attempt.
 * @author another
 *
 */
public interface LoginPostProcessor {
	/**
	 * Post login process.
	 * 
	 * @param userId used id
	 * @throws ProcessorException on failures
	 */
	void postProcessLogin(String userId) throws ProcessorException;

}
