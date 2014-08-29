package net.anotheria.anoplass.api.mock;

import java.lang.reflect.Method;
/**
 * A mock method which always return a predefined value.
 * @author lrosenberg
 *
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
	
	@Override
	public Object invoke(Method method, Object[] args) {
		return value;
	}
	

}
