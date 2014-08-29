package net.anotheria.anoplass.api.mock;

import net.anotheria.anoplass.api.APIFactory;

public class TestAPIFactory implements APIFactory<TestAPI>{

	@Override
	public TestAPI createAPI() {
		return new TestAPIImpl();
	}
	
}
