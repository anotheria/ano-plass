package net.anotheria.anoplass.api.activity;

import net.anotheria.util.NumberUtils;

/**
 * TODO comment this class
 *
 * @author lrosenberg
 * @since 03.07.17 23:24
 */
public class Activity {
	private long timestamp;
	private String url;

	public Activity(String anUrl){
		url = anUrl;
		timestamp = System.currentTimeMillis();
	}

	public long getTimestamp(){
		return timestamp;
	}

	public String getUrl(){
		return url;
	}

	@Override public String toString(){
		return NumberUtils.makeISO8601TimestampString(timestamp)+" "+url;
	}

}
