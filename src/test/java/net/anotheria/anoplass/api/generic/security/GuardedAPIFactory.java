package net.anotheria.anoplass.api.generic.security;

import net.anotheria.anoplass.api.APIFactory;

public class GuardedAPIFactory implements APIFactory<GuardedAPI>{

	@Override
	public GuardedAPI createAPI() {
		return new GuardedAPIImpl();
	}

}
