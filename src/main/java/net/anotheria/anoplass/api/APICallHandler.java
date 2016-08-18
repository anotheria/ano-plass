package net.anotheria.anoplass.api;

import net.anotheria.anoplass.api.generic.security.EnsurePermitted;
import net.anotheria.anoplass.api.generic.security.InterceptIfNotPermitted;
import net.anotheria.anoplass.api.generic.security.SecurityAPI;
import net.anotheria.anoplass.api.generic.security.SecurityInvocationHandler;
import net.anotheria.anoplass.api.generic.security.SecurityObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Invocation handler used to proxy APIImpls from the caller.
 *
 * @param <T> an api.
 * @author lrosenberg
 * @version $Id: $Id
 */
public class APICallHandler<T extends API> implements InvocationHandler {

	/**
	 * Instance of the target api implementation.
	 */
	private T target;
	/**
	 * Target method cache.
	 */
	private ConcurrentMap<Method, MethodInfo> methodMap;
	/**
	 * Link to the security api.
	 */
	private SecurityAPI securityAPI;
	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(APICallHandler.class);

	/**
	 * Creates a new handler for the given implementation.
	 *
	 * @param impl
	 */
	APICallHandler(T impl) {
		target = impl;
		methodMap = new ConcurrentHashMap<Method, APICallHandler.MethodInfo>();

		if (!(impl instanceof SecurityAPI)) {
			try {
				securityAPI = APIFinder.findAPI(SecurityAPI.class);
			} catch (NoAPIFactoryException e) {
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		MethodInfo info = getMethodInfo(method);

		if (info.ensurePermitted()) {
			if (securityAPI == null) {
				log.warn("invoke(..., " + method + ", ...), can't find security api, probably misconfigured, security checks are disabled!");
			} else {
				securityAPI.ensureIsAllowedTo(info.ensurePermittedAnn.action(), createSecuritySubject(), createSecurityObject());
			}
		}

		if (info.interceptIfNotPermitted()) {
			//System.out.println("Intercept not permitted "+method);
			//first check whether the action is permitted or not
			if (securityAPI == null) {
				log.warn("invoke(..., " + method + ", ...), can't find security api, probably misconfigured, security checks are disabled!");
			} else {
				boolean permitted = securityAPI.isAllowedTo(info.interceptIfNotPermittedAnn.action(), createSecuritySubject(), createSecurityObject());
				//System.out.println("Permitted: "+permitted);
				if (!permitted) {
					Object interceptedValue = info.securityHandler.getInterceptedValue(method, args, target);
					//System.out.println("\t intercepted "+interceptedValue);
					return interceptedValue;
				}
			}
		}

		try {
			return method.invoke(target, args);
		} catch (InvocationTargetException t) {
			throw t.getCause();
		}
	}

	private SecurityObject createSecuritySubject() {
		return new SecurityObject();
	}

	private SecurityObject createSecurityObject() {
		return new SecurityObject();
	}

	private MethodInfo getMethodInfo(Method m) {
		MethodInfo fromCache = methodMap.get(m);
		if (fromCache != null) {
			return fromCache;
		}

		MethodInfo info = new MethodInfo(m);
		info.ensurePermittedAnn = m.getAnnotation(EnsurePermitted.class);
		info.interceptIfNotPermittedAnn = m.getAnnotation(InterceptIfNotPermitted.class);
		try {
			if (info.interceptIfNotPermitted())
				info.securityHandler = info.interceptIfNotPermittedAnn.handler().newInstance();
		} catch (InstantiationException e) {
			log.error("getMethodInfo(" + m + ")", e);
			throw new IllegalStateException("Configured security handler can't be instantiated " + info.interceptIfNotPermittedAnn.handler(), e);
		} catch (IllegalAccessException e) {
			log.error("getMethodInfo(" + m + ")", e);
			throw new IllegalStateException("Configured security handler can't be instantiated " + info.interceptIfNotPermittedAnn.handler(), e);
		}
		methodMap.put(m, info);
		return info;

	}

	static <T extends API> T createProxy(Class<T> identifier, Class<? extends API>[] interfaces, T impl) {

		APICallHandler<T> handler = new APICallHandler<T>(impl);
		@SuppressWarnings("unchecked")
		T ret = (T) Proxy.newProxyInstance(identifier.getClassLoader(), interfaces, handler);

		return ret;
	}

	/**
	 * Helper class for MethodInfoCaching.
	 *
	 * @author lrosenberg
	 */
	static class MethodInfo {
		/**
		 * Instance counter for all instances.
		 */
		private static final AtomicInteger instanceCounter = new AtomicInteger(0);
		/**
		 * Number of the current instance.
		 */
		private int instanceNumber;
		/**
		 * Cached method.
		 */
		private Method method;
		/**
		 * Ensure permitted annotation, if this method has one.
		 */
		EnsurePermitted ensurePermittedAnn;
		/**
		 * InterceptIfNotPermitted, if this method has one.
		 */
		InterceptIfNotPermitted interceptIfNotPermittedAnn;
		/**
		 * Configured SecurityInvocationHandler in the annotation.
		 */
		SecurityInvocationHandler securityHandler;

		public MethodInfo(Method aMethod) {
			instanceNumber = instanceCounter.incrementAndGet();
			method = aMethod;
		}

		@Override
		public String toString() {
			return "MethodInfo " + method + " " + instanceNumber + " ensurePermitted: " + ensurePermitted() + ", interceptIfNotPermitted: " +
					interceptIfNotPermitted();
		}

		boolean ensurePermitted() {
			return ensurePermittedAnn != null;
		}

		boolean interceptIfNotPermitted() {
			return interceptIfNotPermittedAnn != null;
		}
	}
}
