package net.anotheria.anoplass.api.generic.login;

import net.anotheria.anoplass.api.APIFactory;

/**
 * The factory for the current login api.
 *
 * @author another
 * @version $Id: $Id
 */
public class LoginAPIFactory implements APIFactory<LoginAPI> {

	/** {@inheritDoc} */
	@Override
	public LoginAPI createAPI() {
		return new LoginAPIImpl();
	}

}
