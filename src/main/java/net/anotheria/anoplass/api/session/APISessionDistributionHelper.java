package net.anotheria.anoplass.api.session;

import net.anotheria.anoprise.sessiondistributor.DistributedSessionAttribute;
import net.anotheria.anoprise.sessiondistributor.DistributedSessionVO;
import net.anotheria.anoprise.sessiondistributor.NoSuchDistributedSessionException;
import net.anotheria.anoprise.sessiondistributor.SessionDistributorService;
import net.anotheria.anoprise.sessiondistributor.SessionDistributorServiceException;
import net.anotheria.anoprise.sessiondistributor.SessionsCountLimitReachedSessionDistributorServiceException;
import net.anotheria.net.util.ByteArraySerializer;
import org.distributeme.core.exception.DistributemeRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Utility class which helps with Session Distribution.
 *
 * @author lrosenberg
 */
public final class APISessionDistributionHelper {
	/**
	 * SessionDistributor service.
	 */
	private static SessionDistributorService distributorService;
	/**
	 * {@link Logger}.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(APISessionDistributionHelper.class);

	/**
	 * Set method for SessionDistributor service.
	 *
	 * @param aSessionDistributorService SessionDistributorService instance
	 */
	public static void setSessionDistributorService(SessionDistributorService aSessionDistributorService) {
		distributorService = aSessionDistributorService;
	}


	/**
	 * Restores previously distributed session.
	 *
	 * @param distributedSessionName the name of the distributed session.
	 * @param callServiced           id of caller service
	 * @return {@link APISession}
	 * @throws APISessionDistributionException
	 *          on errors from backend service
	 */
	public static APISession restoreSession(String distributedSessionName, String callServiced) throws APISessionDistributionException {
		if (!isSessionDistributorServiceConfigured()) {
			LOG.warn("There is nothing to restore! SD - service is not configured! Relying on defaults.");
			return null;
		}

		DistributedSessionVO distributedSession;
		try {
			distributedSession = distributorService.restoreDistributedSession(distributedSessionName, callServiced);
		} catch (SessionDistributorServiceException e) {
			LOG.warn("restoreSession(" + distributedSessionName + "," + callServiced + ")failed. Cause " + e.getMessage());
			throw new APISessionDistributionException(e);
		} catch (DistributemeRuntimeException dMeR) {
			//transport layer runtime!!
			LOG.warn("restoreSession(" + distributedSessionName + "," + callServiced + ") failed. [" + dMeR.getClass().getName() + "] " + dMeR.getMessage());
			if (LOG.isDebugEnabled())
				LOG.debug("", dMeR);
			//Continue work in local mode!
			return null;
		}


		APISessionImpl sessionImpl = new APISessionImpl(distributedSession.getName());
		// next to lines won't be  populated back to Distributed session
		sessionImpl.setCurrentEditorId(distributedSession.getEditorId());
		sessionImpl.setCurrentUserId(distributedSession.getUserId());

		for (DistributedSessionAttribute attribute : distributedSession.getDistributedAttributes().values()) {
			try {
				AttributeWrapper wrapper = (AttributeWrapper) ByteArraySerializer.deserializeObject(attribute.getData());
				sessionImpl.setAttributeWrapper(wrapper);
			} catch (IOException e) {
				LOG.warn("restoreSession(" + distributedSessionName + "," + callServiced + ") FAILED. exception occurred attempting to deSerialize this attribute: " + attribute, e);
			}
		}
		return sessionImpl;

	}

	/**
	 * Add attribute to Distributed session.
	 * If incoming attribute policy  - is not DISTRIBUTED or attribute is not serializable - nothing will be done!
	 *
	 * @param sessionName    name of the  distributed session
	 * @param attributeToAdd attributeWrapper
	 */
	public static void addAttributeToDistributedSession(String sessionName, AttributeWrapper attributeToAdd) {
		if (!isSessionDistributorServiceConfigured())
			return;

		if (!PolicyHelper.isDistributed(attributeToAdd.getPolicy()))
			return;
		if (!attributeToAdd.isSerializable()) {
			LOG.warn("Attribute " + attributeToAdd.getKey() + " is marked as distributed but is not serializable, skipped.");
			return;
		}
		try {
			distributorService.addDistributedAttribute(sessionName, new DistributedSessionAttribute(attributeToAdd.getKey(), ByteArraySerializer.serializeObject(attributeToAdd)));
		} catch (IOException e) {
			LOG.error("addAttributeToDistributedSession(" + sessionName + "," + attributeToAdd + ") failed.", e);
		} catch (NoSuchDistributedSessionException e) {
			LOG.warn("addAttributeToDistributedSession(" + sessionName + "," + attributeToAdd + ") failed. Cause" + e.getMessage());
		} catch (SessionDistributorServiceException e) {
			LOG.error("addAttributeToDistributedSession(" + sessionName + "," + attributeToAdd + ") failed.", e);
		} catch (DistributemeRuntimeException dMeR) {
			//transport layer runtime!!
			LOG.warn("addAttributeToDistributedSession(" + sessionName + "," + attributeToAdd + ") failed [" + dMeR.getClass().getName() + "] " + dMeR.getMessage());
			if (LOG.isDebugEnabled())
				LOG.debug("",dMeR);
		}
	}

	/**
	 * Remove  attribute from distributed session.
	 *
	 * @param sessionName   name of the distributed session
	 * @param attributeName name of attribute to be removed
	 */
	public static void removeAttributeFromDistributedSession(String sessionName, String attributeName) {
		if (!isSessionDistributorServiceConfigured())
			return;
		try {
			distributorService.removeDistributedAttribute(sessionName, attributeName);
		} catch (NoSuchDistributedSessionException e) {
			LOG.warn("removeAttributeFromDistributedSession(" + sessionName + "," + attributeName + ") failed. Cause " + e.getMessage());
		} catch (SessionDistributorServiceException e) {
			LOG.error("removeAttributeFromDistributedSession(" + sessionName + "," + attributeName + ") failed.", e);
		} catch (DistributemeRuntimeException dMeR) {
			//transport layer runtime!!
			LOG.warn("removeAttributeFromDistributedSession(" + sessionName + "," + attributeName + ") failed [" + dMeR.getClass().getName() + "] " + dMeR.getMessage());
			if (LOG.isDebugEnabled())
				LOG.debug("",dMeR);
		}
	}

	/**
	 * Update distributed session userId.
	 *
	 * @param sessionName name of session
	 * @param userId      user id
	 */
	public static void updateDistributedSessionUserId(String sessionName, String userId) {
		if (!isSessionDistributorServiceConfigured())
			return;
		try {
			distributorService.updateSessionUserId(sessionName, userId);
		} catch (NoSuchDistributedSessionException e) {
			LOG.warn("updateDistributedSessionUserId(" + sessionName + "," + userId + ") failed. Cause " + e.getMessage());
		} catch (SessionDistributorServiceException e) {
			LOG.error("updateDistributedSessionUserId(" + sessionName + "," + userId + ") failed.", e);
		} catch (DistributemeRuntimeException dMeR) {
			//transport layer runtime!!
			LOG.warn("updateDistributedSessionUserId(" + sessionName + "," + userId + ") failed [" + dMeR.getClass().getName() + "] " + dMeR.getMessage());
			if (LOG.isDebugEnabled())
				LOG.debug("",dMeR);
		}
	}

	/**
	 * Update distributed session editorId.
	 *
	 * @param sessionName name of session (id)
	 * @param editor      editor id
	 */
	public static void updateDistributedSessionEditorId(String sessionName, String editor) {
		if (!isSessionDistributorServiceConfigured())
			return;
		try {
			distributorService.updateSessionEditorId(sessionName, editor);
		} catch (NoSuchDistributedSessionException e) {
			LOG.warn("updateDistributedSessionEditorId( " + sessionName + "," + editor + ") failed. Cause " + e.getMessage());
		} catch (SessionDistributorServiceException e) {
			LOG.error("updateDistributedSessionEditorId( " + sessionName + "," + editor + ") failed.", e);
		} catch (DistributemeRuntimeException dMeR) {
			//transport layer runtime!!
			LOG.warn("updateDistributedSessionEditorId(" + sessionName + "," + editor + ") failed [" + dMeR.getClass().getName() + "] " + dMeR.getMessage());
			if (LOG.isDebugEnabled())
				LOG.debug("",dMeR);
		}
	}


	/**
	 * Keep distributed session alive call.
	 *
	 * @param sessionName session id
	 */
	public static void keepSessionAliveCall(String sessionName) {
		if (!isSessionDistributorServiceConfigured())
			return;
		try {
			distributorService.keepDistributedSessionAlive(sessionName);
		} catch (NoSuchDistributedSessionException e) {
			LOG.warn("keepSessionAliveCall( " + sessionName + ") failed. Cause " + e.getMessage());
		} catch (SessionDistributorServiceException e) {
			LOG.error("keepSessionAliveCall( " + sessionName + ") failed.", e);
		} catch (DistributemeRuntimeException dMeR) {
			//transport layer runtime!!
			LOG.warn("keepSessionAliveCall(" + sessionName + ") failed [" + dMeR.getClass().getName() + "] " + dMeR.getMessage());
			if (LOG.isDebugEnabled())
				LOG.debug("",dMeR);
		}
	}


	/**
	 * Creates new distributed session. Actually Session id will be returned.
	 * If session with such id already exist - then new id will be generated.
	 * Current id is actually name of distributed session.
	 *
	 * @param aSessionId session id.
	 * @return session id will be returned
	 * @throws APISessionDistributionException
	 *          on distribution exception.
	 */
	public static String createSession(String aSessionId) throws APISessionDistributionException {
		if (!isSessionDistributorServiceConfigured()) {
			LOG.warn("Distributed session can't be created! SD - not configured! Relying on defaults!");
			return null;
		}
		try {
			return distributorService.createDistributedSession(aSessionId);
		} catch (SessionsCountLimitReachedSessionDistributorServiceException sclRSDsE) {
			LOG.warn("createSession(" + aSessionId + ") failed [" + sclRSDsE.getClass().getName() + "]-  Returning same Id without remote session creation!" + sclRSDsE.getMessage());
			//Defaults! Continue local work!
			return aSessionId;
		} catch (SessionDistributorServiceException e) {
			throw new APISessionDistributionException(e);
		} catch (DistributemeRuntimeException dMeR) {
			//transport layer runtime!!
			LOG.warn("createSession(" + aSessionId + ") failed [" + dMeR.getClass().getName() + "] " + dMeR.getMessage());
			if (LOG.isDebugEnabled())
				LOG.debug("", dMeR);
			//Defaults! Continue local work!
			return aSessionId;
		}
	}

	/**
	 * Remove distributed session.
	 *
	 * @param aPISessionId id of session to remove
	 */
	public static void removeDistributedSession(String aPISessionId) {
		if (!isSessionDistributorServiceConfigured())
			return;
		try {
			distributorService.deleteDistributedSession(aPISessionId);
			if (LOG.isDebugEnabled())
				LOG.debug("DistributedSession " + aPISessionId + " removed.");
		} catch (NoSuchDistributedSessionException e) {
			LOG.warn("removeDistributedSession( " + aPISessionId + ") failed. Cause" + e.getMessage());
		} catch (SessionDistributorServiceException e) {
			LOG.error("removeDistributedSession( " + aPISessionId + ") failed", e);
		} catch (DistributemeRuntimeException dMeR) {
			//transport layer runtime!!
			LOG.warn("removeDistributedSession(" + aPISessionId + ") failed [" + dMeR.getClass().getName() + "] " + dMeR.getMessage());
			if (LOG.isDebugEnabled())
				LOG.debug("", dMeR);
		}

	}

	/**
	 * Return true if SessionDistributorService is configured and ready for work, false otherwise.
	 *
	 * @return true if SD is configured, false otherwise
	 */
	protected static boolean isSessionDistributorServiceConfigured() {
		if (distributorService == null) {
			LOG.warn("SessionDistributorService is not configured! Working in local mode. Please configure SD - properly.");
			return false;
		}
		return true;
	}

	/**
	 * Private constructor.
	 */
	private APISessionDistributionHelper() {
		throw new IllegalAccessError("Not possible");
	}
}
