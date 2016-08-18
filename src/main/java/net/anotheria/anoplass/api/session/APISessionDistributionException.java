package net.anotheria.anoplass.api.session;

import net.anotheria.anoprise.sessiondistributor.SessionDistributorServiceException;
/**
 * Exception which is thrown by the APISessionDistributionHelper.
 *
 * @author lrosenberg
 * @version $Id: $Id
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
	 * @param cause {@link net.anotheria.anoprise.sessiondistributor.SessionDistributorServiceException}
	 */
	public APISessionDistributionException(SessionDistributorServiceException cause){
		super("SessionDistributorService failed: "+cause.getMessage(), cause);
	}


}
