package net.anotheria.anoplass.api.activity;

import net.anotheria.anoplass.api.APIFactory;

/**
 * Factory for the activity api.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class ActivityAPIFactory implements APIFactory<ActivityAPI> {

	/** {@inheritDoc} */
	@Override
	public ActivityAPI createAPI() {
		return new ActivityAPIImpl();
	}

}

 
