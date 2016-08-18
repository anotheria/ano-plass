package net.anotheria.anoplass.api.mock;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for masking methods.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class MaskMethodRegistry {
	private static Map<Method, APIMaskMethod<?>> methods = new ConcurrentHashMap<Method, APIMaskMethod<?>>();
	/**
	 * Adds a mask method.
	 *
	 * @param m
	 * @param mock
	 * @param mock a {@link net.anotheria.anoplass.api.mock.APIMaskMethod} object.
	 */
	public static void addMaskMethod(Method m, APIMaskMethod<?> mock){
		methods.put(m, mock);
	}
	/**
	 * Called by the masking api in order to retrive a masking method.
	 *
	 * @param m a {@link java.lang.reflect.Method} object.
	 * @return a {@link net.anotheria.anoplass.api.mock.APIMaskMethod} object.
	 */
	public static APIMaskMethod<?> getMaskMethod(Method m){
		return methods.get(m);
	}
	
	/**
	 * <p>reset.</p>
	 */
	public static void reset(){
		methods = new ConcurrentHashMap<Method, APIMaskMethod<?>>();
	}
}
