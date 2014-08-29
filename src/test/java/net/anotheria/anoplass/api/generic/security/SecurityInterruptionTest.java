package net.anotheria.anoplass.api.generic.security;

import net.anotheria.anoplass.api.APIFinder;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SecurityInterruptionTest {
	
	@Before public void init(){
		APIFinder.cleanUp();
		APIFinder.addAPIFactory(GuardedAPI.class, new GuardedAPIFactory());
		APIFinder.addAPIFactory(SecurityAPI.class, new DenyAllSecurityAPIImpl());
	}
	
	@Test public void testMethodCallInteruption(){
		GuardedAPI api = APIFinder.findAPI(GuardedAPI.class);
		assertFalse(api.mayIDoSomeAction());
	}

	@Test public void testMethodIsGuardedByException(){
		GuardedAPI api = APIFinder.findAPI(GuardedAPI.class);
		try{
			api.doSomeAction();
			fail("Exception should be thrown");
		}catch(SecurityException e){
			
		}
		
		//double check
		assertEquals(0, api.actionCounter());
	}
}
