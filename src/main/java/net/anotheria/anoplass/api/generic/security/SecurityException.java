package net.anotheria.anoplass.api.generic.security;

/**
 * <p>SecurityException class.</p>
 *
 * @author another
 * @version $Id: $Id
 */
public class SecurityException extends RuntimeException{
	/**
	 * Default serial version uid. 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for SecurityException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public SecurityException(String message){
		super(message);
	}
}
