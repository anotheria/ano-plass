package net.anotheria.anoplass.api.session;

import net.anotheria.anoprise.sessiondistributor.DistributedSessionAttribute;
import net.anotheria.anoprise.sessiondistributor.DistributedSessionVO;
import net.anotheria.anoprise.sessiondistributor.NoSuchDistributedSessionException;
import net.anotheria.anoprise.sessiondistributor.SessionDistributorService;
import net.anotheria.anoprise.sessiondistributor.SessionDistributorServiceException;
import net.anotheria.net.util.ByteArraySerializer;
import org.apache.log4j.Logger;

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
	 * Log4j instance.
	 */
	private static Logger LOG = Logger.getLogger(APISessionDistributionHelper.class);

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
	 * @param callServiced		   id of caller service
	 * @return {@link APISession}
	 * @throws APISessionDistributionException
	 *          on errors from backend service
	 */
	public static APISession restoreSession(String distributedSessionName, String callServiced) throws APISessionDistributionException {
		isDistributorServiceProperlyConfigured();

		DistributedSessionVO distributedSession;
		try {
			distributedSession = distributorService.restoreDistributedSession(distributedSessionName, callServiced);
		} catch (SessionDistributorServiceException e) {
			LOG.error("restoreDistributedSession " + distributedSessionName + "failed", e);
			throw new APISessionDistributionException(e);
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
				LOG.error("exception occurred attempting to deSerialize this attribute: " + attribute, e);
			}
		}
		return sessionImpl;

	}

	/**
	 * Add attribute to Distributed session.
	 * If incoming attribute policy  - is not DISTRIBUTED or attribute is not serializable - nothing will be done!
	 *
	 * @param sessionName	name of the  distributed session
	 * @param attributeToAdd attributeWrapper
	 */
	public static void addAttributeToDistributedSession(String sessionName, AttributeWrapper attributeToAdd) {
		isDistributorServiceProperlyConfigured();

		if (!PolicyHelper.isDistributed(attributeToAdd.getPolicy()))
			return;
		if (!attributeToAdd.isSerializable()) {
			LOG.warn("Attribute " + attributeToAdd.getKey() + " is marked as distributed but is not serializable, skipped.");
			return;
		}
		try {
			distributorService.addDistributedAttribute(sessionName, new DistributedSessionAttribute(attributeToAdd.getKey(), ByteArraySerializer.serializeObject(attributeToAdd)));
		} catch (IOException e) {
			LOG.error("exception occurred attempting to serialize this attribute: " + attributeToAdd, e);
		} catch (NoSuchDistributedSessionException e) {
			LOG.error("session with id " + sessionName + " not found!", e);
		} catch (SessionDistributorServiceException e) {
			LOG.error("DistributedSession " + sessionName + " addDistributedAttribute.", e);
		}
	}

	/**
	 * Remove  attribute from distributed session.
	 *
	 * @param sessionName   name of the distributed session
	 * @param attributeName name of attribute to be removed
	 */
	public static void removeAttributeFromDistributedSession(String sessionName, String attributeName) {
		isDistributorServiceProperlyConfigured();
		try {
			distributorService.removeDistributedAttribute(sessionName, attributeName);
		} catch (NoSuchDistributedSessionException e) {
			LOG.error("DistributedSession " + sessionName + " not found.", e);
		} catch (SessionDistributorServiceException e) {
			LOG.error("DistributedSession " + sessionName + " removeDistributedAttribute.", e);
		}
	}

	/**
	 * Update distributed session userId.
	 *
	 * @param sessionName name of session
	 * @param userId	  user id
	 */
	public static void updateDistributedSessionUserId(String sessionName, String userId) {
		isDistributorServiceProperlyConfigured();
		try {
			distributorService.updateSessionUserId(sessionName, userId);
		} catch (NoSuchDistributedSessionException e) {
			LOG.error("DistributedSession " + sessionName + " not found.", e);
		} catch (SessionDistributorServiceException e) {
			LOG.error("DistributedSession " + sessionName + " updateSessionUserId.", e);
		}
	}

	/**
	 * Update distributed session editorId.
	 *
	 * @param sessionName name of session (id)
	 * @param editor	  editor id
	 */
	public static void updateDistributedSessionEditorId(String sessionName, String editor) {
		isDistributorServiceProperlyConfigured();
		try {
			distributorService.updateSessionEditorId(sessionName, editor);
		} catch (NoSuchDistributedSessionException e) {
			LOG.error("DistributedSession " + sessionName + " not found.", e);
		} catch (SessionDistributorServiceException e) {
			LOG.error("DistributedSession " + sessionName + " updateSessionEditorId.", e);
		}
	}


	/**
	 * Keep distributed session alive call.
	 *
	 * @param sessionName session id
	 */
	public static void keepSessionAliveCall(String sessionName) {
		isDistributorServiceProperlyConfigured();
		try {
			distributorService.keepDistributedSessionAlive(sessionName);
		} catch (NoSuchDistributedSessionException e) {
			LOG.error("DistributedSession " + sessionName + " not found.", e);
		} catch (SessionDistributorServiceException e) {
			LOG.error("DistributedSession " + sessionName + " updateSessionEditorId.", e);
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
		isDistributorServiceProperlyConfigured();
		try {
			return distributorService.createDistributedSession(aSessionId);
		} catch (SessionDistributorServiceException e) {
			throw new APISessionDistributionException(e);
		}
	}

	/**
	 * Remove distributed session.
	 *
	 * @param aPISessionId id of session to remove
	 */
	public static void removeDistributedSession(String aPISessionId) {
		isDistributorServiceProperlyConfigured();
		try {
			distributorService.deleteDistributedSession(aPISessionId);
			LOG.debug("DistributedSession " + aPISessionId + " removed.");
		} catch (NoSuchDistributedSessionException e) {
			LOG.error("DistributedSession " + aPISessionId + " not found.", e);
		} catch (SessionDistributorServiceException e) {
			LOG.error("DistributedSession " + aPISessionId + " remove failed.", e);
		}

	}

	/**
	 * Throws exception if session distribution is not configured!
	 */
	private static void isDistributorServiceProperlyConfigured() {
		if (distributorService == null)
			throw new IllegalStateException("No SessionDistributorService configured. Please set a SessionDistributorService.");
	}

	/**
	 * Private constructor.
	 */
	private APISessionDistributionHelper() {
		throw new IllegalAccessError("Not possible");
	}
}
