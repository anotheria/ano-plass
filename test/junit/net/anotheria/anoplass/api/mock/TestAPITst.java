package net.anotheria.anoplass.api.mock;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.validation.ValidationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestAPITst {
	@Before public void init(){
		APIFinder.addAPIFactory(TestAPI.class, new TestAPIFactory());
	}
	
	@After public void cleanup(){
		APIFinder.findAPI(TestAPI.class).deInit();
		APIFinder.cleanUp();
	}

	
	//this test just tests 
	@Test public void testStandartBehavior() throws APIException{
		TestAPI testAPI = APIFinder.findAPI(TestAPI.class);
		
		assertEquals(42, testAPI.methodIsReturning42());
		assertEquals(50, testAPI.mul(10, 5));
		assertEquals(15, testAPI.add(10, 5));

		assertEquals(true, testAPI.methodIsReturningTrue());
		testAPI.methodIsDoingNothing();
		
		try{
			testAPI.methodIsThrowingValidationException();
			fail("expected validation exception");
		}catch(ValidationException e){}

		try{
			testAPI.methodIsThrowingException();
			fail("expected api exception");
		}catch(APIException e){}
		
		assertNotNull(testAPI.toString());

	}
}
