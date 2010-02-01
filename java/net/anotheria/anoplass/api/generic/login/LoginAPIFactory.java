package net.anotheria.anoplass.api.generic.login;

import net.anotheria.anoplass.api.APIFactory;
/**
 * The factory for the current login api.
 */
public class LoginAPIFactory implements APIFactory<LoginAPI>{

	@Override public LoginAPI createAPI() {
		return new LoginAPIImpl();
	}

}
