package net.anotheria.anoplass.api.generic.security;

import net.anotheria.anoplass.api.APIFinder;

import org.junit.Test;
import static org.junit.Assert.*;

public class SecurityByPassTest {
	@Test public void testDirectInvocation(){
		GuardedAPIImpl impl = new GuardedAPIImpl();
		doChecks(impl);
	}
	@Test public void testDisabledSecurityInvocation(){
		APIFinder.cleanUp();
		APIFinder.disableSecurity();
		APIFinder.addAPIFactory(GuardedAPI.class, new GuardedAPIFactory());
		doChecks(APIFinder.findAPI(GuardedAPI.class));
	}
	
	@Test public void testWithGrantAllSecurityAPI(){
		APIFinder.cleanUp();
		APIFinder.addAPIFactory(GuardedAPI.class, new GuardedAPIFactory());
		APIFinder.addAPIFactory(SecurityAPI.class, new GrantAllSecurityAPIImpl());
		doChecks(APIFinder.findAPI(GuardedAPI.class));
	}
	
	private void doChecks(GuardedAPI targetAPI){
		assertTrue(targetAPI.mayIDoSomeAction());
		assertEquals(0, targetAPI.actionCounter());
		targetAPI.doSomeAction();
		assertEquals(1, targetAPI.actionCounter());
	}
	
}
