package net.anotheria.anoplass.api.activity;

import net.anotheria.anoplass.api.AbstractAPIImpl;

public class ActivityAPIImpl extends AbstractAPIImpl implements ActivityAPI{

	//this is just a test impl of a test api sofar.
	//In the future we will use this api to detect users inactivity.
	
	@Override public void notifyMyActivity(String url) {
		setAttributeInSession("LAST_URL", url);
	}
	
}
