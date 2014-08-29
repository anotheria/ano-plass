package net.anotheria.anoplass.api.activity;

import net.anotheria.anoplass.api.APIFactory;

/**
 * Factory for the activity api.
 * @author lrosenberg
 *
 */
public class ActivityAPIFactory implements APIFactory<ActivityAPI> {

	@Override
	public ActivityAPI createAPI() {
		return new ActivityAPIImpl();
	}

}

 