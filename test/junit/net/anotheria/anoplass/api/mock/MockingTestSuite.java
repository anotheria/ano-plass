package net.anotheria.anoplass.api.mock;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value=Suite.class)
@SuiteClasses(value={TestAPITest.class, TestAPIMaskTest.class, TestAPIMockTest.class} )
public class MockingTestSuite {

}
 