package net.anotheria.anoplass.api.generic.observation;

import net.anotheria.util.Date;

public class SubjectUpdateEvent {
	
	private String subject;
	
	/**
	 * The originator who fired the event. Typically a class name should be the content of this field.
	 */
	private String originator;
	
	private long timestamp;
	
	/**
	 * The user id this event is relying on. If targetUserId == null, current user is the target.
	 */
	private String targetUserId;
	
	private SubjectUpdateEvent(){
		timestamp = System.currentTimeMillis();
	}
	
	SubjectUpdateEvent(String aSubject, String anOriginator, String aUserId) {
		this();
		subject = aSubject;
		originator = anOriginator;
		targetUserId = aUserId;
	}
	
	SubjectUpdateEvent(String aSubject, String anOriginator) {
		this(aSubject, anOriginator, null);
	}
	
	
	public String getOriginator() {
		return originator;
	}
	public void setOriginator(String originator) {
		this.originator = originator;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public String getTargetUserId() {
		return targetUserId;
	}
	public void setTargetUserId(String targetUserId) {
		this.targetUserId = targetUserId;
	}

	@Override
	public String toString(){
		StringBuilder b = new StringBuilder("S: ").append(getSubject()).append(", O: ").append(getOriginator());
		b.append(", U: ").append(targetUserId == null ? "current":targetUserId);
		b.append(", T: ").append(getTimestamp()).append(", D: ").append(new Date(getTimestamp()));
		return b.toString();
	}

	
	
}
