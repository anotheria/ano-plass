package net.anotheria.anoplass.api.mock;

import net.anotheria.anoplass.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A registry for mocking methods.
 * @author another
 *
 */
public class MockMethodRegistry {
	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(MockMethodRegistry.class);
	/**
	 * MockMethodRegistry 'methods'. Actually holder for Mocked methods.
	 */
	private static Map<Method, APIMockMethod> methods = new ConcurrentHashMap<>();
	/**
	 * init.
	 */
	static{
		reset();
	}

	/**
	 * Adding mock method to the registry.
	 * @param m method
	 * @param mock mocked implementation
	 */
	public static void addMockMethod(Method m, APIMockMethod mock){
		methods.put(m, mock);
	}

	/**
	 * Return mocked method.
	 * @param m method itself
	 * @return mocked implementation from registry
	 */
	public static APIMockMethod getMockMethod(Method m){
		return methods.get(m);
	}

	/**
	 * Reset.
	 */
	public static void reset(){
		try{
			Method init = API.class.getMethod("init");
			addMockMethod(init, new NoopMockMethod());
		}catch(NoSuchMethodException e){
			log.error("Someone changed the api signature!", e);
		}

	}
}
