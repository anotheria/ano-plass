package net.anotheria.anoplass.api.mock;

import java.lang.reflect.Method;

import net.anotheria.anoplass.api.API;
import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.validation.ValidationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This test does the same as TestAPITest, but instead of using the real implementation it creates one on the fly.
 * @author lrosenberg.
 *
 */
public class TestAPIMockTst {
	@Before public void init() throws Exception{
		APIFinder.setMockingEnabled(true);
		
		MockMethodRegistry.addMockMethod(TestAPI.class.getMethod("methodIsReturning42"), new ReturnIntegerMockMethod(42));
		MockMethodRegistry.addMockMethod(TestAPI.class.getMethod("methodIsReturningTrue"), new ReturnBooleanMockMethod(true));
		MockMethodRegistry.addMockMethod(TestAPI.class.getMethod("methodIsDoingNothing"), new NoopMockMethod());
		
		MockMethodRegistry.addMockMethod(TestAPI.class.getMethod("methodIsThrowingValidationException"), new ThrowExceptionMockMethod(new ValidationException()));
		MockMethodRegistry.addMockMethod(TestAPI.class.getMethod("methodIsThrowingException"), new ThrowExceptionMockMethod(new APIException()));
		
		MockMethodRegistry.addMockMethod(TestAPI.class.getMethod("mul", int.class, int.class), new APIMockMethod(){
			public Object invoke(Method method, Object[] args){
				return (Integer)args[0]*(Integer)args[1];
			}
		});
		MockMethodRegistry.addMockMethod(TestAPI.class.getMethod("add", int.class, int.class), new APIMockMethod(){
			public Object invoke(Method method, Object[] args){
				return (Integer)args[0]+(Integer)args[1];
			}
		});
		
		MockMethodRegistry.addMockMethod(API.class.getMethod("deInit"), new NoopMockMethod());
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
