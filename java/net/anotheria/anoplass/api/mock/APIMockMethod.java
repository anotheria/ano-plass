package net.anotheria.anoplass.api.mock;

import java.lang.reflect.Method;

/**
 * A mocking method, used to construct an api implementation on the fly (or at least as much of it, as needed by tests).
 * @author another
 *
 */
public interface APIMockMethod {
	/**
	 *  Actually invoke mocked method.
	 * 
	 * @param method method itself
	 * @param args arguments
	 * @return invocation result
	 */
	Object invoke(Method method, Object[] args);
}
