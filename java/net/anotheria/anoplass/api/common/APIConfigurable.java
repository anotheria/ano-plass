package net.anotheria.anoplass.api.common;

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
	@Configure	private boolean verboseMethodCalls;

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

}
