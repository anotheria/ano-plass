package net.anotheria.anoplass.api.mock;

import java.lang.reflect.Method;

import net.anotheria.anoplass.api.APIException;

public class ThrowExceptionMockMethod implements APIMockMethod{

	private APIException toThrow;
	
	public ThrowExceptionMockMethod(APIException anException){
		toThrow = anException;
	}
	
	@Override
	public Object invoke(Method method, Object[] args) throws APIException{
		throw toThrow;
	}

}
