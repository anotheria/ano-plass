package net.anotheria.anoplass.api.generic.security;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFactory;
import net.anotheria.anoplass.api.AbstractAPIImpl;

public class GrantAllSecurityAPIImpl extends AbstractAPIImpl implements SecurityAPI, APIFactory<SecurityAPI>{

	@Override
	public SecurityAPI createAPI() {
		return this;
	}

	@Override
	public boolean isAllowedTo(String action, SecurityObject subject,
			SecurityObject object) throws APIException {
		return true;
	}

	@Override
	public void ensureIsAllowedTo(String action, SecurityObject subject,
			SecurityObject object) throws APIException, SecurityException {
	}

}
