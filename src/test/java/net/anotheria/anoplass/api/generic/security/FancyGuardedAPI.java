package net.anotheria.anoplass.api.generic.security;

import java.lang.reflect.Method;

import net.anotheria.anoplass.api.API;
import net.anotheria.util.StringUtils;

//this is test api for not THAT common usages of security interception.
public interface FancyGuardedAPI extends API{
	@InterceptIfNotPermitted(action="foo", handler=MultiplyIntegerByTwo.class)
	int getAnswerToLifeAndEverything();
	
	@InterceptIfNotPermitted(action="foo", handler=ReverseResultString.class)
	String sayHello();
	
	public static class MultiplyIntegerByTwo implements SecurityInvocationHandler{

		@Override
		public <T extends API> Object getInterceptedValue(Method method,
				Object[] args, T apiImpl) throws Exception {
			int result = (Integer)method.invoke(apiImpl, args);
			return result*2;
		}
	}
	
	public static class ReverseResultString implements SecurityInvocationHandler{

		@Override
		public <T extends API> Object getInterceptedValue(Method method,
				Object[] args, T apiImpl) throws Exception {
			String result = (String) method.invoke(apiImpl, args);
			return StringUtils.reverseString(result);
		}
		
	}
}
