package net.anotheria.anoplass.api.mock;

import java.lang.reflect.Method;

import net.anotheria.anoplass.api.API;
/**
 * A method implementation used to mask an underlying method in an api implementation.
 * @author lrosenberg
 */
public interface APIMaskMethod <T extends API>{
	/**
	 * Called to invoke the given method on a given api. An instance of underlying api implementation is also given for internal usage.
	 * @param method
	 * @param args
	 * @param maskedAPI
	 * @return
	 */
	Object invoke(Method method, Object[] args, T maskedAPI);
}
