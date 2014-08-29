package net.anotheria.anoplass.api.mock;

import net.anotheria.anoplass.api.API;
import net.anotheria.anoplass.api.APIException;

import java.lang.reflect.Method;

/**
 * Return object mask method.
 *
 * @param <T> - masked API type {@link API}
 */
public class ReturnObjectMaskMethod<T extends API> implements APIMaskMethod<T> {
	/**
	 * ReturnObjectMaskMethod 'returnValue'.
	 */
	private Object returnValue;

	/**
	 * Constructor.
	 *
	 * @param anObject {@link Object}
	 */
	public ReturnObjectMaskMethod(Object anObject) {
		returnValue = anObject;
	}

	@Override
	public Object invoke(Method method, Object[] args, T maskedAPI) throws APIException {
		return returnValue;
	}
}

