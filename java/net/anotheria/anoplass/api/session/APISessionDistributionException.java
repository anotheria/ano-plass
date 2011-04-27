package net.anotheria.anoplass.api.session;

import net.anotheria.anoprise.sessiondistributor.SessionDistributorServiceException;
/**
 * Exception which is thrown by the APISessionDistributionHelper.
 * @author lrosenberg
 *
 */
public class APISessionDistributionException extends Exception{
	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -976156015267674943L;

	/**
	 * Constructor.
	 *
	 * @param message string message.
	 */
	public APISessionDistributionException(String message){
		super(message);
	}
	
	/**
	 * Constructor.
	 *
	 * @param cause {@link SessionDistributorServiceException}
	 */
	public APISessionDistributionException(SessionDistributorServiceException cause){
		super("SessionDistributorService failed: "+cause.getMessage(), cause);
	}


}
