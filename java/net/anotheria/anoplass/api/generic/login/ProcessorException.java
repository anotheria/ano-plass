package net.anotheria.anoplass.api.generic.login;

import net.anotheria.anoplass.api.APIException;
/**
 * Can be thrown by a login/logout preprocessor.
 * @author lrosenberg
 */
public class ProcessorException extends APIException {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new ProcessorException.
	 */
	public ProcessorException(){
		
	}
	
	/**
	 * Creates a new ProcessorException.
	 * @param message string message
	 */
	public ProcessorException(String message){
		super(message);
	}

	/**
	 * Creates a new ProcessorException.
	 * @param message string message
	 * @param cause exception
	 */
	public ProcessorException(String message, Exception cause){
		super(message,cause);
	}

}
