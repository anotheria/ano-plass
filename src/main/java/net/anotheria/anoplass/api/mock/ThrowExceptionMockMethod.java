package net.anotheria.anoplass.api.mock;

import java.lang.reflect.Method;

import net.anotheria.anoplass.api.APIException;

/**
 * <p>ThrowExceptionMockMethod class.</p>
 *
 * @author another
 * @version $Id: $Id
 */
public class ThrowExceptionMockMethod implements APIMockMethod{

	private APIException toThrow;
	
	/**
	 * <p>Constructor for ThrowExceptionMockMethod.</p>
	 *
	 * @param anException a {@link net.anotheria.anoplass.api.APIException} object.
	 */
	public ThrowExceptionMockMethod(APIException anException){
		toThrow = anException;
	}
	
	/** {@inheritDoc} */
	@Override
	public Object invoke(Method method, Object[] args) throws APIException{
		throw toThrow;
	}

}
