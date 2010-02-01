package net.anotheria.anoplass.api.generic.observation;

import java.util.HashSet;
import java.util.Set;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ObservationAPITest {
	
	@Before public void setup(){
		APICallContext.getCallContext().reset();
		APIFinder.addAPIFactory(ObservationAPI.class, new ObservationAPIFactory());
	}
	
	@Test public void testEventDelivery() throws APIException{
		ObservationAPI api = APIFinder.findAPI(ObservationAPI.class);
		
		final Set<String> resultSetMe = new HashSet<String>();
		final Set<String> resultSet = new HashSet<String>();
		
		api.registerObserver(new Observer() {
			
			@Override
			public void notifySubjectUpdatedForUser(SubjectUpdateEvent event) {
				resultSet.add(event.getOriginator());
			}
			
			@Override
			public void notifySubjectUpdatedForCurrentUser(SubjectUpdateEvent event) {
				resultSetMe.add(event.getOriginator());
			}
		}, "TEST");
		
		for (int i=0; i<100; i++){
			if ((i&1)==1)
				api.fireSubjectUpdateForCurrentUser("TEST", ""+i);
			else
				api.fireSubjectUpdateForUser("TEST", ""+i, ""+i);
		}
		
		assertEquals(50, resultSet.size());
		assertEquals(50, resultSetMe.size());
	}
	
	@Test public void testErrorInObserver() throws APIException{
		ObservationAPI api = APIFinder.findAPI(ObservationAPI.class);
		
		final Set<String> resultSet = new HashSet<String>();
		
		for (int i=0; i<100; i++){
			api.fireSubjectUpdateForCurrentUser("TEST", ""+i);
			api.fireSubjectUpdateForUser("TEST", ""+i, ""+i);
		}
		assertEquals(0, resultSet.size());
		
		api.registerObserver(new Observer() {
			
			@Override
			public void notifySubjectUpdatedForUser(SubjectUpdateEvent event) {
			}
			
			@Override
			public void notifySubjectUpdatedForCurrentUser(SubjectUpdateEvent event) {
				if (event.getOriginator().length()>1)
					throw new RuntimeException("foo");
				resultSet.add(event.getOriginator());
			}
		}, "TEST");
		
		for (int i=0; i<100; i++){
			if ((i&1)==1)
				api.fireSubjectUpdateForCurrentUser("TEST", ""+i);
			else
				api.fireSubjectUpdateForUser("TEST", ""+i, ""+i);
		}
		
		assertEquals(5, resultSet.size());
	}
}
