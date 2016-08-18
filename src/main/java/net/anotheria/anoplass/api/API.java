package net.anotheria.anoplass.api;

/**
 * Base interface for all api-class interfaces.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public interface API {
	
	/**
	 * Called when an api instance is first created.
	 *
	 * @throws net.anotheria.anoplass.api.APIInitException if init failed
	 */
	void init() throws APIInitException;
	
	/**
	 * Called immediately before shutdown.
	 */
	void deInit() ;
}
