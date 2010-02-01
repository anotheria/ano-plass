package net.anotheria.anoplass.api;

import net.anotheria.anoplass.api.generic.observation.ObservationAPITest;
import net.anotheria.anoplass.api.session.SessionDistributionTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value=Suite.class)
@SuiteClasses(value={SessionDistributionTest.class, ObservationAPITest.class} )
public class ApiTestSuite {

}
