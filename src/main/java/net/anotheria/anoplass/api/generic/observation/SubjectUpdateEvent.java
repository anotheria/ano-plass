package net.anotheria.anoplass.api.generic.observation;

import net.anotheria.util.Date;

/**
 * Subject updated event.
 *
 * @author another
 * @version $Id: $Id
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


	/**
	 * <p>Getter for the field <code>originator</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getOriginator() {
		return originator;
	}

	/**
	 * <p>Setter for the field <code>originator</code>.</p>
	 *
	 * @param originator a {@link java.lang.String} object.
	 */
	public void setOriginator(String originator) {
		this.originator = originator;
	}

	/**
	 * <p>Getter for the field <code>subject</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * <p>Setter for the field <code>subject</code>.</p>
	 *
	 * @param subject a {@link java.lang.String} object.
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * <p>Getter for the field <code>timestamp</code>.</p>
	 *
	 * @return a long.
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * <p>Getter for the field <code>targetUserId</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTargetUserId() {
		return targetUserId;
	}

	/**
	 * <p>Setter for the field <code>targetUserId</code>.</p>
	 *
	 * @param targetUserId a {@link java.lang.String} object.
	 */
	public void setTargetUserId(String targetUserId) {
		this.targetUserId = targetUserId;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("S: ").append(getSubject()).append(", O: ").append(getOriginator());
		b.append(", U: ").append(targetUserId == null ? "current" : targetUserId);
		b.append(", T: ").append(getTimestamp()).append(", D: ").append(new Date(getTimestamp()));
		return b.toString();
	}


}
