package net.anotheria.anoplass.api.generic.observation;

import net.anotheria.anoplass.api.API;
/**
 * The observer api allows a publisher/subscriber modification of an subject/observer pattern. Instead of registering observers directly at subjects, the api provides a delegate object 
 * which allows a decoupling between subjects and observers, and allows multiple third-objects to fire events for a subject.
 * Another difference between this api and the publisher/subscriber model is that the event issuer (i.e. subject) is actually blocked until the event is delivered to all observers.
 * Further, the API makes different methods for a user and the current user, the later allowing my-methods in the api to alter caches (since they are called in the context of the user's session).
 * @author lrosenberg
 *
 */
public interface ObservationAPI extends API{
	
	/**
	 * Registers an observer for a list of subjects.
	 * @param observer
	 * @param subject
	 */
	void registerObserver(Observer observer, String... subject);
	/**
	 * Unregisters and observer for a list of subjects.
	 * @param observer 
	 * @param subject
	 */
	void unRegisterObserver(Observer observer, String... subject);

	/**
	 * Fire an update event for the current user. Current-User updates are important for clearing of session-bound caching in 
	 * "My" methods. 
	 * @param subject the subject which was updated.
	 * @param originator the originator of the event, typically the class name, but feel free.
	 */
	void fireSubjectUpdateForCurrentUser(String subject, String originator);
	
	/**
	 * Fire an update event for a user. Those kind of updates are important for clearing of not-session-bound caching in
	 * the api (or whenever). An example is profilepage caching, or favorite caching.
	 * @param subject the subject which was updated.
	 * @param originator the originator of the event, typically the class name, but feel free.
	 * @param userId the userId of the updated user.
	 */
	void fireSubjectUpdateForUser(String subject, String originator, String userId);
}
