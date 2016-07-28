package net.anotheria.anoplass.api.generic.observation;

import net.anotheria.util.Date;

/**
 * Subject updated event.
 */
public class SubjectUpdateEvent {
	/**
	 * SubjectUpdateEvent 'subject'.
	 */
	private String subject;

	/**
	 * The originator who fired the event. Typically a class name should be the content of this field.
	 */
	private String originator;

	/**
	 * SubjectUpdateEvent 'timeStamp'.
	 */
	private long timestamp;

	/**
	 * The user id this event is relying on. If targetUserId == null, current user is the target.
	 */
	private String targetUserId;

	/**
	 * Constructor.
	 */
	private SubjectUpdateEvent() {
		timestamp = System.currentTimeMillis();
	}

	/**
	 * Constructor.
	 *
	 * @param aSubject	 subject
	 * @param anOriginator originator
	 * @param aUserId	  user id
	 */
	SubjectUpdateEvent(String aSubject, String anOriginator, String aUserId) {
		this();
		subject = aSubject;
		originator = anOriginator;
		targetUserId = aUserId;
	}

	/**
	 * Constructor.
	 * @param aSubject subject
	 * @param anOriginator originator
	 */
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
	public String toString() {
        StringBuilder b = new StringBuilder("S: ").append(subject).append(", O: ").append(originator);
		b.append(", U: ").append(targetUserId == null ? "current" : targetUserId);
        b.append(", T: ").append(timestamp).append(", D: ").append(new Date(timestamp));
		return b.toString();
	}


}
