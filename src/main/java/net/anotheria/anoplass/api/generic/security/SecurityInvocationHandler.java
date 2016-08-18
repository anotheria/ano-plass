package net.anotheria.anoplass.api.generic.security;

import net.anotheria.anoplass.api.API;

import java.lang.reflect.Method;

/**
 * <p>SecurityInvocationHandler interface.</p>
 *
 * @author another
 * @version $Id: $Id
 */
public interface SecurityInvocationHandler {
	/**
	 * <p>getInterceptedValue.</p>
	 *
	 * @param method a {@link java.lang.reflect.Method} object.
	 * @param args an array of {@link java.lang.Object} objects.
	 * @param apiImpl a T object.
	 * @param <T> a T object.
	 * @return a {@link java.lang.Object} object.
	 * @throws java.lang.Exception if any.
	 */
	<T extends API> Object getInterceptedValue(Method method, Object[] args, T apiImpl) throws Exception;
}
