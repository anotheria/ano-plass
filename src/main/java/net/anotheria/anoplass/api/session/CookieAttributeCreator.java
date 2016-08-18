package net.anotheria.anoplass.api.session;

/**
 * CookieAttributeCreator interface.
 *
 * @author another
 * @version $Id: $Id
 */
public interface CookieAttributeCreator {

	/**
	 * Create Attribute method.
	 *
	 * @param value {@link java.lang.String} value
	 * @return {@link java.lang.Object}
	 */
	Object createAttribute(String value);
}
