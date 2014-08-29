package net.anotheria.anoplass.api.generic.security;

import net.anotheria.anoplass.api.APIFactory;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.AbstractAPIImpl;
import net.anotheria.util.StringUtils;

import org.junit.Test;
import static org.junit.Assert.*;

public class FancyGuardTest {
	
	private static class FancyGuardedAPIImpl extends AbstractAPIImpl implements FancyGuardedAPI{
		@Override
		public int getAnswerToLifeAndEverything() {
			return 42;
		}

		@Override
		public String sayHello() {
			return "hello";
		}
	}
	
	@Test public void testUnSecured(){
		APIFinder.cleanUp();
		APIFinder.addAPIFactory(SecurityAPI.class, new GrantAllSecurityAPIImpl());
		APIFinder.addAPIFactory(FancyGuardedAPI.class, new APIFactory<FancyGuardedAPI>() {
			public FancyGuardedAPI createAPI(){
				return new FancyGuardedAPIImpl();
			}
		});
		
		FancyGuardedAPI api = APIFinder.findAPI(FancyGuardedAPI.class);
		assertEquals(42, api.getAnswerToLifeAndEverything());
		assertEquals("hello", api.sayHello());
	}
	@Test public void testSecured(){
		APIFinder.cleanUp();
		APIFinder.addAPIFactory(SecurityAPI.class, new DenyAllSecurityAPIImpl());
		APIFinder.addAPIFactory(FancyGuardedAPI.class, new APIFactory<FancyGuardedAPI>() {
			public FancyGuardedAPI createAPI(){
				return new FancyGuardedAPIImpl();
			}
		});
		
		FancyGuardedAPI api = APIFinder.findAPI(FancyGuardedAPI.class);
		assertEquals(42*2, api.getAnswerToLifeAndEverything());
		assertEquals(StringUtils.reverseString("hello"), api.sayHello());
	}
}
