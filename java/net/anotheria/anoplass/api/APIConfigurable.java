package net.anotheria.anoplass.api;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

/**
 * ConfigureME based Config. APIConfigurable - for API configuring.
 */
@ConfigureMe(name = "apiconfig")
public class APIConfigurable {
	/**
	 * APIConfigurable 'verboseMethodCalls'.
	 */
	@Configure private boolean verboseMethodCalls;

	/**
	 * 
	 */
	@Configure private boolean associateSessions = false;  
	
	/**
	 * Constructor.
	 */
	public APIConfigurable(){
	}

	public boolean isVerboseMethodCalls() {
		return verboseMethodCalls;
	}

	public void setVerboseMethodCalls(boolean value) {
		verboseMethodCalls = value;
	}

	public boolean isAssociateSessions() {
		return associateSessions;
	}

	public void setAssociateSessions(boolean associateSessions) {
		this.associateSessions = associateSessions;
	}

}
