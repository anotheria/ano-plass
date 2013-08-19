package net.anotheria.anoplass.api.session;


import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoprise.eventservice.Event;
import net.anotheria.anoprise.eventservice.EventService;
import net.anotheria.anoprise.eventservice.EventServiceFactory;
import net.anotheria.anoprise.eventservice.EventServicePushConsumer;
import net.anotheria.anoprise.eventservice.util.QueuedEventReceiver;
import net.anotheria.anoprise.processor.ElementWorker;
import net.anotheria.anoprise.processor.QueuedMultiProcessor;
import net.anotheria.anoprise.processor.QueuedMultiProcessorBuilder;
import net.anotheria.anoprise.processor.UnrecoverableQueueOverflowException;
import net.anotheria.anoprise.sessiondistributor.events.SessionDistributorESConstants;
import net.anotheria.anoprise.sessiondistributor.events.SessionDistributorEvent;
import net.anotheria.anoprise.sessiondistributor.events.SessionRestoreEvent;
import net.anotheria.moskito.core.util.storage.Storage;
import net.anotheria.util.IdCodeGenerator;
import net.anotheria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class manages api sessions.
 *
 * @author lrosenberg
 */
public class APISessionManager {
	/**
	 * Default serviceId length.
	 */
	private static final int SERVICE_ID_LENGTH = 10;
	/**
	 * API session processor name constant.
	 */
	private static final String API_SESSION_QUEUED_MULTIPROCESSOR_NAME = "apiSessionManagerQueuedMultiProcessor";

	/**
	 * {@link Logger} for queuedMultiProcessor.
	 */
	private static final Logger MULTI_PROCESSOR_LOGGER = LoggerFactory.getLogger("APISessionManager_MultiProcessor_LOGGER");
	/**
	 * Singleton instance of the manager.
	 */
	private static APISessionManager instance = new APISessionManager();
	/**
	 * Storage for api sessions.
	 */
	private Storage<String, APISession> sessions;
	/**
	 * Storage for reference ids. Reference ids are ids of external connected objects, for example httpSession, and are used to propagate lifeCycle changes
	 * of the external objects to the corresponding session.
	 */
	private Storage<String, String> referenceIds;

	/**
	 * {@link Map} associates Session ID  with last keep alive  distributed session call time.
	 */
	private Map<String, Long> distributedSessionLastCallTime;

	/**
	 * Listeners for session events.
	 */
	private List<APISessionManagerListener> listeners;
	/**
	 * Logger.
	 */
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * APISessionManager 'distributionConfig'.
	 */
	private APISessionDistributionConfig distributionConfig;

	/**
	 * APISessionManager 'callback'.
	 */
	private APISessionImpl.APISessionCallBack callback;

	/**
	 * QueuedMultiProcessor  for APISessionEvent.
	 * Will be initialized only if Distribution is on!.
	 */
	private QueuedMultiProcessor<APISessionEvent> processor;

	/**
	 * Unique id of current service.
	 */
	private String serviceId;

	/**
	 * Constructor.
	 */
	private APISessionManager() {
		sessions = Storage.createConcurrentHashMapStorage("sessions");
		referenceIds = Storage.createConcurrentHashMapStorage("session-refIds");
		listeners = new CopyOnWriteArrayList<APISessionManagerListener>();
		distributionConfig = APISessionDistributionConfig.getInstance();
		callback = new APISessionCallBackImpl();
		serviceId = "APISessionManager_" + IdCodeGenerator.generateCode(SERVICE_ID_LENGTH);
		configureIntegration();

	}

	/**
	 * Init integration.
	 */
	private void configureIntegration() {
		// init for last call time map
		distributedSessionLastCallTime = new ConcurrentHashMap<String, Long>();

		//Don't configure if not required :)
		if (!distributionConfig.isDistributionEnabled())
			return;
		if (!APISessionDistributionHelper.isSessionDistributorServiceConfigured())
			return;


		log.info("APISessionManager SessionDistributorConsumer INITIALIZATION: STARTED");
		try {
			//Initing EventServiceBridge!  Start
			org.distributeme.support.eventservice.generated.EventServiceRMIBridgeServer.init();
			org.distributeme.support.eventservice.generated.EventServiceRMIBridgeServer.createServiceAndRegisterLocally();
			//Initing EventServiceBridge!  END

			EventService eventService = EventServiceFactory.createEventService();
			SessionDistributorConsumer usConsumer = new SessionDistributorConsumer();
			//Receive events from session distributor.
			QueuedEventReceiver sessionDistributorEventReceiver = new QueuedEventReceiver("SessionDistributorEventReceiver",
					SessionDistributorESConstants.CHANNEL_NAME, usConsumer,
					distributionConfig.getSessionDistributorEventReceiverQueueSize(),
					distributionConfig.getSessionDistributorEventReceiverQueueSleepTime(), log);
			//consumer registration
			eventService.obtainEventChannel(SessionDistributorESConstants.CHANNEL_NAME, sessionDistributorEventReceiver).addConsumer(sessionDistributorEventReceiver);
			sessionDistributorEventReceiver.start();
			log.info("APISessionManager SessionDistributorConsumer INITIALIZATION: FINISHED. Waiting for events...");
		} catch (Exception e) {
			log.error("SessionDistributorEventReceiver init failed.", e);
		}
		//configure Event sender!!!

		processor = new QueuedMultiProcessorBuilder<APISessionEvent>().
				setSleepTime(distributionConfig.getApiSessionEventSenderQueueSleepTime()).
				setQueueSize(distributionConfig.getApiSessionEventSenderQueueSize()).
				setProcessorChannels(distributionConfig.getApiSessionEventSenderQueueProcessingChannelsAmount()).
				setProcessingLog(MULTI_PROCESSOR_LOGGER).
				attachMoskitoLoggers("APISessionManager : APISession-Distribution events processor", "storage", "default").
				build(API_SESSION_QUEUED_MULTIPROCESSOR_NAME, new SessionWorker());
		processor.start();
	}

	/**
	 * Returns the single instance of this class.
	 *
	 * @return instance
	 */
	public static APISessionManager getInstance() {
		return instance;
	}


	/**
	 * Obtain session.
	 * There are few options available.
	 * First if distribution is enabled and "dSessionIdRequestParam" is available - system will try to restore distributed session, if session will be successfully restored - that's it.
	 * <p/>
	 * In case if "apiSessionId" is not available, distribution is enabled and "dSessionIdFromCookies" present - system  will try to restore distributed session, in case of failure
	 * new session will be created.
	 * <p/>
	 * The last  chance to obtain session  is next :
	 * <p/>
	 * If "apiSessionId"  -  is present - system will try to find session with such in local storage, in case of failure  new session will be created...
	 * <p/>
	 * <p/>
	 * Base parameters :
	 * <b>httpSessionId</b>  reference id , actually {@link javax.servlet.http.HttpSession#getId()}.
	 * <b>apiSessionId</b>   actually api session id - which is  linked to current HttpSession.
	 * <p/>
	 * Session-Restore options/parameters:
	 * <p>Each of them can be NULL!</p>
	 * <b>dSessionIdFromCookies</b>  - has  the highest priority .. In case when session distribution is enabled and  properly configured + session with such id  still was not restored,
	 * system  will try to restore  Distributed api session  using this  property...</p>
	 * <b>dSessionIdFromCookies</b> - will be used if no API session still attached to HttpSession, and distribution is  enabled.<p/>
	 * <p/>
	 * Additional parameters :
	 * <p/>
	 * <b>remoteAddress</b> - additional parameter {@link javax.servlet.http.HttpServletRequest#getRemoteAddr()} which will be populated to just obtained {@link APISession}.
	 * <b>userAgent</b> - user agent header {@link javax.servlet.http.HttpServletRequest#getHeader(String)} which will be populated to just obtained {@link APISession}.
	 * <b>locale</b> - locale from request {@link javax.servlet.http.HttpServletRequest#getLocale()} which will be populated to  {@link APICallContext} if required.
	 * <b>editorId</b> - cms editor id  from session attributes {@link javax.servlet.http.HttpSession#getAttribute(String)} which will be populated to  {@link APICallContext} if required.
	 * <p/>
	 * <p/>
	 *
	 * @param httpSessionId          reference id itself
	 * @param apiSessionId           id of  api session  which should be obtained .. NULL is possible
	 * @param dSessionIdFromCookies  distributed session ID  value -  which comes from cookies (Null is possible if distribution is  disabled, or cookies not present)
	 * @param dSessionIdRequestParam distributed session ID - request  parameter value... ( Null is possible )
	 * @param remoteAddress          - request  header "user-agent"
	 * @param userAgent              - request  remote ip property
	 * @param locale                 request Locale property
	 * @param editorId               cms editor id.. as HttpSession attribute
	 * @return obtained {@link APISession}
	 * @throws APISessionCreationException on Session obtain errors
	 */
	public APISession obtainSession(final String httpSessionId, final String apiSessionId, final String dSessionIdFromCookies, String dSessionIdRequestParam,
									final String remoteAddress, final String userAgent, final Locale locale, final String editorId) throws APISessionCreationException {

		if (StringUtils.isEmpty(httpSessionId))
			throw new IllegalArgumentException("Invalid httpSessionId passed");

		//reset current call context.
		APICallContext.getCallContext().reset();

		APISession session = null;
		// first checking if distribution is enabled and distributedSessionIdReqParam - present... and  surely checking that  such session was not restored!
		if (isDistributionEnabledAndProperlyConfigured() && !StringUtils.isEmpty(dSessionIdRequestParam) &&
				/* Only if still not restored! */
				(!dSessionIdRequestParam.equals(apiSessionId) || apiSessionId == null))
			try {
				session = restoreSession(dSessionIdRequestParam, httpSessionId);
				if (session != null)
					return populateAdditionalParameters(remoteAddress, userAgent, editorId, locale, session);
			} catch (APISessionRestoreException e) {
				log.warn("restoreSession(" + dSessionIdRequestParam + ", " + httpSessionId + ") failed. " + e.getMessage());
			}

		// in case when no associated session  found -- trying to restore - using "distributedSessionIdCookieValue"
		if (StringUtils.isEmpty(apiSessionId)) {
			if (isDistributionEnabledAndProperlyConfigured() && !StringUtils.isEmpty(dSessionIdFromCookies))
				try {
					session = restoreSession(dSessionIdFromCookies, httpSessionId);
				} catch (APISessionRestoreException e) {
					log.warn("restoreSession(" + dSessionIdRequestParam + ", " + httpSessionId + ") failed. " + e.getMessage());
				}
			//if was not restored - creating new ONE!
			session = session == null ? createSession(httpSessionId) : session;
			return populateAdditionalParameters(remoteAddress, userAgent, editorId, locale, session);
		}

		// trying to obtain session using "apiSessionIdHttpSessionAttributeValue".
		session = getSession(apiSessionId);
		// checking if it's exists
		if (session == null)
			session = createSession(httpSessionId);

		return populateAdditionalParameters(remoteAddress, userAgent, editorId, locale, session);

	}

	/**
	 * Usage only for tests, and different test context initialization stuff!
	 *
	 * @param referenceId reference id
	 * @return {@link APISession}
	 * @throws APISessionCreationException on errors
	 */
	@Deprecated
	public APISession obtainSession(String referenceId) throws APISessionCreationException {
		return obtainSession(referenceId, "", "", "", "", "", null, "");
	}


	/**
	 * Populate additional properties to {@link APISession} and current {@link APICallContext}.
	 *
	 * @param remoteAddress     - remote Ip
	 * @param userAgent         "user-agent" header value
	 * @param editorId          cms editor id if  such present
	 * @param locale            {@link Locale}
	 * @param currentAPISession {@link APISession} which should be populated with properties
	 * @return {@link APISession}
	 */
	private APISession populateAdditionalParameters(final String remoteAddress, final String userAgent, final String editorId, final Locale locale, final APISession currentAPISession) {
		APICallContext currentContext = APICallContext.getCallContext();

		currentAPISession.setIpAddress(remoteAddress);
		currentAPISession.setUserAgent(userAgent);

		currentContext.setCurrentSession(currentAPISession);

		Locale localeToSet = currentAPISession.getLocale() == null ? locale : currentAPISession.getLocale();
		currentContext.setCurrentLocale(localeToSet);

		if (!StringUtils.isEmpty(editorId))
			currentContext.setCurrentEditorId(editorId);

		currentContext.setCurrentUserId(currentAPISession.getCurrentUserId());
		return currentAPISession;
	}


	/**
	 * Creates a new session with a reference id. The reference id is the id of the connected object (for example httpSession) and is used to retrieve the
	 * api session later in the owning
	 * object lifeCycle update. For example if the httpSession expires the session listener notifies the APISessionManager and it can expire corresponding
	 * APISession.
	 *
	 * @param referenceId Http session ID
	 * @return create APISession
	 * @throws APISessionCreationException on distributed apiSession create errors
	 */
	protected APISession createSession(String referenceId) throws APISessionCreationException {

		APISession session = new APISessionImpl(createAPISessionId());
		APISessionImpl.class.cast(session).setReferenceId(referenceId);

		sessions.put(session.getId(), session);
		referenceIds.put(referenceId, session.getId());
		if (log.isDebugEnabled())
			log.debug("createSession, id=" + session.getId());

		//Populating call back only if distribution is enabled - and properly configured!
		if (isDistributionEnabledAndProperlyConfigured()) {
			//populate distribution call time if required
			distributedSessionLastCallTime.put(session.getId(), System.currentTimeMillis());
			APISessionImpl.class.cast(session).setSessionCallBack(callback);
		}

		return session;
	}

	/**
	 * Restore session.
	 *
	 * @param sessionId   id of session to restore
	 * @param referenceId jSessionId  (after success restore - event should be generated! and all hosts without this session id - should loose curr session)
	 * @return {@link APISession}
	 * @throws APISessionRestoreException on restore and configuration failures
	 */
	protected APISession restoreSession(String sessionId, String referenceId) throws APISessionRestoreException {
		if (!distributionConfig.isDistributionEnabled())
			throw new APISessionRestoreException(sessionId, "SessionDistribution is turned OFF!");
		//Additional check! to prevent possible errors!!
		if (!APISessionDistributionHelper.isSessionDistributorServiceConfigured())
			throw new APISessionRestoreException(sessionId, "SessionDistribution is not properly configured!");

		try {
			APISession restored = APISessionDistributionHelper.restoreSession(sessionId, serviceId);

			//checking - that Restored is Really APISessionImpl - and is not NULL (in case if Distributor service is not configured properly)
			if (!(restored instanceof APISessionImpl))
				throw new APISessionRestoreException(sessionId, "Unknown implementation was restored! Skipping.");


			APISessionImpl session = APISessionImpl.class.cast(restored);
			session.setSessionCallBack(callback);
			session.setReferenceId(referenceId);
			sessions.put(session.getId(), session);
			referenceIds.put(referenceId, session.getId());
			distributedSessionLastCallTime.put(session.getId(), System.currentTimeMillis());
			if (log.isDebugEnabled())
				log.debug("session restored, id=" + session.getId());
			return restored;


		} catch (APISessionDistributionException e) {
			log.error("session restore failed session[" + sessionId + "]", e);
			throw new APISessionRestoreException(sessionId, e);
		}

	}


	/**
	 * Generates session ID for APISession.
	 * First generating the id. Afterwards if distribution is enabled - trying to create
	 * distributed session - with generated id. If session with such id already exists sessionId will be regenerated by
	 * Back-end service, using same mechanism.
	 *
	 * @return id.
	 * @throws APISessionCreationException on distributed session creation errors.
	 */
	private String createAPISessionId() throws APISessionCreationException {
		String sessionId = IdCodeGenerator.generateCode(30);
		return createAndGetAssociatedDistributedSessionId(sessionId);
	}

	/**
	 * If session distribution is enabled (and properly configured)  - then before APISession creation, Distributed session will be created.
	 * Current method creates such session and returns it's id.
	 *
	 * @param sessionId proposed distributed session id
	 * @return id of distributed session
	 * @throws APISessionCreationException on distributed session creation errors
	 */
	private String createAndGetAssociatedDistributedSessionId(String sessionId) throws APISessionCreationException {
		if (isDistributionEnabledAndProperlyConfigured())
			try {
				sessionId = APISessionDistributionHelper.createSession(sessionId);
			} catch (APISessionDistributionException e) {
				log.error("Distributed session " + sessionId + " creation failure", e);
				throw new APISessionCreationException(sessionId, e);
			}
		return sessionId;
	}

	/**
	 * Returns a list with all reference ids.
	 *
	 * @return {@link ArrayList<String> }
	 */
	public ArrayList<String> getReferenceIds() {
		ArrayList<String> ret = new ArrayList<String>(referenceIds.size());
		for (String id : referenceIds.keySet())
			ret.add(id);
		return ret;
	}

	/**
	 * Returns a list with all session ids.
	 *
	 * @return {@link ArrayList<String> }
	 */
	public ArrayList<String> getSessionIds() {
		ArrayList<String> ret = new ArrayList<String>(sessions.size());
		for (String id : sessions.keySet())
			ret.add(id);
		return ret;
	}

	/**
	 * Returns the session with the given id.
	 *
	 * @param id session id
	 * @return {@link APISession}
	 */
	public APISession getSession(String id) {
		APISession session = sessions.get(id);

		//Checking last distributed session call time
		//send keep alive call to distribution service if required
		if (isDistributionEnabledAndProperlyConfigured() && session != null) {
			Long lastCall = distributedSessionLastCallTime.get(id);
			lastCall = lastCall == null ? System.currentTimeMillis() : lastCall;
			if ((System.currentTimeMillis() - lastCall) >= distributionConfig.getDistributedSessionKeepAliveCallInterval()) {
				callback.keepAliveCall(id);
				distributedSessionLastCallTime.put(session.getId(), System.currentTimeMillis());
			}

		}
		return session;
	}

	/**
	 * Returns the number of known session.
	 *
	 * @return int value
	 */
	public int getSessionCount() {
		return sessions.size();
	}

	/**
	 * Returns a session by the reference id of its connected object.
	 *
	 * @param aReferenceId associated httpSession id
	 * @return {@link APISession}
	 */
	public APISession getSessionByReferenceId(String aReferenceId) {
		String sessionId;
		sessionId = referenceIds.get(aReferenceId);
		if (sessionId == null)
			throw new RuntimeException("Can't find session for referenceId: " + aReferenceId);
		return getSession(sessionId);
	}

	/**
	 * Destroys a session via its reference id. This is used by APISessionListener to propagate session timeout event from http session to corresponding api session.
	 *
	 * @param referenceId jSessionId itself :)
	 */
	public void destroyAPISessionByReferenceId(String referenceId) {
		String sessionId = referenceIds.get(referenceId);
		if (sessionId == null)
			return;
		APISession session = sessions.remove(sessionId);
		if (session == null) {
			log.info("HttpSession expired " + referenceId + ", no api session connected");
			return;
		}

		for (APISessionManagerListener listener : listeners)
			listener.apiSessionDestroyed(session);
		destroyDistributedSession(sessionId);

		if (log.isDebugEnabled())
			log.debug("HttpSession expired: " + referenceId + ", ApiSessionId: " + session.getId());

		((APISessionImpl) session).clear();
		//remove distributed session call time  if enabled
		if (distributionConfig.isDistributionEnabled())
			distributedSessionLastCallTime.remove(session.getId());

		referenceIds.remove(referenceId);

		// send Destroy session event
	}

	/**
	 * Destroys an api session.
	 *
	 * @param sessionId id of session
	 */
	public void destroyAPISessionBySessionId(String sessionId) {

		if (StringUtils.isEmpty(sessionId))
			return;

		APISession session = sessions.remove(sessionId);

		if (session == null)
			return;
		for (APISessionManagerListener listener : listeners)
			listener.apiSessionDestroyed(session);
		destroyDistributedSession(sessionId);

		APISessionImpl.class.cast(session).clear();
		//remove distributed session call time  if enabled
		if (distributionConfig.isDistributionEnabled() && distributedSessionLastCallTime != null)
			distributedSessionLastCallTime.remove(session.getId());

		referenceIds.remove(APISessionImpl.class.cast(session).getReferenceId());
		//// send Destroy session event

	}

	/**
	 * Remove distributed session by sessionId, only if sessionDistribution is enabled adn configured.
	 *
	 * @param sessionId id of distributed session
	 */
	private void destroyDistributedSession(String sessionId) {
		if (isDistributionEnabledAndProperlyConfigured())
			APISessionDistributionHelper.removeDistributedSession(sessionId);
	}

	/**
	 * Adds an api session listener.
	 *
	 * @param listener {@link APISessionManagerListener}
	 */
	public void addAPISessionManagerListener(APISessionManagerListener listener) {
		listeners.add(listener);
	}


	/**
	 * Return true - if distribution is enabled and  properly configured.
	 * False otherwise.
	 *
	 * @return boolean value
	 */
	private boolean isDistributionEnabledAndProperlyConfigured() {
		return distributionConfig.isDistributionEnabled() && APISessionDistributionHelper.isSessionDistributorServiceConfigured();
	}

	/**
	 * Remove session after restore on other host.
	 *
	 * @param aSessionId     session id
	 * @param aCallServiceId id of service which called restore
	 */
	private void removeRestoredSession(String aSessionId, String aCallServiceId) {
		if (serviceId.equals(aCallServiceId)) {
			if (log.isDebugEnabled())
				log.debug("Do nothing ! we are at host which just restored session[" + aSessionId + "]");
			return;
		}
		APISession session = sessions.remove(aSessionId);
		if (session == null)
			return;
		for (APISessionManagerListener listener : listeners)
			listener.apiSessionDestroyed(session);

		APISessionImpl.class.cast(session).clear();
		distributedSessionLastCallTime.remove(session.getId());
		referenceIds.remove(APISessionImpl.class.cast(session).getReferenceId());
	}


	/**
	 * APISessionImpl.APISessionCallBack default implementation.
	 * Used only for Sessions sync.
	 */
	private class APISessionCallBackImpl implements APISessionImpl.APISessionCallBack {

		@Override
		public void attributeSet(String sessionId, AttributeWrapper aWrapper) {
			if (!distributionConfig.isDistributionEnabled())
				return;
			if (!APISessionDistributionHelper.isSessionDistributorServiceConfigured())
				return;


			if (processor == null) {
				log.error("Session distribution is enabled but processor is not configured! ");
				return;
			}

			if (!aWrapper.isSerializable()) {
				log.error("Attribute " + aWrapper + " is not serializable! Ignoring.");
				return;
			}
			//create add event
			APISessionEvent event = new APISessionEvent(sessionId, CallBackOperationType.ATTRIBUTE_ADD);
			event.setWrapper(aWrapper);

			try {
				processor.addToQueueDontWait(event);
				// adjusting distributed session call time
				distributedSessionLastCallTime.put(sessionId, System.currentTimeMillis());
			} catch (UnrecoverableQueueOverflowException e) {
				log.error("Queue is Full. Session " + sessionId + " add attribute " + aWrapper.getKey() + " distribution skipped.", e);
			}

		}

		@Override
		public void attributeRemoved(String sessionId, String attributeName) {
			if (!distributionConfig.isDistributionEnabled())
				return;
			if (!APISessionDistributionHelper.isSessionDistributorServiceConfigured())
				return;

			if (processor == null) {
				log.error("Session distribution is enabled but processor is not configured! ");
				return;
			}
			//create remove event
			APISessionEvent event = new APISessionEvent(sessionId, CallBackOperationType.ATTRIBUTE_REMOVE);
			event.setAttributeName(attributeName);

			try {
				processor.addToQueueDontWait(event);
				// adjusting distributed session call time
				distributedSessionLastCallTime.put(sessionId, System.currentTimeMillis());
			} catch (UnrecoverableQueueOverflowException e) {
				log.error("Queue is Full. Session " + sessionId + " remove attribute " + attributeName + " distribution skipped.", e);
			}
		}

		@Override
		public void currentUserIdChanged(String sessionId, String userId) {
			if (!distributionConfig.isDistributionEnabled())
				return;
			if (!APISessionDistributionHelper.isSessionDistributorServiceConfigured())
				return;

			if (processor == null) {
				log.error("Session distribution is enabled but processor is not configured! ");
				return;
			}
			//create userID set event
			APISessionEvent event = new APISessionEvent(sessionId, CallBackOperationType.USER_ID_SET);
			event.setUserId(userId);

			try {
				processor.addToQueueDontWait(event);
				// adjusting distributed session call time
				distributedSessionLastCallTime.put(sessionId, System.currentTimeMillis());
			} catch (UnrecoverableQueueOverflowException e) {
				log.error("Queue is Full. Session " + sessionId + " update userId " + userId + " distribution skipped.", e);
			}
		}

		@Override
		public void editorIdChanged(String sessionId, String editorId) {
			if (!distributionConfig.isDistributionEnabled())
				return;
			if (!APISessionDistributionHelper.isSessionDistributorServiceConfigured())
				return;

			if (processor == null) {
				log.error("Session distribution is enabled but processor is not configured! ");
				return;
			}
			//create editor id set event
			APISessionEvent event = new APISessionEvent(sessionId, CallBackOperationType.EDITOR_ID_SET);
			event.setEditorId(editorId);

			try {
				processor.addToQueueDontWait(event);
				// adjusting distributed session call time
				distributedSessionLastCallTime.put(sessionId, System.currentTimeMillis());
			} catch (UnrecoverableQueueOverflowException e) {
				log.error("Queue is Full. Session " + sessionId + " update editorId " + editorId + " distribution skipped.", e);
			}
		}

		@Override
		public void keepAliveCall(String sessionId) {
			if (!distributionConfig.isDistributionEnabled())
				return;
			if (!APISessionDistributionHelper.isSessionDistributorServiceConfigured())
				return;

			if (processor == null) {
				log.error("Session distribution is enabled but processor is not configured! ");
				return;
			}

			APISessionEvent event = new APISessionEvent(sessionId, CallBackOperationType.KEEP_ALIVE_CALL);
			try {
				processor.addToQueueDontWait(event);
				// adjusting distributed session call time
				distributedSessionLastCallTime.put(sessionId, System.currentTimeMillis());
			} catch (UnrecoverableQueueOverflowException e) {
				log.error("Queue is Full. Session " + sessionId + " keep alive call skipped.", e);
			}
		}
	}

	/**
	 * Consumer for SessionDistributor events.
	 */
	protected class SessionDistributorConsumer implements EventServicePushConsumer {
		/**
		 * Logger.
		 */
		private final Logger log = LoggerFactory.getLogger(SessionDistributorConsumer.class);

		@Override
		public void push(Event event) {
			if (log.isDebugEnabled())
				log.debug("SessionDistributor  service event: " + event);
			if (event == null || event.getData() == null || !(event.getData() instanceof SessionDistributorEvent))
				return;

			SessionDistributorEvent someEvent = (SessionDistributorEvent) event.getData();
			switch (someEvent.getOperation()) {
				case SESSION_RESTORE:
					SessionRestoreEvent restore = (SessionRestoreEvent) someEvent;
					removeRestoredSession(restore.getSessionId(), restore.getServiceId());
					if (log.isDebugEnabled())
						log.debug("Incoming RESTORE EVENT!" + restore);
					break;
				case SESSION_DELETE:
					if (log.isDebugEnabled())
						log.debug("Current method SESSION_DELETE not used currently" + event);
					break;
				case SESSION_CLEAN_UP:
					if (log.isDebugEnabled())
						log.debug("Current method SESSION_CLEAN_UP not used currently" + event);
					break;
			}

		}


	}

	/**
	 * Operations type.
	 */
	private static enum CallBackOperationType {
		/**
		 * Attribute add operation.
		 */
		ATTRIBUTE_ADD,
		/**
		 * Attribute remove operation.
		 */
		ATTRIBUTE_REMOVE,
		/**
		 * User Id set operation.
		 */
		USER_ID_SET,
		/**
		 * EditorId set operation.
		 */
		EDITOR_ID_SET,
		/**
		 * Keep alive call.
		 */
		KEEP_ALIVE_CALL

	}

	/**
	 * APISession event.
	 */
	private static class APISessionEvent implements Serializable {
		/**
		 * Basic "default" serial version uid.
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * APISessionEvent operation type.
		 */
		private CallBackOperationType type;
		/**
		 * APISessionEvent sessionId.
		 */
		private String apiSessionId;
		/**
		 * APISessionEvent userId.
		 */
		private String userId;
		/**
		 * APISessionEvent editorId.
		 */
		private String editorId;
		/**
		 * APISessionEvent attributeName.
		 */
		private String attributeName;
		/**
		 * APISessionEvent wrapper.
		 */
		private AttributeWrapper wrapper;


		/**
		 * Constructor.
		 *
		 * @param aSessionId session id
		 * @param aType      operation type
		 */
		public APISessionEvent(String aSessionId, CallBackOperationType aType) {
			this.apiSessionId = aSessionId;
			this.type = aType;
		}

		public String getApiSessionId() {
			return apiSessionId;
		}

		public String getAttributeName() {
			return attributeName;
		}

		public void setAttributeName(String attributeName) {
			this.attributeName = attributeName;
		}

		public String getEditorId() {
			return editorId;
		}

		public void setEditorId(String editorId) {
			this.editorId = editorId;
		}

		public CallBackOperationType getType() {
			return type;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public AttributeWrapper getWrapper() {
			return wrapper;
		}

		public void setWrapper(AttributeWrapper wrapper) {
			this.wrapper = wrapper;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("APISessionEvent");
			sb.append("{apiSessionId='").append(apiSessionId).append('\'');
			sb.append(", type=").append(type);
			sb.append(", userId='").append(userId).append('\'');
			sb.append(", editorId='").append(editorId).append('\'');
			sb.append(", attributeName='").append(attributeName).append('\'');
			sb.append(", wrapper=").append(wrapper);
			sb.append('}');
			return sb.toString();
		}
	}


	/**
	 * API session even worker.
	 */
	private class SessionWorker implements ElementWorker<APISessionEvent> {

		@Override
		public void doWork(APISessionEvent workingElement) throws Exception {
			if (log.isDebugEnabled())
				log.debug("Working on element" + workingElement);
			switch (workingElement.getType()) {
				case ATTRIBUTE_ADD:
					APISessionDistributionHelper.addAttributeToDistributedSession(workingElement.getApiSessionId(), workingElement.getWrapper());
					if (log.isDebugEnabled())
						log.debug("ATTRIBUTE_ADD" + workingElement);
					return;
				case ATTRIBUTE_REMOVE:
					APISessionDistributionHelper.removeAttributeFromDistributedSession(workingElement.getApiSessionId(), workingElement.getAttributeName());
					if (log.isDebugEnabled())
						log.debug("ATTRIBUTE_REMOVE" + workingElement);
					return;
				case USER_ID_SET:
					APISessionDistributionHelper.updateDistributedSessionUserId(workingElement.getApiSessionId(), workingElement.getUserId());
					if (log.isDebugEnabled())
						log.debug("USER_ID_SET" + workingElement);
					return;
				case EDITOR_ID_SET:
					APISessionDistributionHelper.updateDistributedSessionEditorId(workingElement.getApiSessionId(), workingElement.getEditorId());
					if (log.isDebugEnabled())
						log.debug("EDITOR_ID_SET" + workingElement);
					return;
				case KEEP_ALIVE_CALL:
					//simplest call which will update session timeout time in SessionDistributorService
					APISessionDistributionHelper.keepSessionAliveCall(workingElement.getApiSessionId());
					if (log.isDebugEnabled())
						log.debug("KEEP_ALIVE_CALL" + workingElement);
					return;
			}
			if (log.isDebugEnabled())
				log.debug("element process completed");

		}
	}
}


