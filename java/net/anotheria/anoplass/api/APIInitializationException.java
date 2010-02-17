package net.anotheria.anoplass.api;

public class APIInitializationException extends Error{
	public APIInitializationException(Class<? extends API> identifier, Exception cause){
		super("API Initialization of "+identifier+" failed because "+cause.getMessage(), cause);
	}
}
