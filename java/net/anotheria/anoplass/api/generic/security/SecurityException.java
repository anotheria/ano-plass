package net.anotheria.anoplass.api.generic.security;

public class SecurityException extends RuntimeException{
	/**
	 * Default serial version uid. 
	 */
	private static final long serialVersionUID = 1L;

	public SecurityException(String message){
		super(message);
	}
}
