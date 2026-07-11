package net.anotheria.anoplass.api.mock;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.validation.ValidationException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestAPITest {
	@BeforeEach public void init(){
		APIFinder.addAPIFactory(TestAPI.class, new TestAPIFactory());
	}
	
	@AfterEach public void cleanup(){
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
