package net.anotheria.anoplass.api;

import net.anotheria.anoplass.api.generic.observation.ObservationAPITest;
import net.anotheria.anoplass.api.mock.MockingTestSuite;
import net.anotheria.anoplass.api.session.APISessionManagerSingleAndSessionDistributionTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value=Suite.class)
@SuiteClasses(value={APISessionManagerSingleAndSessionDistributionTest.class, MockingTestSuite.class, ObservationAPITest.class} )
public class RunAllTests {

}
