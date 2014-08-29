package net.anotheria.anoplass.api.mock;

import net.anotheria.anoplass.api.API;
import net.anotheria.anoplass.api.APIException;

public interface TestAPI extends API{
	void methodIsDoingNothing() throws APIException;
	
	int methodIsReturning42() throws APIException;

	boolean methodIsReturningTrue() throws APIException;
	
	void methodIsThrowingException() throws APIException;
	
	void methodIsThrowingValidationException() throws APIException;
	
	int add(int a, int b) throws APIException;
	
	int mul(int a, int b) throws APIException;
}
