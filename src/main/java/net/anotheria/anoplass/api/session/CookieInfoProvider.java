package net.anotheria.anoplass.api.session;

/**
 * <p>CookieInfoProvider interface.</p>
 *
 * @author another
 * @version $Id: $Id
 */
public interface CookieInfoProvider {
	/**
	 * <p>getCookieDuration.</p>
	 *
	 * @param attributeName a {@link java.lang.String} object.
	 * @param attributeValue a {@link java.lang.Object} object.
	 * @return a long.
	 */
	long getCookieDuration(String attributeName, Object attributeValue);
}
