package net.anotheria.anoplass.api;

/**
 * <p>APIInitializationException class.</p>
 *
 * @author another
 * @version $Id: $Id
 */
public class APIInitializationException extends Error{
	/**
	 * Default svuid.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for APIInitializationException.</p>
	 *
	 * @param identifier a {@link java.lang.Class} object.
	 * @param cause a {@link java.lang.Exception} object.
	 */
	public APIInitializationException(Class<? extends API> identifier, Exception cause){
		super("API Initialization of "+identifier+" failed because "+cause.getMessage(), cause);
	}
}
