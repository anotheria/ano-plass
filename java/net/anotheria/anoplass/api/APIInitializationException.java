package net.anotheria.anoplass.api;

public class APIInitializationException extends Error{
	/**
	 * Default svuid.
	 */
	private static final long serialVersionUID = 1L;

	public APIInitializationException(Class<? extends API> identifier, Exception cause){
		super("API Initialization of "+identifier+" failed because "+cause.getMessage(), cause);
	}
}
