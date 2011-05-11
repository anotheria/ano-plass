package net.anotheria.anoplass.api.generic.observation;

/**
 * Defines generic Observation Subjects.
 * @author denis
 *
 */
public final class ObservationSubjects {
	/**
	 * User logs in into the system.
	 */
	public static final String LOGIN = "login";
	
	/**
	 * User logs out.
	 */
	public static final String LOGOUT = "logout";

	/**
	 * Private constructor.
	 */
	private ObservationSubjects(){
		throw new IllegalAccessError("Can't be instantiated.");
	}
	
}
