package net.anotheria.anoplass.api;

import net.anotheria.anoplass.api.mock.MockingTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value=Suite.class)
@SuiteClasses(value={ApiTestSuite.class, MockingTestSuite.class} )
public class RunAllTests {

}
