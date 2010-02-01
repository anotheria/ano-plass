package net.anotheria.anoplass.api.activity;


import net.anotheria.anoplass.api.API;
/**
 * This api is used to track users activity. 
 * @author lrosenberg
 *
 */
public interface ActivityAPI extends API{
	/**
	 * Called by the APIFilter for each url called by the session.
	 * @param url
	 */
	void notifyMyActivity(String url);

}
