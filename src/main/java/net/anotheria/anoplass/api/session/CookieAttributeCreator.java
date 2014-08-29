package net.anotheria.anoplass.api.session;

/**
 * CookieAttributeCreator interface.
 */
public interface CookieAttributeCreator {

	/**
	 * Create Attribute method.
	 * @param value {@link String} value
	 * @return {@link Object}
	 */
	Object createAttribute(String value);
}
