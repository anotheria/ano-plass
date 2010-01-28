package net.anotheria.anoplass.api.session;

import net.anotheria.anoprise.sessiondistributor.SessionDistributorServiceException;

public class APISessionDistributionException extends Exception{
	public APISessionDistributionException(String message){
		super(message);
	}
	
	public APISessionDistributionException(SessionDistributorServiceException cause){
		super("SessionDistributorService failed: "+cause.getMessage(), cause);
	}
}
