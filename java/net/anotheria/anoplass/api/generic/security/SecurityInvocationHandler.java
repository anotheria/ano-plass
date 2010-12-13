package net.anotheria.anoplass.api.generic.security;

import java.lang.reflect.Method;

import net.anotheria.anoplass.api.API;

public interface SecurityInvocationHandler {
	<T extends API> Object getInterceptedValue(Method method, Object[] args, T apiImpl) throws Exception;
}
