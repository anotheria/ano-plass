package net.anotheria.anoplass.api.generic.login;

/**
 * Login preProcessors are used to adopt the behaviour of the login api. They get called before each login. If a LoginPreProcessor throws a Processor exception
 * the login is aborted.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public interface LoginPreProcessor {
	/**
	 * PreLogin operation.
	 *
	 * @param userId user id
	 * @throws net.anotheria.anoplass.api.generic.login.ProcessorException on failures
	 */
	void preProcessLogin(String userId) throws ProcessorException;
}
