package net.anotheria.anoplass.api.generic.observation;

import org.apache.log4j.Logger;
/**
 * Debugging utility which simply logs out all events it receives.
 * @author lrosenberg
 *
 */
public class DebugObserver implements Observer{

	@Override public void notifySubjectUpdatedForCurrentUser(SubjectUpdateEvent event) {
		debugOutEvent("currentUser", event);
	}

	@Override public void notifySubjectUpdatedForUser(SubjectUpdateEvent event) {
		debugOutEvent("aUser", event);
		
	}
	
	/**
	 * Debugs out event.
	 * @param method method which has been called, either currentUser or aUser.
	 * @param event event for debugging.
	 */
	private void debugOutEvent(String method, SubjectUpdateEvent event){
		Logger log = Logger.getLogger(this.getClass());
		log.debug("Subject Fired: "+method+", event: "+event);
	}
	
}
