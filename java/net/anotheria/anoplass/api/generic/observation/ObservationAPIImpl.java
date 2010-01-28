package net.anotheria.anoplass.api.generic.observation;

import net.anotheria.anoplass.api.common.APIInitException;
import net.anotheria.anoplass.api.common.AbstractAPIImpl;
import net.java.dev.moskito.util.storage.Storage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class ObservationAPIImpl extends AbstractAPIImpl implements ObservationAPI{
	
	private Storage<String, List<Observer>> subjects;
	
	public void init() throws APIInitException {
		super.init();
		subjects = Storage.createConcurrentHashMapStorage("subjects");
		//createDebugObserver();
	}

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

	@Override public void fireSubjectUpdateForUser(String subject, String originator, String userId) {
		log.debug("Firing update event for user "+userId+", originator: "+originator+" and subject: "+subject);
		List<Observer> observers = subjects.get(subject);
		if (observers == null || observers.size() == 0)
			return;
		SubjectUpdateEvent event = new SubjectUpdateEvent(subject, originator, userId);
		for (Observer anObserver : observers){
			try{
				anObserver.notifySubjectUpdatedForUser(event);
			}catch(Exception e){
				log.warn("(Uncaught exception in observer: "+anObserver+" .notifySubjectUpdatedForCurrentUser("+event+")",e);
			}
		}
	}

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
