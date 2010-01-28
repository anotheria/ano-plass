package net.anotheria.anoplass.api.mock;

import java.lang.reflect.Method;

/**
 * Mocking method which performs no operation. Used to render required but not implemented methods in the mocked api effectless.
 * For example if you test some code which calls XYZApi.notifyFoo() but you don't have an XYZApi instance in your setup, or 
 * don't want to implement notifyFoo, you simply mock it with NoopMockMethod and the call to XYZApi.notifyFoo() will neither fail nor have any effect.
 * @author lrosenberg
 *
 */
public class NoopMockMethod implements APIMockMethod{

	@Override
	public Object invoke(Method method, Object[] args) {
		return null;
	}

}
