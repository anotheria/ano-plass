package net.anotheria.anoplass.api.session;

import net.anotheria.anoprise.sessiondistributor.SessionDistributorServiceException;
/**
 * Exception which is thrown by the APISessionDistributionHelper.
 * @author lrosenberg
 *
 */
public class APISessionDistributionException extends Exception{
	/**
	 * SerialVersuinUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * @param message
	 */
	public APISessionDistributionException(String message){
		super(message);
	}
	
	/**
	 * Constructor.
	 * @param cause
	 */
	public APISessionDistributionException(SessionDistributorServiceException cause){
		super("SessionDistributorService failed: "+cause.getMessage(), cause);
	}
}
