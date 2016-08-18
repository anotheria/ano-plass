package net.anotheria.anoplass.api.mock;

import java.lang.reflect.Method;
/**
 * A mock method which always return a predefined value.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class ReturnObjectMockMethod implements APIMockMethod{
	/**
	 * The value to return.
	 */
	private Object value;

	/**
	 * Constructor.
	 *
	 * @param aValue object
	 */
	public ReturnObjectMockMethod(Object aValue){
		value = aValue;
	}
	
	/** {@inheritDoc} */
	@Override
	public Object invoke(Method method, Object[] args) {
		return value;
	}
	

}
