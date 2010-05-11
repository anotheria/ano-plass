package net.anotheria.anoplass.api.session;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * The default implementation of the APISession.
 * @author lrosenberg
 */
public class APISessionImpl implements APISession, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The id of the session
	 */
	private String id;
	/**
	 * Internal attribute map
	 */
	private Map<String, AttributeWrapper> attributes;
	/**
	 * Ip address of the user
	 */
	private String ipAddress;
	/**
	 * User-agent of the users browser
	 */
	private String userAgent;
	/**
	 * Reference id (the http session id)
	 */
	private String referenceId;
	/**
	 * Current userid
	 */
	private String currentUserId;
	/**
	 * Current editorid.
	 */
	private String currentEditorId;
	
	private Locale locale;
	/**
	 * A scope which only exists between two executions (like flush).
	 */
	private Map<String, Object> actionScope;
	
	private static Logger log;
	static {
		log = Logger.getLogger(APISessionImpl.class);
	}
	
	/**
	 * Creates a new APISession with the given id.
	 * @param anId
	 */
	APISessionImpl(String anId){
		id = anId;
		attributes = new ConcurrentHashMap<String,AttributeWrapper>();
		actionScope = new ConcurrentHashMap<String, Object>();
	}

	@Override public Object getAttribute(String key) {
		AttributeWrapper wrapper = attributes.get(key);
		return wrapper == null ? null : wrapper.getValue();
	}

	@Override public String getId() {
		return id;
	}

	@Override public void setAttribute(String key, int policy, Object value) {
		attributes.put(key, new AttributeWrapper(key, value, policy));
	}
	
	@Override public void setAttribute(String key, int policy, Object value, long expiresWhen) {
		attributes.put(key, new AttributeWrapper(key, value, policy, expiresWhen));
	}

	@Override public void setAttribute(String key, Object value) {
		setAttribute(key, APISession.POLICY_DEFAULT, value);
	}
	
	@Override public void removeAttribute(String key){
		attributes.remove(key);
	}
	
	@Override public String toString() {
		return "Id: "+id+", attributes: "+attributes.size();
	}
	
	@Override public String getIpAddress() {
		return ipAddress;
	}

	@Override public void setIpAddress(String anIpAddress) {
		String oldIpAdress = ipAddress;
		ipAddress = anIpAddress;
		if (oldIpAdress!=null && !(oldIpAdress.equals(ipAddress)))
			log.warn(this + " session switches ip from "+oldIpAdress+", to: "+ipAddress);
	}

	@Override public String getUserAgent() {
		return userAgent;
	}

	@Override public void setUserAgent(String anUserAgent) {
		userAgent = anUserAgent;
	}

	/**
	 * Used internally to clear all attributes.
	 */
	void clear(){
		attributes.clear();
	}
	
	/**
	 * Called on logout. Removes all attributes except those with the POLICY_SURVIVE_LOGOUT.
	 */
	@Override public void cleanupOnLogout() {
		for (AttributeWrapper w : getAttributes()){
			if (!PolicyHelper.isPolicySet(w.getPolicy(), POLICY_SURVIVE_LOGOUT))
				attributes.remove(w.getKey());
		}
	}
		
	/**
	 * Returns all attributes
	 * @return
	 */
	Collection<AttributeWrapper> getAttributes(){
		return attributes.values();
	}
	
	/**
	 * Sets an attributewrapper
	 * @param w
	 */
	void setAttributeWrapper(AttributeWrapper w){
		attributes.put(w.getKey(), w);
	}
		
	/**
	 * Dump session to stream for debug purposes
	 * @param out
	 */
	public void dumpSession(PrintStream out){
		out.println("API Session with id: "+getId()+", Attributes: "+attributes);
		for (AttributeWrapper a : attributes.values())
			System.out.println(a.getKey()+" = "+a.getValue());
	}

	/**
	 * Dump session to log for debug purposes
	 * @param log
	 */
	public void dumpSession(Logger log){
		log.debug("API Session with id: "+getId()+", Attributes: "+attributes);
	}

	
	public String getReferenceId() {
    	return referenceId;
    }

	public void setReferenceId(String referenceId) {
    	this.referenceId = referenceId;
    }

	@Override public String getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(String currentUserId) {
		this.currentUserId = currentUserId;
	}

	@Override public String getCurrentEditorId() {
		return currentEditorId;
	}

	public void setCurrentEditorId(String currentEditorId) {
		this.currentEditorId = currentEditorId;
	}
	
	public void addAttributeToActionScope(String name, Object attribute){
		actionScope.put(name, attribute);
	}
	
	public Map<String,Object> getActionScope(){
		return actionScope;
	}
	
	public void resetActionScope(){
		actionScope.clear();
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
}

