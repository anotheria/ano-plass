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
	 * If false the sessions won't be tied to http sessions.
	 */
	@Configure private boolean associateSessions = false;

	/**
	 * If api's should be monitored. Default true.
	 */
	@Configure private boolean enableApiMonitoring = true;
	
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

	public boolean isEnableApiMonitoring() {
		return enableApiMonitoring;
	}

	public void setEnableApiMonitoring(boolean enableApiMonitoring) {
		this.enableApiMonitoring = enableApiMonitoring;
	}
}
