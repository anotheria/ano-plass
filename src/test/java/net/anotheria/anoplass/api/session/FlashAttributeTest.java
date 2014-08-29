package net.anotheria.anoplass.api.session;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * TODO comment this class
 *
 * @author lrosenberg
 * @since 26.09.13 16:48
 */
public class FlashAttributeTest {
	@Test public void testFlash() throws Exception{
		APISession session = APISessionManager.getInstance().createSession("bloob");
		String a = "AAA";
		String b = "BBB";

		session.setAttribute("a", a);
		session.setAttribute("b", APISession.POLICY_FLASH, b);

		//first get should work.
		assertEquals(a, session.getAttribute("a"));
		assertEquals(b, session.getAttribute("b"));

		//second get shouldn't work
		assertEquals(a, session.getAttribute("a"));
		assertNull(session.getAttribute("b"));
	}
}
