package net.anotheria.anoplass.api.mock;

import java.lang.reflect.Method;

import net.anotheria.anoplass.api.APIException;

/**
 * A mocking method, used to construct an api implementation on the fly (or at least as much of it, as needed by tests).
 *
 * @author another
 * @version $Id: $Id
 */
public interface APIMockMethod {
	/**
	 *  Actually invoke mocked method.
	 *
	 * @param method method itself
	 * @param args arguments
	 * @return invocation result
	 * @throws net.anotheria.anoplass.api.APIException if any.
	 */
	Object invoke(Method method, Object[] args) throws APIException;
}
