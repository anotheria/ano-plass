package net.anotheria.anoplass.api;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

/**
 * ConfigureME based Config. APIConfigurable - for API configuring.
 *
 * @author another
 * @version $Id: $Id
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
	 * Support remoting for observation. If true remoting events should be propagated.
	 */
	@Configure private boolean supportRemotingForObservation = false;
	
	/**
	 * Constructor.
	 */
	public APIConfigurable(){
	}

	/**
	 * <p>isVerboseMethodCalls.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isVerboseMethodCalls() {
		return verboseMethodCalls;
	}

	/**
	 * <p>Setter for the field <code>verboseMethodCalls</code>.</p>
	 *
	 * @param value a boolean.
	 */
	public void setVerboseMethodCalls(boolean value) {
		verboseMethodCalls = value;
	}

	/**
	 * <p>isAssociateSessions.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isAssociateSessions() {
		return associateSessions;
	}

	/**
	 * <p>Setter for the field <code>associateSessions</code>.</p>
	 *
	 * @param associateSessions a boolean.
	 */
	public void setAssociateSessions(boolean associateSessions) {
		this.associateSessions = associateSessions;
	}

	/**
	 * <p>isEnableApiMonitoring.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isEnableApiMonitoring() {
		return enableApiMonitoring;
	}

	/**
	 * <p>Setter for the field <code>enableApiMonitoring</code>.</p>
	 *
	 * @param enableApiMonitoring a boolean.
	 */
	public void setEnableApiMonitoring(boolean enableApiMonitoring) {
		this.enableApiMonitoring = enableApiMonitoring;
	}

	public boolean isSupportRemotingForObservation() {
		return supportRemotingForObservation;
	}

	public void setSupportRemotingForObservation(boolean supportRemotingForObservation) {
		this.supportRemotingForObservation = supportRemotingForObservation;
	}
}
