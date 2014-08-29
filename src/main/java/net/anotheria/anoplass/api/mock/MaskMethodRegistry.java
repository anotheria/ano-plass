package net.anotheria.anoplass.api.mock;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for masking methods.
 * @author lrosenberg
 *
 */
public class MaskMethodRegistry {
	private static Map<Method, APIMaskMethod<?>> methods = new ConcurrentHashMap<Method, APIMaskMethod<?>>();
	/**
	 * Adds a mask method.
	 * @param m
	 * @param mock
	 */
	public static void addMaskMethod(Method m, APIMaskMethod<?> mock){
		methods.put(m, mock);
	}
	/**
	 * Called by the masking api in order to retrive a masking method.
	 * @param m
	 * @return
	 */
	public static APIMaskMethod<?> getMaskMethod(Method m){
		return methods.get(m);
	}
	
	public static void reset(){
		methods = new ConcurrentHashMap<Method, APIMaskMethod<?>>();
	}
}
