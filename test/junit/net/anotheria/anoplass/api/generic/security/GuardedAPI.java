package net.anotheria.anoplass.api.generic.security;

import net.anotheria.anoplass.api.API;

public interface GuardedAPI extends API{
	@EnsurePermitted (action="testaction")
	void doSomeAction() ;
	
	@InterceptIfNotPermitted (action="testaction2")
	boolean mayIDoSomeAction();
	
	/**
	 * This method returns the amount of time doSomeAction has been successfully called. Its for testing purposes only.
	 * @return
	 */
	int actionCounter();
}
