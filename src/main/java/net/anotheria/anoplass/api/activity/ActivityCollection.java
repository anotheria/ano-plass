package net.anotheria.anoplass.api.activity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class holds the last activities of a user. Every activity is an url.
 *
 * @author lrosenberg
 * @since 03.07.17 23:23
 */
public class ActivityCollection {
	private List<Activity> activities;
	private int limit;

	public ActivityCollection(int aLimit){
		limit = aLimit;
		activities = new CopyOnWriteArrayList<>();
	}

	public void add(String url) {
		add(new Activity(url));
	}

	public void add(Activity activity) {
		int tolerableLimit = (int)(limit*1.1);
		if (activities.size()>tolerableLimit){
			trimList(limit);
		}
		activities.add(activity);

	}

	public List<Activity> getActivites(){
		return activities;
	}

	/*unit test*/ synchronized void trimList(int toLimit){
		int listSize = activities.size();
		List<Activity> newList = new CopyOnWriteArrayList<Activity>();
		for (int i=listSize - toLimit; i<listSize; i++){
			newList.add(activities.get(i));
		}
		activities = newList;

	}

	@Override
	public String toString() {
		return "ActivityCollection{" +
				"activities=" + activities +
				'}';
	}
}
