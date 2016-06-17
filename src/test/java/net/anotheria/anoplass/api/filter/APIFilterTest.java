package net.anotheria.anoplass.api.filter;

import net.anotheria.anoplass.api.session.APISessionManager;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Simplest concurrent test which checks - SessionCreation functionality with conc - threads...
 *
 * @author h3llka
 */
public class APIFilterTest {
	private static final String SESSION_ID = "http_mock_junit_session_id";

	//Checking session Init method!!!!
	//each thread will generate new Request ( from Same HttpSession) to APIFilter...
	//@ the end  we should have only 1 APISession!!!
	@Test
	public void testSessionInitStuff() {
		//50 threads - to test
		final int nThreads = 150;
		final CountDownLatch prepareLatch = new CountDownLatch(nThreads);
		final CountDownLatch startLatch = new CountDownLatch(1);
		final CountDownLatch stopLatch = new CountDownLatch(nThreads);
		final Set<String> resultSetWithSessionIds = new HashSet<String>();
		//http session for current test  ! This  session will be shared  between all threads!
		final HttpSession session = new MockSession();


		int sessionSizeBeforeTest = APISessionManager.getInstance().getSessionCount();

		//Creating API filter instance - emulating real Server Behaviour!
		final APIFilter filter = new APIFilter();
		try {
			filter.init(null);
		} catch (ServletException e) {
			Assert.fail(e.getMessage());
		}

		for (int i = 0; i < nThreads; i++) {
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						prepareLatch.countDown();
						startLatch.await();
						try {
							resultSetWithSessionIds.add(filter.initSession(new RequestMock(session)).getId());
						} catch (ServletException e) {
							Assert.fail(e.getMessage());
						}
					} catch (InterruptedException e) {
						Assert.fail(e.getMessage());
					} finally {
						stopLatch.countDown();
					}
				}
			};
			t.start();
		}

		try {
			prepareLatch.await();
			startLatch.countDown();

			stopLatch.await();
		} catch (InterruptedException e) {
			Assert.fail(e.getMessage());
		}

		// Now  checking how many sessions Do we have!
		//Should be exactly 1!
		Assert.assertEquals(1, resultSetWithSessionIds.size());
		Assert.assertEquals("Only 1 additional session should be created!!", APISessionManager.getInstance().getSessionIds().size(), sessionSizeBeforeTest + 1);
	}

	/**
	 * Mocked Request implementation which contains only required implemented  methods!.  For current TEst only!!
	 */
	public static final class RequestMock implements HttpServletRequest {

		private HttpSession currentSession = new MockSession();

		private Map<String, String> params;

		private Map<String, Object> attrs;

		public RequestMock(HttpSession session) {
			currentSession = session;
			params = new HashMap<String, String>();
			attrs = new HashMap<String, Object>();
		}

		@Override
		public String getAuthType() {
			return "H3ll's Junit";
		}

		@Override
		public Cookie[] getCookies() {
			List<Cookie> cookies = Collections.emptyList();
			return cookies.toArray(new Cookie[cookies.size()]);
		}

		@Override
		public long getDateHeader(String s) {
			return 0;
		}

		@Override
		public String getHeader(String s) {
			return "Any-header for this Junit";
		}

		@Override
		public Enumeration getHeaders(String s) {
			return null;// func does not  required here
		}

		@Override
		public Enumeration getHeaderNames() {
			return null;// func does not  required here
		}

		@Override
		public int getIntHeader(String s) {
			return 0;// func does not  required here
		}

		@Override
		public String getMethod() {
			return "GET - for JUNIT";
		}

		@Override
		public String getPathInfo() {
			return "Junit path";
		}

		@Override
		public String getPathTranslated() {
			return "Junit path";
		}

		@Override
		public String getContextPath() {
			return "Junit context";
		}

		@Override
		public String getQueryString() {
			return ""; // does not required
		}

		@Override
		public String getRemoteUser() {
			return "Junit User";
		}

		@Override
		public boolean isUserInRole(String s) {
			return false;
		}

		@Override
		public Principal getUserPrincipal() {
			return null;
		}

		@Override
		public String getRequestedSessionId() {
			return SESSION_ID;
		}

		@Override
		public String getRequestURI() {
			return "";
		}

		@Override
		public StringBuffer getRequestURL() {
			return null;
		}

		@Override
		public String getServletPath() {
			return "";
		}

		@Override
		public HttpSession getSession(boolean b) {

			return currentSession;
		}

		@Override
		public HttpSession getSession() {
			return currentSession;
		}

		@Override
		public String changeSessionId() {
			return null;
		}

		@Override
		public boolean isRequestedSessionIdValid() {
			return true;
		}

		@Override
		public boolean isRequestedSessionIdFromCookie() {
			return false;
		}

		@Override
		public boolean isRequestedSessionIdFromURL() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public boolean isRequestedSessionIdFromUrl() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
			return false;
		}

		@Override
		public void login(String s, String s1) throws ServletException {

		}

		@Override
		public void logout() throws ServletException {

		}

		@Override
		public Collection<Part> getParts() throws IOException, ServletException {
			return null;
		}

		@Override
		public Part getPart(String s) throws IOException, ServletException {
			return null;
		}

		@Override
		public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
			return null;
		}

		@Override
		public Object getAttribute(String s) {

			return attrs.get(s);
		}

		@Override
		public Enumeration getAttributeNames() {
			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public String getCharacterEncoding() {
			return "UTF-8";
		}

		@Override
		public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
		}

		@Override
		public int getContentLength() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public long getContentLengthLong() {
			return 0;
		}

		@Override
		public String getContentType() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public String getParameter(String s) {
			return params.get(s);
		}

		@Override
		public Enumeration getParameterNames() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public String[] getParameterValues(String s) {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public Map getParameterMap() {
			return params;
		}

		@Override
		public String getProtocol() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public String getScheme() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public String getServerName() {
			return "local.junit";
		}

		@Override
		public int getServerPort() {
			return 0;
		}

		@Override
		public BufferedReader getReader() throws IOException {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public String getRemoteAddr() {
			return "Local.Junit.addr";
		}

		@Override
		public String getRemoteHost() {
			return "Local.Junit.addr";
		}

		@Override
		public void setAttribute(String s, Object o) {

			attrs.put(s, o);
		}

		@Override
		public void removeAttribute(String s) {
			attrs.remove(s);
		}

		@Override
		public Locale getLocale() {
			return Locale.ENGLISH;
		}

		@Override
		public Enumeration getLocales() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public boolean isSecure() {
			return false;
		}

		@Override
		public RequestDispatcher getRequestDispatcher(String s) {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public String getRealPath(String s) {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public int getRemotePort() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public String getLocalName() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public String getLocalAddr() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public int getLocalPort() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public ServletContext getServletContext() {
			return null;
		}

		@Override
		public AsyncContext startAsync() throws IllegalStateException {
			return null;
		}

		@Override
		public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
			return null;
		}

		@Override
		public boolean isAsyncStarted() {
			return false;
		}

		@Override
		public boolean isAsyncSupported() {
			return false;
		}

		@Override
		public AsyncContext getAsyncContext() {
			return null;
		}

		@Override
		public DispatcherType getDispatcherType() {
			return null;
		}


	}


	/**
	 * Mocked Session impl - which contains  only required implemented methods.
	 */
	public static final class MockSession implements HttpSession {

		private long crTime;
		private Map<String, Object> attributes;

		public MockSession() {
			crTime = System.currentTimeMillis();
			attributes = new ConcurrentHashMap<String, Object>();
		}

		@Override
		public long getCreationTime() {
			return crTime;
		}

		@Override
		public String getId() {
			return SESSION_ID;
		}

		@Override
		public long getLastAccessedTime() {
			return 0;
		}

		@Override
		public ServletContext getServletContext() {
			return null;
		}

		@Override
		public void setMaxInactiveInterval(int i) {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public int getMaxInactiveInterval() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public HttpSessionContext getSessionContext() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public Object getAttribute(String s) {
			return attributes.get(s);
		}

		@Override
		public Object getValue(String s) {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public Enumeration getAttributeNames() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public String[] getValueNames() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public void setAttribute(String s, Object o) {
			attributes.put(s, o);
		}

		@Override
		public void putValue(String s, Object o) {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public void removeAttribute(String s) {
			attributes.remove(s);
		}

		@Override
		public void removeValue(String s) {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public void invalidate() {

			//TODO : Implement me!
			throw new UnsupportedOperationException("Implement me please!!!");
		}

		@Override
		public boolean isNew() {
			return false;
		}
	}
}
