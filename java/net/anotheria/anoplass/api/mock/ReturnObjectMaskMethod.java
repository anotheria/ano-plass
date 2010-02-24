package net.anotheria.anoplass.api.mock;

import java.lang.reflect.Method;

import net.anotheria.anoplass.api.API;
import net.anotheria.anoplass.api.APIException;

public class ReturnObjectMaskMethod<T extends API> implements APIMaskMethod<T>{
	
	private Object returnValue;
	
	public ReturnObjectMaskMethod(Object anObject){
		returnValue = anObject;
	}
	
	public Object invoke(Method method, Object[] args, T maskedAPI) throws APIException{
		return returnValue;
	}
}
