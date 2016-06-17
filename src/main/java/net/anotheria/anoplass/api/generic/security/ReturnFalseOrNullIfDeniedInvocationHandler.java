package net.anotheria.anoplass.api.generic.security;

import java.lang.reflect.Method;

import net.anotheria.anoplass.api.API;

public class ReturnFalseOrNullIfDeniedInvocationHandler implements SecurityInvocationHandler{

	@Override
	public <T extends API> Object getInterceptedValue(Method method,
			Object[] args, T apiImpl) throws Exception {
		if (method.getReturnType()==Boolean.class || method.getReturnType()==boolean.class)
			return Boolean.FALSE;
		return null;
	}

}