package net.anotheria.anoplass.api.session;

public interface CookieInfoProvider {
	long getCookieDuration(String attributeName, Object attributeValue);
}
