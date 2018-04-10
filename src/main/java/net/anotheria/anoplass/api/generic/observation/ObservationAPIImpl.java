package net.anotheria.anoplass.api.generic.observation;

import net.anotheria.anoplass.api.APIConfig;
import net.anotheria.anoplass.api.APIInitException;
import net.anotheria.anoplass.api.AbstractAPIImpl;
import net.anotheria.moskito.core.util.storage.Storage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of the observation api.
 *
 * @author lrosenberg.
 * @version $Id: $Id
 */
public class ObservationAPIImpl extends AbstractAPIImpl implements ObservationAPI{
	
	/**
	 * Internal storage for subject-observer mappings.
	 */
	private Storage<String, List<Observer>> subjects;

	private ObservationAPIEventingBridge observationAPIEventingBridge = null;
	
	/**
	 * <p>init.</p>
	 *
	 * @throws net.anotheria.anoplass.api.APIInitException if any.
	 */
	public void init() throws APIInitException {
		super.init();
		subjects = Storage.createConcurrentHashMapStorage("subjects");
		//createDebugObserver();

		log.debug("Support remoting for observation is "+ APIConfig.supportRemotingForObservation());
		if (APIConfig.supportRemotingForObservation()){
			observationAPIEventingBridge = new ObservationAPIEventingBridge(this);
		}

	}

	/** {@inheritDoc} */
	@Override public void fireSubjectUpdateForCurrentUser(String subject, String originator) {
		log.debug("Firing update event for current user, originator: "+originator+" and subject: "+subject);
		List<Observer> observers = subjects.get(subject);
		if (observers == null || observers.size() == 0)
			return;
		SubjectUpdateEvent event = new SubjectUpdateEvent(subject, originator);
		for (Observer anObserver : observers){
			try{
				anObserver.notifySubjectUpdatedForCurrentUser(event);
			}catch(Exception e){
				log.warn("(Uncaught exception in observer: "+anObserver+" .notifySubjectUpdatedForCurrentUser("+event+")",e);
			}
		}
	}

	/** {@inheritDoc} */
	@Override public void fireSubjectUpdateForUser(String subject, String originator, String userId) {
		log.debug("Firing update event for user "+userId+", originator: "+originator+" and subject: "+subject);
		fireSubjectUpdateForUserInternally(subject, originator, userId);
		//we check both conditions for check if we support remoting, this was we can switch off remoting via config if necessary for this instance, even the bridge is still here.
		boolean supportRemoting = APIConfig.supportRemotingForObservation() && observationAPIEventingBridge != null;
		if (!supportRemoting)
			return;
		observationAPIEventingBridge.fireSubjectUpdateForUser(subject, originator, userId);
	}

	public void remotelyFiredSubjectUpdateForUser(String subject, String originator, String userId) {
		log.debug("Remotely fired update event for user "+userId+", originator: "+originator+" and subject: "+subject);
		fireSubjectUpdateForUserInternally(subject, originator, userId);
	}


	private void fireSubjectUpdateForUserInternally(String subject, String originator, String userId){
		List<Observer> observers = subjects.get(subject);
		if (observers==null || observers.size()==0) {
			return;
		}
		SubjectUpdateEvent event = new SubjectUpdateEvent(subject, originator, userId);
		for (Observer anObserver : observers) {
			try {
				anObserver.notifySubjectUpdatedForUser(event);
			} catch (Exception e) {
				log.warn("(Uncaught exception in observer: " + anObserver + " .notifySubjectUpdatedForCurrentUser(" + event + ")", e);
			}
		}
	}


	/** {@inheritDoc} */
	@Override public void unRegisterObserver(Observer observer, String... someSubjects) {
		for (String subject : someSubjects)
			unRegisterObserver(observer, subject);
	}
	
	/**
	 * Unregisters an observer for a subject
	 * @param observer
	 * @param subject
	 */
	private void unRegisterObserver(Observer observer, String subject) {
		log.debug("Unregistering observer: "+observer+", for subject: "+subject);
		List<Observer> observers = subjects.get(subject);
		if (observers==null || observers.size() == 0)
			return;
		observers.remove(observer);
	}

	/** {@inheritDoc} */
	@Override public void registerObserver(Observer observer, String... someSubjects) {
		for (String subject : someSubjects)
			registerObserver(subject, observer);
	}
	
	/**
	 * Registers an observer for the subject internally.
	 * @param subject
	 * @param observer
	 */
	private void registerObserver(String subject, Observer observer) {
		log.debug("Registering observer: "+observer+", for subject: "+subject);
		List<Observer> observers = subjects.get(subject);
		if (observers==null){
			synchronized (subjects) {
				observers = subjects.get(subject);
				if (observers==null){
					observers = new CopyOnWriteArrayList<Observer>();
					subjects.put(subject, observers);
				}
			}
		}else{
			if (observers.indexOf(observer)!=-1){
				log.debug("Observer "+observer+ " was already registered, skipping. ");
				return;
			}
		}
		observers.add(observer);
		
	}
}
