package net.anotheria.anoplass.api.generic.security;

import net.anotheria.anoplass.api.API;
import net.anotheria.anoplass.api.APIException;

public interface SecurityAPI extends API{
	boolean isAllowedTo(String action, SecurityObject subject, SecurityObject object) throws APIException;
	
	void ensureIsAllowedTo(String action, SecurityObject subject, SecurityObject object) throws APIException,SecurityException;
}
