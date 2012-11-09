package net.anotheria.anoplass.api.session;

import junit.framework.Assert;
import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoprise.sessiondistributor.*;
import net.anotheria.util.IdCodeGenerator;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


/**
 * Please  Note  that current test implementation is integration test, so please don't worry if some  test  will be executed with errors, cause
 * most of them can be based on small sleep time .. etc.
 */
public class APISessionManagerSingleAndSessionDistributionTest {

	private static int createCall = 0;
	private static int deleteCall = 0;
	private static int restoreCall = 0;
	private static int updateUserCall = 0;
	private static int updateEditorCall = 0;
	private static int addAttributeCall = 0;
	private static int removeAttributeCall = 0;
	private static int keepAliveCall = 0;

	private static SessionDistributorService service;

	@Before
	public void setup() {

		createCall = 0;
		deleteCall = 0;
		restoreCall = 0;
		updateUserCall = 0;
		updateEditorCall = 0;
		addAttributeCall = 0;
		removeAttributeCall = 0;
		keepAliveCall = 0;

		//config init

		APISessionDistributionConfig config = APISessionDistributionConfig.getInstance();
		config.setDistributionEnabled(true);
		config.setApiSessionEventSenderQueueSize(100);
		config.setApiSessionEventSenderQueueSleepTime(150);
		config.setDistributedSessionKeepAliveCallInterval(200); // 200 ms !
		config.setSessionDistributorEventReceiverQueueSize(100);
		config.setSessionDistributorEventReceiverQueueSleepTime(150);

		//service config init

		SessionDistributorServiceConfig serviceConfig = SessionDistributorServiceConfig.getInstance();
		serviceConfig.setDistributedSessionMaxAge(15000); // 10 seconds :)
		serviceConfig.setDistributedSessionsCleanUpInterval(25000);
		serviceConfig.setSessionDistributorEventQueueSize(500);
		serviceConfig.setSessionDistributorEventQueueSleepTime(150);


		service = new DistributorServiceWrapper();
		APISessionDistributionHelper.setSessionDistributorService(service);
		APICallContext.getCallContext().reset();

		//Reiniting integration!  To run  each tes method  separately!
		try {
			Method method = null;
			for (Method m : APISessionManager.class.getDeclaredMethods()) {
				if (m.getName().equals("configureIntegration")) {
					method = m;
					break;
				}
			}

			if (method != null) {
				method.setAccessible(true);
				method.invoke(APISessionManager.getInstance());
			}
		} catch (InvocationTargetException e) {
			Assert.fail(e.getMessage());
		} catch (IllegalAccessException e) {
			Assert.fail(e.getMessage());
		}


	}

	@Test
	public void testDistributedSessionCreationAndFlow() {
		String referenceId = "h3llkaTest";

		APISessionManager manager = APISessionManager.getInstance();


		for (String sessionId : manager.getSessionIds()) {
			manager.destroyAPISessionBySessionId(sessionId);
		}

		try {

			Assert.assertTrue("There are SOME sessions!!! count=[" + manager.getSessionCount() + "]", manager.getSessionCount() == 0);

			//created session
			APISessionImpl session = APISessionImpl.class.cast(manager.createSession(referenceId));

			//populate values! and attributes
			session.setCurrentEditorId("h3llka");
			session.setCurrentUserId("h3llka_test");
			//distributed
			session.setAttribute("h3llka_attribute", APISession.POLICY_DISTRIBUTED, "Test_tet_test!!!");
			//not serializable
			session.setAttribute("notDistributable_notSerializable", APISession.POLICY_DISTRIBUTED, new UnserializeableAttribute());
			//Local!
			session.setAttribute("123123123", APISession.POLICY_LOCAL, "test");

			Thread.sleep(500);

			Assert.assertEquals("Should be 1 call for create", 1, createCall);
			Assert.assertEquals("Should be 1 call for updateUser", 1, updateUserCall);
			Assert.assertEquals("Should be 1 call for updateEditor", 1, updateEditorCall);
			Assert.assertEquals("Should be 1 call for create attribute", 1, addAttributeCall);
			try {
				Assert.assertEquals("Should be 1 session in SessionDistributor", 1, service.getDistributedSessionNames().size());
			} catch (SessionDistributorServiceException e) {
				Assert.fail("Can't happen here!");
			}

			//creating keep alive call
			manager.getSession(session.getId());
			Thread.sleep(500);
			Assert.assertEquals("Should be 1 call for keep alive", 1, keepAliveCall);


			// Trying To restore session!!!  now!
			Assert.assertEquals("Should contains only 1 session ", 1, manager.getSessionCount());

			try {
				APISessionImpl restoredSession = APISessionImpl.class.cast(manager.restoreSession(session.getId(), referenceId));
				Assert.assertTrue("Should contains 1 distributed attribute", restoredSession.getAttribute("h3llka_attribute") != null);
				Assert.assertEquals("Should be 1 call for restore session", 1, restoreCall);
				Assert.assertEquals("Should contains only 1 session ", 1, manager.getSessionCount());
			} catch (APISessionRestoreException e) {
				Assert.fail("Should not happen!!!");
			}


			//remove attribute !!

			session.removeAttribute("h3llka_attribute");
			Thread.sleep(500);
			Assert.assertEquals("Should be 1 remove attribute call", 1, removeAttributeCall);

			Assert.assertEquals(1, manager.getSessionCount());

			manager.destroyAPISessionByReferenceId(referenceId);
			try {
				Assert.assertEquals("Should be 0 session in SessionDistributor", 0, service.getDistributedSessionNames().size());
			} catch (SessionDistributorServiceException e) {
				Assert.fail("Should not happen!!!");
			}
			Assert.assertEquals("Should be 1 call for delete session", 1, deleteCall);


		} catch (APISessionCreationException e) {
			Assert.fail("Should not happen!");
		} catch (InterruptedException e) {
			Assert.fail("Should not happen!");
		}


	}

	@Test
	public void testDistributionDisabled() {
		APISessionDistributionConfig.getInstance().setDistributionEnabled(false);
		String referenceId = "h3llkaTest";

		APISessionManager manager = APISessionManager.getInstance();
		try {
			//created session
			APISessionImpl session = APISessionImpl.class.cast(manager.createSession(referenceId));

			//populate values! and attributes
			session.setCurrentEditorId("h3llka");
			session.setCurrentUserId("h3llka_test");
			//distributed
			session.setAttribute("h3llka_attribute", APISession.POLICY_DISTRIBUTED, "Test_tet_test!!!");
			//not serializable
			session.setAttribute("notDistributable_notSerializable", APISession.POLICY_DISTRIBUTED, new UnserializeableAttribute());
			//Local!
			session.setAttribute("123123123", APISession.POLICY_LOCAL, "test");

			Thread.sleep(500);

			Assert.assertEquals("Should be 0 call for create", 0, createCall);
			Assert.assertEquals("Should be 0 call for updateUser", 0, updateUserCall);
			Assert.assertEquals("Should be 0 call for updateEditor", 0, updateEditorCall);
			Assert.assertEquals("Should be 0 call for create attribute", 0, addAttributeCall);
			try {
				Assert.assertEquals("Should be 0 session in SessionDistributor", 0, service.getDistributedSessionNames().size());
			} catch (SessionDistributorServiceException e) {
				Assert.fail("Can't happen here!");
			}

			//creating keep alive call
			manager.getSession(session.getId());
			Thread.sleep(500);
			Assert.assertEquals("Should be 0 call for keep alive", 0, keepAliveCall);


			// Trying To restore session!!!  now!

			try {
				APISessionImpl restoredSession = APISessionImpl.class.cast(manager.restoreSession(session.getId(), referenceId));
				Assert.fail("Should fail here!");
			} catch (APISessionRestoreException e) {
				// distribution is turned OFF!!!
			}

			//remove attribute !!

			session.removeAttribute("h3llka_attribute");
			Thread.sleep(500);
			Assert.assertEquals("Should be 0 remove attribute call", 0, removeAttributeCall);


			manager.destroyAPISessionBySessionId(session.getId());
			Thread.sleep(500);
			try {
				Assert.assertEquals("Should be 0 session in SessionDistributor", 0, service.getDistributedSessionNames().size());
			} catch (SessionDistributorServiceException e) {
				Assert.fail("Should not happen!!!");
			}
			Assert.assertEquals("Should be 0 call for delete session", 0, deleteCall);


		} catch (APISessionCreationException e) {
			Assert.fail("Should not happen!");
		} catch (InterruptedException e) {
			Assert.fail("Should not happen!");
		}

	}


	@SuppressWarnings({"NullableProblems"})
	@Test
	public void testErrorsAndExceptions() {

		//restore unexists Session!!
		try {
			SessionDistributorServiceConfig conf = SessionDistributorServiceConfig.getInstance();
			conf.setDistributedSessionMaxAge(1);
			conf.setDistributedSessionsCleanUpInterval(100);
			service = new DistributorServiceWrapper();
			APISessionDistributionHelper.setSessionDistributorService(service);

			String id = service.createDistributedSession("123");
			//waiting for clean!
			Thread.sleep(500);
			try {
				Assert.assertNull("Can't be restored!!!", APISessionManager.getInstance().restoreSession(id, "h3llka"));
				Assert.fail("Should not happen!!!  There is no such session!!!!");
			} catch (APISessionRestoreException e) {
			}

			//Badly configured service!!!
			APISessionDistributionHelper.setSessionDistributorService(null);
			try {
				Assert.assertNotNull(APISessionManager.getInstance().createSession("123"));
				Assert.assertFalse(APISessionDistributionHelper.isSessionDistributorServiceConfigured());
			} catch (Exception e) {
				Assert.fail("Should not happen! " + e.getMessage());
			}


		} catch (SessionDistributorServiceException e) {
			//
		} catch (InterruptedException e) {
			//
		}


	}

	/**
	 * Obtain session --- from Distributed session - using request parameter!
	 */
	@Test
	public void testObtainSessionCase1() {
		APISessionDistributionConfig.getInstance().setDistributionEnabled(true);
		String referenceId = "h3llkaTest_obtainCase1";

		final String distributedSessionId = createRemoteSession();

		APISessionManager manager = APISessionManager.getInstance();


		//so session does not exists!   let's  restore!!!
		APISession session = null;
		try {
			restoreCall = 0;
			session = manager.obtainSession(referenceId, null, null, distributedSessionId, "", "", null, "");
			Assert.assertNotNull(session);
			Assert.assertEquals(session.getId(), distributedSessionId);
			Assert.assertEquals(APISessionImpl.class.cast(session).getReferenceId(), referenceId);

			Assert.assertEquals("There should be  exactly 1  restore call", restoreCall, 1);

		} catch (APISessionCreationException e) {
			Assert.fail(e.getMessage());
		}


		// now  let's try to  obtain  same  session  once again!!! AS session  already restored  there should not be restore call's
		try {
			restoreCall = 0;
			session = manager.obtainSession(referenceId, session.getId(), null, distributedSessionId, "", "", null, "");
			Assert.assertNotNull(session);
			Assert.assertEquals(session.getId(), distributedSessionId);
			Assert.assertEquals(APISessionImpl.class.cast(session).getReferenceId(), referenceId);

			Assert.assertEquals("There should be  exactly 0  restore call's ! Cause  session allraedy restored!", restoreCall, 0);

		} catch (APISessionCreationException e) {
			Assert.fail(e.getMessage());
		}


		// now  let's try to  obtain  same  session  once again!!! with disabled Distribution!
		try {
			APISessionDistributionConfig.getInstance().setDistributionEnabled(false);
			restoreCall = 0;
			APISession session2 = manager.obtainSession(referenceId, "", null, distributedSessionId, "", "", null, "");
			Assert.assertNotNull(session2);
			Assert.assertFalse(" False -  cause  new  session was  created!!!!", session2.getId().equals(distributedSessionId));
			Assert.assertEquals(APISessionImpl.class.cast(session2).getReferenceId(), referenceId);

			Assert.assertEquals("There should be  exactly 0  restore call's ! Cause  session allraedy restored!", restoreCall, 0);
		} catch (APISessionCreationException e) {
			Assert.fail(e.getMessage());
		}

		APISessionDistributionConfig.getInstance().setDistributionEnabled(true);

	}


	/**
	 * Restore session using - SesionId from Cookies.
	 */
	@Test
	public void testObtainSession2() {
		APISessionDistributionConfig.getInstance().setDistributionEnabled(true);
		String referenceId = "h3llkaTest_obtainCase2";

		final String distributedSessionIdFromCookies = createRemoteSession();

		APISessionManager manager = APISessionManager.getInstance();


		//so session does not exists!   let's  restore!!!
		APISession session = null;
		try {
			restoreCall = 0;
			session = manager.obtainSession(referenceId, null, distributedSessionIdFromCookies, null, "", "", null, "");
			Assert.assertNotNull(session);
			Assert.assertEquals(session.getId(), distributedSessionIdFromCookies);
			Assert.assertEquals(APISessionImpl.class.cast(session).getReferenceId(), referenceId);

			Assert.assertEquals("There should be  exactly 1  restore call", restoreCall, 1);

		} catch (APISessionCreationException e) {
			Assert.fail(e.getMessage());
		}

		// now  let's try to  obtain  same  session  once again!!! AS session  already restored  there should not be restore call's
		try {
			restoreCall = 0;
			session = manager.obtainSession(referenceId, session.getId(), distributedSessionIdFromCookies, null, "", "", null, "");
			Assert.assertNotNull(session);
			Assert.assertEquals(session.getId(), distributedSessionIdFromCookies);
			Assert.assertEquals(APISessionImpl.class.cast(session).getReferenceId(), referenceId);

			Assert.assertEquals("There should be  exactly 0  restore call's ! Cause  session allraedy restored!", restoreCall, 0);

		} catch (APISessionCreationException e) {
			Assert.fail(e.getMessage());
		}

		// now  let's try to  obtain  same  session  once again!!! with disabled Distribution!
		try {
			APISessionDistributionConfig.getInstance().setDistributionEnabled(false);
			restoreCall = 0;
			APISession session2 = manager.obtainSession(referenceId, "", distributedSessionIdFromCookies, null, "", "", null, "");
			Assert.assertNotNull(session2);
			Assert.assertFalse(" False -  cause  new  session was  created!!!!", session2.getId().equals(distributedSessionIdFromCookies));
			Assert.assertEquals(APISessionImpl.class.cast(session2).getReferenceId(), referenceId);

			Assert.assertEquals("There should be  exactly 0  restore call's ! Cause  session allraedy restored!", restoreCall, 0);
		} catch (APISessionCreationException e) {
			Assert.fail(e.getMessage());
		}

		APISessionDistributionConfig.getInstance().setDistributionEnabled(true);
	}


	/**
	 * Simply creating and obtaining  new API session. With disabled and enabled dist.
	 */
	@Test
	public void obtainSessionByCreationNewOneTest() {
		String referenceId = "h3llka_reference_simpleCretion";

		APISessionManager manager = APISessionManager.getInstance();
		//so session does not exists!   let's  restore!!!
		APISession session = null;
		//disable distribution!  does not required here!
		APISessionDistributionConfig.getInstance().setDistributionEnabled(false);

		try {
			session = manager.obtainSession(referenceId, null, "", "", "", "", null, "");
			Assert.assertNotNull(session);
			Assert.assertEquals(APISessionImpl.class.cast(session).getReferenceId(), referenceId);
		} catch (APISessionCreationException e) {
			Assert.fail(e.getMessage());
		}

		//obtain now!


		try {
			APISession session2 = manager.obtainSession(referenceId, session.getId(), "", "", "", "", null, "");
			Assert.assertNotNull(session2);
			Assert.assertEquals(APISessionImpl.class.cast(session2).getReferenceId(), referenceId);
			Assert.assertEquals(session, session2);
		} catch (APISessionCreationException e) {
			Assert.fail(e.getMessage());
		}
	}


	/**
	 * Creates  distributed session.
	 *
	 * @return
	 */
	private String createRemoteSession() {
		try {
			return service.createDistributedSession(IdCodeGenerator.generateCode(20));
		} catch (SessionDistributorServiceException e) {
			Assert.fail("Should not happens! " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Obtain session --- from Distributed session - using cookies parameter!
	 */
	@Test
	public void testObtainSessionCase2() {

	}

	/**
	 * Obtain session --- using!  using cookies parameter!  - Distr disabled
	 */
	@Test
	public void testObtainSessionCase21() {

	}


	/**
	 * Obtain session. Create New one.
	 */

	/**
	 * Obtain session. Already existing.
	 */


	/**
	 * Wrapper for default  SessionDistributorServiceImpl, simply dor current test.
	 */
	private class DistributorServiceWrapper extends SessionDistributorServiceImpl {
		@Override
		public String createDistributedSession(String sessionId) throws SessionDistributorServiceException {
			String result = super.createDistributedSession(sessionId);    //To change body of overridden methods use File | Settings | File Templates.
			createCall++;
			return result;
		}

		@Override
		public void deleteDistributedSession(String name) throws SessionDistributorServiceException {
			super.deleteDistributedSession(name);    //To change body of overridden methods use File | Settings | File Templates.
			deleteCall++;
		}

		@Override
		public DistributedSessionVO restoreDistributedSession(String name, String callerId) throws SessionDistributorServiceException {
			DistributedSessionVO result = super.restoreDistributedSession(name, callerId);    //To change body of overridden methods use File | Settings | File
			restoreCall++;
			return result;
			// Templates.
		}

		@SuppressWarnings({"DefaultFileTemplate"})
		@Override
		public List<String> getDistributedSessionNames() throws SessionDistributorServiceException {
			return super.getDistributedSessionNames();    //To change body of overridden methods use File | Settings | File Templates.
		}

		@Override
		public void updateSessionUserId(String sessionName, String userId) throws SessionDistributorServiceException {
			try {
				super.updateSessionUserId(sessionName, userId);    //To change body of overridden methods use File | Settings | File Templates.
			} catch (Exception e) {
				//ignore
			}
			updateUserCall++;
		}

		@Override
		public void updateSessionEditorId(String sessionName, String editorId) throws SessionDistributorServiceException {
			super.updateSessionEditorId(sessionName, editorId);    //To change body of overridden methods use File | Settings | File Templates.
			updateEditorCall++;
		}

		@Override
		public void addDistributedAttribute(String sessionName, DistributedSessionAttribute attribute) throws SessionDistributorServiceException {
			super.addDistributedAttribute(sessionName, attribute);    //To change body of overridden methods use File | Settings | File Templates.
			addAttributeCall++;
		}

		@Override
		public void removeDistributedAttribute(String sessionName, String attributeName) throws SessionDistributorServiceException {
			super.removeDistributedAttribute(sessionName, attributeName);    //To change body of overridden methods use File | Settings | File Templates.
			removeAttributeCall++;
		}

		@Override
		public void keepDistributedSessionAlive(String sessionName) throws SessionDistributorServiceException {
			super.keepDistributedSessionAlive(sessionName);    //To change body of overridden methods use File | Settings | File Templates.
			keepAliveCall++;
		}
	}


	public static class UnserializeableAttribute {
		@SuppressWarnings("unused")
		private String unused;
	}
}
