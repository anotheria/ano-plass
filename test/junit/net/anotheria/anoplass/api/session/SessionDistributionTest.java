package net.anotheria.anoplass.api.session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoplass.api.session.APISession;
import net.anotheria.anoplass.api.session.APISessionDistributionException;
import net.anotheria.anoplass.api.session.APISessionDistributionHelper;
import net.anotheria.anoplass.api.session.APISessionImpl;
import net.anotheria.anoplass.api.session.APISessionManager;
import net.anotheria.anoprise.mocking.MockFactory;
import net.anotheria.anoprise.mocking.Mocking;
import net.anotheria.anoprise.sessiondistributor.NoSuchDistributedSessionException;
import net.anotheria.anoprise.sessiondistributor.SessionAttribute;
import net.anotheria.anoprise.sessiondistributor.SessionDistributorService;
import net.anotheria.anoprise.sessiondistributor.SessionDistributorServiceException;
import net.anotheria.util.IdCodeGenerator;

public class SessionDistributionTest {
	
	@Before public void setup(){
		SessionDistributorService service = MockFactory.createMock(SessionDistributorService.class, new SessionDistributorMocking());
		APISessionDistributionHelper.setSessionDistributorService(service);
		APICallContext.getCallContext().reset();
		
	}
	
	@Test public void testSessionDistribution() throws APISessionDistributionException{
		APISession source = APISessionManager.getInstance().createSession("foo1");
		APISession target = APISessionManager.getInstance().createSession("foo2");
		
		
		((APISessionImpl)source).setCurrentEditorId("editor");
		((APISessionImpl)source).setCurrentUserId("user");
		//those both should not survive distribution
		source.setAttribute("local", "local");
		source.setAttribute("expiring", APISession.POLICY_AUTOEXPIRE, "expiring");
		
		source.setAttribute("distributed", APISession.POLICY_DISTRIBUTED, "distributed");
		source.setAttribute("distributedandexpiring", APISession.POLICY_DISTRIBUTED | APISession.POLICY_AUTOEXPIRE, "distributedandexpiring");
		source.setAttribute("distributedandpersistent", APISession.POLICY_DISTRIBUTED | APISession.POLICY_PERSISTENT, "distributedandpersistent");
		
		source.setAttribute("distributedbutnotseriliazeable", APISession.POLICY_DISTRIBUTED, new UnserializeableAttribute());
		
		String name = APISessionDistributionHelper.distributeSession(source);
		assertNotNull(name);
		
		APISessionDistributionHelper.restoreSession(name, target);
		
		assertNull(target.getAttribute("local"));
		assertNull(target.getAttribute("expiring"));
		assertNull(target.getAttribute("distributedbutnotseriliazeable"));
		assertNotNull(target.getAttribute("distributed"));
		assertNotNull(target.getAttribute("distributedandexpiring"));
		assertNotNull(target.getAttribute("distributedandpersistent"));
		
		assertEquals("user", target.getCurrentUserId());
		assertEquals("editor", target.getCurrentEditorId());
		
	}
	
	@Test public void testErrorMapping() throws Exception{
		try{
			APISessionDistributionHelper.restoreSession("foo", APISessionManager.getInstance().createSession(""));
			fail("Exception expected");
		}catch(APISessionDistributionException e){
			
		}

		try{
			APISessionDistributionHelper.restoreSession("foo", null);
			fail("Exception expected");
		}catch(AssertionError e){
			
		}

		try{
			APISessionDistributionHelper.setSessionDistributorService(null);
			APISessionDistributionHelper.restoreSession("foo", null);
			fail("IllegalStateException expected");
		}catch(IllegalStateException e){
			
		}

	}
	
	public static class SessionDistributorMocking implements Mocking{
		
		private Map<String, List<SessionAttribute>> storage = new HashMap<String, List<SessionAttribute>>();
		
		public String createDistributedSession(List<SessionAttribute> attributes) throws SessionDistributorServiceException{
			String name = IdCodeGenerator.generateCode(30);
			storage.put(name, attributes);
			return name;
		}
		
		public List<SessionAttribute> getAndDeleteDistributedSession(String name) throws SessionDistributorServiceException{
			List<SessionAttribute> ret = storage.remove(name);
			if (ret==null)
				throw new NoSuchDistributedSessionException(name);
			return ret;
		}
	}
	
	public static class UnserializeableAttribute{
		@SuppressWarnings("unused")private String unused;
	}
}
