package net.anotheria.anoplass.api.session;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The default implementation of the APISession.
 *
 * @author lrosenberg
 */
public class APISessionImpl implements APISession, Serializable {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The id of the session.
	 */
	private String id;
	/**
	 * Internal attribute map.
	 */
	private Map<String, AttributeWrapper> attributes;
	/**
	 * Ip address of the user.
	 */
	private String ipAddress;
	/**
	 * User-agent of the users browser.
	 */
	private String userAgent;
	/**
	 * Reference id (the http session id).
	 */
	private String referenceId;
	/**
	 * Current userId.
	 */
	private String currentUserId;
	/**
	 * Current editorId.
	 */
	private String currentEditorId;
	/**
	 * Locale itself.
	 */
	private Locale locale;
	/**
	 * APISessionCallBack instance.
	 * NULL - when distribution is disabled!!!
	 */
	private transient APISessionCallBack sessionCallBack;
	/**
	 * A scope which only exists between two executions (like flush).
	 */
	private Map<String, Object> actionScope;
	/**
	 * Log4j logger.
	 */
	private static Logger log;


	/**
	 * Static init block.
	 */
	static {
		log = LoggerFactory.getLogger(APISessionImpl.class);
	}

	/**
	 * Creates a new APISession with the given id.
	 *
	 * @param anId session Id
	 */
	APISessionImpl(String anId) {
		id = anId;
		attributes = new ConcurrentHashMap<String, AttributeWrapper>();
		actionScope = new ConcurrentHashMap<String, Object>();
	}


	@Override
	public Object getAttribute(String key) {
		AttributeWrapper wrapper = attributes.get(key);
		if (wrapper!=null && wrapper.isFlashing()){
			removeAttribute(key);
		}
		return wrapper == null ? null : wrapper.getValue();
	}

    public AttributeWrapper getAttributeWrapper(String key){
        return attributes.get(key);
    }

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setAttribute(String key, int policy, Object value) {
		AttributeWrapper wrapper = new AttributeWrapper(key, value, policy);
		attributes.put(key, wrapper);
		//calling SetAttribute
		if (canDistribute(wrapper))
			sessionCallBack.attributeSet(getId(), wrapper);

	}


	@Override
	public void setAttribute(String key, int policy, Object value, long expiresWhen) {
		AttributeWrapper wrapper = new AttributeWrapper(key, value, policy, expiresWhen);
		attributes.put(key, wrapper);
		//calling SetAttribute
		if (canDistribute(wrapper))
			sessionCallBack.attributeSet(getId(), wrapper);

	}

	@Override
	public void setAttribute(String key, Object value) {
		setAttribute(key, APISession.POLICY_DEFAULT, value);
	}

	@Override
	public void removeAttribute(String key) {
		AttributeWrapper wrapper = attributes.get(key);
		attributes.remove(key);
		//calling remove Attribute
		if (canDistribute(wrapper))
			sessionCallBack.attributeRemoved(getId(), key);
	}

	@Override
	public List<String> getAttributeNames() {
		return new ArrayList<String>(attributes.keySet());
	}

	@Override
	public String toString() {
		return "Id: " + id + ", attributes: " + attributes.size();
	}

	@Override
	public String getIpAddress() {
		return ipAddress;
	}

	@Override
	public void setIpAddress(String anIpAddress) {
		String oldIpAddress = ipAddress;
		ipAddress = anIpAddress;
		if (oldIpAddress != null && !(oldIpAddress.equals(ipAddress)))
			log.warn(this + " session switches ip from " + oldIpAddress + ", to: " + ipAddress);
	}

	@Override
	public String getUserAgent() {
		return userAgent;
	}

	@Override
	public void setUserAgent(String anUserAgent) {
		userAgent = anUserAgent;
	}

	/**
	 * Used internally to clear all attributes.
	 * Actually after remove session from SessionManager holders.
	 */
	void clear() {
		attributes.clear();
		//there is no any  need to clean up distributed attributes sofar! as current method used only with delete!
	}

	/**
	 * Called on logout. Removes all attributes except those with the POLICY_SURVIVE_LOGOUT.
	 */
	@Override
	public void cleanupOnLogout() {
		for (AttributeWrapper w : getAttributes()) {
			if (!PolicyHelper.isPolicySet(w.getPolicy(), POLICY_SURVIVE_LOGOUT)) {
				attributes.remove(w.getKey());
				//remove attributes
				if (canDistribute(w))
					sessionCallBack.attributeRemoved(getId(), w.getKey());
			}
		}
	}

	/**
	 * Returns all attributes.
	 *
	 * @return {@link Collection<AttributeWrapper>}
	 */
	Collection<AttributeWrapper> getAttributes() {
		return attributes.values();
	}

	/**
	 * Sets an {@link AttributeWrapper}.
	 *
	 * @param w {@link AttributeWrapper}
	 */
	void setAttributeWrapper(AttributeWrapper w) {
		attributes.put(w.getKey(), w);
	}

	/**
	 * Dump session to stream for debug purposes.
	 *
	 * @param out {@link PrintStream}
	 */
	public void dumpSession(PrintStream out) {
		out.println("API Session with id: " + getId() + ", Attributes: " + attributes);
		for (AttributeWrapper a : attributes.values())
			System.out.println(a.getKey() + " = " + a.getValue());
	}

	/**
	 * Dump session to log for debug purposes.
	 *
	 * @param log {@link Logger}
	 */
	public void dumpSession(Logger log) {
		log.debug("API Session with id: " + getId() + ", Attributes: " + attributes);
	}


	public String getReferenceId() {
		return referenceId;
	}

	/**
	 * Set reference id.
	 *
	 * @param aReferenceId reference id  to set.
	 */
	public void setReferenceId(String aReferenceId) {
		this.referenceId = aReferenceId;
	}


	@Override
	public String getCurrentUserId() {
		return currentUserId;
	}

	/**
	 * Set current user id.
	 *
	 * @param aCurrentUserId userId
	 */
	public void setCurrentUserId(String aCurrentUserId) {
		boolean isDifferent = isChanged(currentUserId, aCurrentUserId);
		this.currentUserId = aCurrentUserId;
		//current userId changed
		if (sessionCallBack != null && isDifferent)
			sessionCallBack.currentUserIdChanged(getId(), currentUserId);

	}

	@Override
	public String getCurrentEditorId() {
		return currentEditorId;
	}

	/**
	 * Set current editor id.
	 *
	 * @param aCurrentEditorId editor id
	 */
	public void setCurrentEditorId(String aCurrentEditorId) {
		boolean isDifferent = isChanged(currentEditorId, aCurrentEditorId);
		this.currentEditorId = aCurrentEditorId;
		//current editorId changed
		if (sessionCallBack != null && isDifferent)
			sessionCallBack.editorIdChanged(getId(), currentEditorId);
	}

	/**
	 * Add attribute to action scope.
	 *
	 * @param name	  attribute name
	 * @param attribute attribute itself
	 */
	public void addAttributeToActionScope(String name, Object attribute) {
		actionScope.put(name, attribute);
	}

	/**
	 * Return action scope map.
	 *
	 * @return {@link Map<String,Object>}
	 */
	public Map<String, Object> getActionScope() {
		return actionScope;
	}

	/**
	 * Clear action scope.
	 */
	public void resetActionScope() {
		actionScope.clear();
	}

	public Locale getLocale() {
		return locale;
	}

	/**
	 * Set locale.
	 *
	 * @param aLocale locale to set
	 */
	public void setLocale(Locale aLocale) {
		this.locale = aLocale;
	}



	protected void setSessionCallBack(APISessionCallBack sessionCallBack) {
		this.sessionCallBack = sessionCallBack;
	}

	/* Return true if values differs.
	 *
	 * @param value1 <T> val
	 * @param value2 <T> val
	 * @return boolean result
	 */
	private <T> boolean isChanged(T value1, T value2) {
		return value1 != null && !value1.equals(value2) || value2 != null && !value2.equals(value1);
	}

	/**
	 * Return true if attribute can be distributed!
	 *
	 * @param wrapper {@link AttributeWrapper}
	 * @return boolean value
	 */
	private boolean canDistribute(AttributeWrapper wrapper) {
		return sessionCallBack != null && wrapper != null && PolicyHelper.isDistributed(wrapper.getPolicy());
	}

	/**
	 * APISession call back event interface. Used for
	 * sending events on {@link APISession} changes.
	 * Used only when distribution is enabled!
	 */
	protected static interface APISessionCallBack {
		/**
		 * Attribute added to session.
		 *
		 * @param sessionId APISession id
		 * @param aWrapper  attribute itself
		 */
		void attributeSet(String sessionId, AttributeWrapper aWrapper);

		/**
		 * Attribute removed from APISession.
		 *
		 * @param sessionId	 id
		 * @param attributeName attribute name
		 */
		void attributeRemoved(String sessionId, String attributeName);

		/**
		 * UserId changed in session.
		 *
		 * @param sessionId session id
		 * @param userId	user id
		 */
		void currentUserIdChanged(String sessionId, String userId);

		/**
		 * Editor id changed in session.
		 *
		 * @param sessionId session id
		 * @param editorId  editor id
		 */
		void editorIdChanged(String sessionId, String editorId);

		/**
		 * Keep alive call. Simplest call to distributor service, it will adjust Distributed session
		 * expiration time.
		 *
		 * @param sessionId	id of session
		 */
		void keepAliveCall(String sessionId);

	}


}

