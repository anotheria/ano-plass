package net.anotheria.anoplass.api.mock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.anotheria.anoplass.api.API;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.APIInitException;

import org.apache.log4j.Logger;

/**
 * This is a masking api implementation which is used as wrapper to an underlying api implementation and allows masking of methods.
 * @see MaskMethodRegistry
 * @see APIFinder
 * @author lrosenberg
 */
public class APIMaskImpl <T extends API> implements API, InvocationHandler{
	/**
	 * Underlying 'masked' api impl.
	 */
	private T maskedInstance;
	/**
	 * Clazz file of the api.
	 */
	private Class<T> maskedClazz;
	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(APIMockImpl.class);
	/**
	 * Created a new masked api.
	 * @param aMaskedInstance
	 * @param aMaskedClazz
	 */
	public APIMaskImpl(T aMaskedInstance, Class<T> aMaskedClazz){
		maskedInstance = aMaskedInstance;
		maskedClazz = aMaskedClazz;
	}

	@Override
	public void deInit() {
		maskedInstance.deInit();
	}

	@Override
	public void init() throws APIInitException {
		maskedInstance.init();
	}
	
	@SuppressWarnings("unchecked")
	public T createAPIProxy(){
		return (T)Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{maskedClazz}, this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		try{
			if (method.getDeclaringClass().equals(maskedClazz) || method.getDeclaringClass().equals(API.class))
				return invokeOnMasked(proxy, method, args);
	
			return method.invoke(this, args);
		}catch(InvocationTargetException e){
			throw e.getCause();
		}
	}

	/**
	 * Called for each potentially masked methods. This method looks up the method signature in the MaskMethodRegistry. If it finds a fitting method, the method will be called, otherwise the 
	 * call will be performed on the underlying implementation.
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	private Object invokeOnMasked(Object proxy, Method method, Object[] args) throws Throwable{
		if (log.isDebugEnabled())
			log.debug("Called method: "+method+" in "+maskedClazz.getName());
		
		@SuppressWarnings("unchecked")
		APIMaskMethod<T> meth = (APIMaskMethod<T>)MaskMethodRegistry.getMaskMethod(method);
		if (meth==null){
			//method not masked
			return method.invoke(maskedInstance, args);
		}
		//method masked
		return meth.invoke(method, args, maskedInstance);
		
	}

}
