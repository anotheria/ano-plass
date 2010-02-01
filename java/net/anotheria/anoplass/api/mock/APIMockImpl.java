package net.anotheria.anoplass.api.mock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.anotheria.anoplass.api.API;

import org.apache.log4j.Logger;
/**
 * This is an api implementation for on the fly api construction for mocking.
 * For each method called on this object it performs a lookup in the MockAPIRegistry for the corresponding method impl. This object is used to construct an api on the fly. 
 * @author lrosenberg
 */
public class APIMockImpl<T extends API> implements API, InvocationHandler{
	/**
	 * Mocked API interface.
	 */
	private Class<T> mockedClazz;
	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(APIMockImpl.class);
	/**
	 * Creates a new mocked api.
	 * @param aMockedClazz
	 */
	public APIMockImpl(Class<T> aMockedClazz){
		mockedClazz = aMockedClazz;
	}

	@Override
	public void deInit() {
	}

	@Override
	public void init() {
	}
	
	@SuppressWarnings("unchecked")
	public T createAPIProxy(){
		return (T)Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{mockedClazz}, this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		if (method.getDeclaringClass().equals(mockedClazz) || method.getDeclaringClass().equals(API.class))
			return invokeOnMock(proxy, method, args);
		
		return method.invoke(this, args);
	}

	/**
	 * Invokes the method on mock.
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 */
	private Object invokeOnMock(Object proxy, Method method, Object[] args){
		if (log.isDebugEnabled())
			log.debug("Called method: "+method+" in "+mockedClazz.getName());
		
		APIMockMethod meth = MockMethodRegistry.getMockMethod(method);
		if (meth==null)
			throw new IllegalArgumentException("Method "+method+" is not mocked");
		
		return meth.invoke(method, args);
		
	}

}
