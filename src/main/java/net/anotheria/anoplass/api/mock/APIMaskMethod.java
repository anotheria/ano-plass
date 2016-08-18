package net.anotheria.anoplass.api.mock;

import net.anotheria.anoplass.api.API;
import net.anotheria.anoplass.api.APIException;

import java.lang.reflect.Method;
/**
 * A method implementation used to mask an underlying method in an api implementation.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public interface APIMaskMethod <T extends API>{
	/**
	 * Called to invoke the given method on a given api. An instance of underlying api implementation is also given for internal usage.
	 *
	 * @param method a {@link java.lang.reflect.Method} object.
	 * @param args an array of {@link java.lang.Object} objects.
	 * @param maskedAPI a T object.
	 * @return a {@link java.lang.Object} object.
	 * @throws net.anotheria.anoplass.api.APIException if any.
	 */
	Object invoke(Method method, Object[] args, T maskedAPI) throws APIException;
}
