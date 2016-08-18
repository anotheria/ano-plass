package net.anotheria.anoplass.api;

/**
 * Base exception class for all exceptions thrown by the api.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class APIException extends Exception{

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = -8378944843859795925L;

	/**
	 * Constructor.
	 */
	public APIException(){
		
	}

	/**
	 * Constructor.
	 *
	 * @param message string message
	 */
	public APIException(String message){
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message string message
	 * @param cause exception cause
	 */
	public APIException(String message, Exception cause){
		super(message, cause);
	}
}
