package net.anotheria.anoplass.api.generic.security;

import net.anotheria.anoplass.api.API;
import net.anotheria.anoplass.api.APIException;

/**
 * <p>SecurityAPI interface.</p>
 *
 * @author another
 * @version $Id: $Id
 */
public interface SecurityAPI extends API{
	/**
	 * <p>isAllowedTo.</p>
	 *
	 * @param action a {@link java.lang.String} object.
	 * @param subject a {@link net.anotheria.anoplass.api.generic.security.SecurityObject} object.
	 * @param object a {@link net.anotheria.anoplass.api.generic.security.SecurityObject} object.
	 * @return a boolean.
	 * @throws net.anotheria.anoplass.api.APIException if any.
	 */
	boolean isAllowedTo(String action, SecurityObject subject, SecurityObject object) throws APIException;
	
	/**
	 * <p>ensureIsAllowedTo.</p>
	 *
	 * @param action a {@link java.lang.String} object.
	 * @param subject a {@link net.anotheria.anoplass.api.generic.security.SecurityObject} object.
	 * @param object a {@link net.anotheria.anoplass.api.generic.security.SecurityObject} object.
	 * @throws net.anotheria.anoplass.api.APIException if any.
	 * @throws net.anotheria.anoplass.api.generic.security.SecurityException if any.
	 */
	void ensureIsAllowedTo(String action, SecurityObject subject, SecurityObject object) throws APIException,SecurityException;
}
