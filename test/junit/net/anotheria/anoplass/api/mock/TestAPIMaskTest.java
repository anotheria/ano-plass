package net.anotheria.anoplass.api.mock;

import java.lang.reflect.Method;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.validation.ValidationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestAPIMaskTest {
	@Before public void init() throws Exception{
		APIFinder.setMaskingEnabled(true);
		APIFinder.addAPIFactory(TestAPI.class, new TestAPIFactory());
		
		MaskMethodRegistry.addMaskMethod(TestAPI.class.getMethod("methodIsReturning42"), new ReturnObjectMaskMethod<TestAPI>(43));
		MaskMethodRegistry.addMaskMethod(TestAPI.class.getMethod("methodIsReturningTrue"), new ReturnObjectMaskMethod<TestAPI>(false));
		MaskMethodRegistry.addMaskMethod(TestAPI.class.getMethod("methodIsDoingNothing"), new APIMaskMethod<TestAPI>() {
			public Object invoke(Method method, Object[] args, TestAPI maskedAPI) throws APIException{
				throw new RuntimeException("foo!");
			}
		});
		//mask addition with substraction.
		MaskMethodRegistry.addMaskMethod(TestAPI.class.getMethod("add", int.class, int.class), new APIMaskMethod<TestAPI>() {
			public Object invoke(Method method, Object[] args, TestAPI maskedAPI) throws APIException{
				int a = (Integer)args[0];
				int b = (Integer)args[1];
				return a - b;
			}
		});
		
	}

	@After public void cleanup(){
		APIFinder.findAPI(TestAPI.class).deInit();
		APIFinder.cleanUp();
	}

	
	//this test just tests 
	@Test public void testStandartBehavior() throws APIException{
		TestAPI testAPI = APIFinder.findAPI(TestAPI.class);
		
		//we change the behavior of the four methods.
		
		assertEquals(43, testAPI.methodIsReturning42());
		assertEquals(false, testAPI.methodIsReturningTrue());
		assertEquals(5, testAPI.add(10, 5));
		try{
			testAPI.methodIsDoingNothing();
			fail("expected exception");
		}catch(RuntimeException e){
			
		}
		
		//don't change those three
		assertEquals(50, testAPI.mul(10, 5));
		
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
