package net.anotheria.anoplass.api.mock;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.AbstractAPIImpl;
import net.anotheria.anoplass.api.validation.ValidationException;

public class TestAPIImpl extends AbstractAPIImpl implements TestAPI{

	@Override
	public void methodIsDoingNothing() throws APIException {
		//do nothing
	}

	@Override
	public int methodIsReturning42() throws APIException {
		return 42;
	}

	@Override
	public boolean methodIsReturningTrue() throws APIException {
		return true;
	}

	@Override
	public void methodIsThrowingException() throws APIException {
		throw new APIException("APIException");
	}

	@Override
	public void methodIsThrowingValidationException() throws APIException {
		throw new ValidationException();
	}

	@Override
	public int add(int a, int b) throws APIException {
		return a+b;
		
	}

	@Override
	public int mul(int a, int b) throws APIException {
		return a*b;
	}

}
