package net.anotheria.anoplass.api.session;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.anotheria.util.IdCodeGenerator;
import net.java.dev.moskito.util.storage.Storage;

import org.apache.log4j.Logger;

/**
 * This class manages api sessions. 
 * @author lrosenberg
 *
 */
public class APISessionManager {
	/**
	 * Storage for api sessions.
	 */
	private Storage<String, APISession> sessions;
	/**
	 * Storage for reference ids. Reference ids are ids of external connected objects, for example httpsession, and are used to propagate lifecycle changes of the external objects to the corresponding session.
	 */
	private Storage<String, String> referenceIds;
	/**
	 * Singleton instance of the manager.
	 */
	private static APISessionManager instance = new APISessionManager();
	/**
	 * Listeners for session events.
	 */
	private List<APISessionManagerListener> listeners;
	/**
	 * Logger.
	 */
	protected Logger log = Logger.getLogger(this.getClass());

	private APISessionManager(){
		sessions = Storage.createConcurrentHashMapStorage("sessions");
		referenceIds = Storage.createConcurrentHashMapStorage("session-refIds");
		listeners = new CopyOnWriteArrayList<APISessionManagerListener>();
	}
	
	/**
	 * Returns the single instance of this class.
	 * @return
	 */
	public static APISessionManager getInstance() {
		return instance;
	}

	/**
	 * Creates a new session and copies all attributes (incl. policies) into it. If policy REUSE_WRAPPER is used, modified attributes will be modified in both sessions.
	 * @param sourceSessionId
	 * @param referenceId
	 * @return
	 */
	public APISession createSessionCopy(String sourceSessionId, String referenceId){
		APISessionImpl source = (APISessionImpl)getSession(sourceSessionId);
		if (source==null)
			return null;
		
		APISessionImpl target = (APISessionImpl)createSession(referenceId);
		Collection<AttributeWrapper> wrappers = source.getAttributes();
		for (AttributeWrapper w : wrappers)
			target.setAttributeWrapper(w);
		
		
		target.setCurrentEditorId(source.getCurrentEditorId());
		target.setCurrentUserId(source.getCurrentUserId());
		
		return target;
	}
	
	/**
	 * Creates a new session with a reference id. The reference id is the id of the connected object (for example httpsession) and is used to retrieve the api session later in the owning 
	 * object lifecycle update. For example if the httpsession expires the session listener notifies the APISessionManager and it can expire corresponding APISession.
	 * @param referenceId
	 * @return
	 */
	public APISession createSession(String referenceId){
		APISession s = new APISessionImpl(IdCodeGenerator.generateCode(30));
		((APISessionImpl)s).setReferenceId(referenceId);
		sessions.put(s.getId(), s);
		referenceIds.put(referenceId, s.getId());
		log.debug("createSession, id=" + s.getId());
		return s;
	}
	
	/**
	 * Returns a list with all reference ids.
	 * @return
	 */
	public ArrayList<String> getReferenceIds() {
		ArrayList<String> ret = new ArrayList<String>(referenceIds.size());
		for ( String id : referenceIds.keySet() )
			ret.add(id);
		return ret;
	}
	
	/**
	 * Returns a list with all session ids.
	 * @return
	 */
	public ArrayList<String> getSessionIds() {
		ArrayList<String> ret = new ArrayList<String>(sessions.size());
		for ( String id : sessions.keySet() )
			ret.add(id);
		return ret;
	}
	
	/**
	 * Returns the session with the given id.
	 * @param id
	 * @return
	 */
	public APISession getSession(String id){
		return sessions.get(id);
	}
	/**
	 * Returns the number of known session.
	 * @return
	 */
	public int getSessionCount(){
		return sessions.size();
	}
	
	/**
	 * Returns a session by the reference id of its connected object.
	 * @param aReferenceId
	 * @return
	 */
	public APISession getSessionByReferenceId(String aReferenceId){
		String sessionId; 
		sessionId = referenceIds.get(aReferenceId);
		if (sessionId==null)
			throw new RuntimeException("Can't find session for referenceId: "+aReferenceId);
		return getSession(sessionId);
	}
	
	/**
	 * Destroys a session via its reference id. This is used by APISessionListener to propagate session timeout event from http session to corresponding api session.
	 * @param referenceId
	 */
	public void destroyAPISessionByReferenceId(String referenceId){
		//System.out.println("DESTROY API SESSION BY REFERENCE CALLED: "+referenceId);
		APISession session = null;
		String sessionId = referenceIds.get(referenceId);
		if (sessionId!=null){
			session = sessions.remove(sessionId);			
			for (APISessionManagerListener listener : listeners)
				listener.apiSessionDestroyed(session);
		}
		if (session==null)
			log.info("HttpSession expired "+referenceId+", no api session connected");
		else
			log.info("HttpSession expired: "+referenceId+", ApiSessionId: "+session.getId());
		//System.out.println("DESTROY API SESSION FINISHED: "+referenceId+", "+sessionId+", "+session);
		/*if (session!=null)
			((APISessionImpl)session).dumpSession(System.out);*/
		if (session!=null) {
			((APISessionImpl)session).clear();
			referenceIds.remove(referenceId);
		}
	}
	
	/**
	 * Destroys an api session.
	 * @param sessionId
	 */
	public void destroyAPISessionBySessionId(String sessionId){
		APISession session = null;
		if (sessionId!=null){
			session = sessions.remove(sessionId);			
			for (APISessionManagerListener listener : listeners)
				listener.apiSessionDestroyed(session);
		}
		if (session!=null){
			((APISessionImpl)session).clear();
			referenceIds.remove(((APISessionImpl)session).getReferenceId());
		}
		
	}

	/**
	 * Adds an api session listener.
	 * @param listener
	 */
	public void addAPISessionManagerListener(APISessionManagerListener listener){
		listeners.add(listener);
	}
	
}