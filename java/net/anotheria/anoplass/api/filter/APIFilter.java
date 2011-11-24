package net.anotheria.anoplass.api.filter;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoplass.api.APIConfig;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.activity.ActivityAPI;
import net.anotheria.anoplass.api.session.APISession;
import net.anotheria.anoplass.api.session.APISessionCreationException;
import net.anotheria.anoplass.api.session.APISessionDistributionConfig;
import net.anotheria.anoplass.api.session.APISessionManager;
import net.anotheria.anoplass.api.session.APISessionRestoreException;
import net.anotheria.util.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * The filter which performs bounding of a user request and session to the previously created or new APISession and APICallContext.
 *
 * @author lrosenberg
 */
public class APIFilter implements Filter {


	/**
	 * Copy session parameter.
	 */
	@Deprecated
	public static final String PARAM_COPY_SESSION = "srcsession";

	/**
	 * API session id constant.
	 */
	public static final String API_SESSION_ID_HTTP_SESSION_ATTRIBUTE_NAME = "API_SESSION_ID";
	/**
	 * API session constant.
	 */
	public static final String API_SESSION_HTTP_SESSION_ATTRIBUTE_NAME = "API_SESSION";
	/**
	 * User - Agent header constant.
	 */
	private static final String USER_AGENT_HEADER_CONSTANT = "user-agent";

	/**
	 * The activity api, which is notified about all actions by the user.
	 */
	private ActivityAPI activityAPI;
	/**
	 * Current user id constant.
	 */
	private static final String CURRENT_USER_ID = "currentUserId";
	/**
	 * Lo4j logger.
	 */
	private static final Logger LOG = Logger.getLogger(APIFilter.class);

	/**
	 * APISessionDistributionConfig instance.
	 */
	private APISessionDistributionConfig configuration;


	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest sReq, ServletResponse sRes, FilterChain chain) throws IOException, ServletException {

		if (!(sReq instanceof HttpServletRequest))
			return;

		HttpServletRequest req = (HttpServletRequest) sReq;

		String copySessionParam = req.getParameter(PARAM_COPY_SESSION);
		if (copySessionParam != null && copySessionParam.length() > 0)
			copySession(req, copySessionParam);

		//Checking if sessionId present in URL! If so - trying to init such session
		//If distribution is disabled - method will be ignored....
		String distributedSessionId = req.getParameter(configuration.getDistributedSessionParameterName());
		if (!StringUtils.isEmpty(distributedSessionId))
			restoreDistributedSession(distributedSessionId, req);

		//init process  for APISession!!!
		APISession session = initSession(req);

		String url = req.getRequestURL().toString();
		String qs = req.getQueryString();
		if (!StringUtils.isEmpty(qs))
			url += qs;

		activityAPI.notifyMyActivity(url);

		if (!(sRes instanceof HttpServletResponse))
			return;


		saveCookie(HttpServletResponse.class.cast(sRes), req, session);
		try{
			chain.doFilter(sReq, sRes);
		}finally{
			APICallContext.remove();
		}
	}


	/**
	 * Add or update cookie wit distributed session id, only if Distribution is enabled.
	 *
	 * @param res	 {@link HttpServletRequest
	 * @param req	 {@link HttpServletResponse}
	 * @param session {@link APISession}
	 */
	private void saveCookie(HttpServletResponse res, HttpServletRequest req, APISession session) {
		if (!configuration.isDistributionEnabled())
			return;

		//manage Cookie with SessionId
		Cookie distributedSessionCookie = getDistributedSessionIdCookie(req);
		if (distributedSessionCookie == null) {
			addSessionIdCookieToResponse(res, session.getId());
			return;
		}
		//  update cookie if sessionId was change! etc.
		if (!session.getId().equals(distributedSessionCookie.getValue())) {
			//remove old  cookie with invalid sessionId
			distributedSessionCookie.setMaxAge(0);
			addSessionIdCookieToResponse(res, session.getId());
		}
	}


	/**
	 * Method restore distributed session, using URL parameter under which sessionId comes.
	 * This method is  actual in cases when cookie is unreachable (different domains, etc). It has higher priority
	 * that restoring by cookie. Etc.
	 *
	 * @param distributedSessionId id of distributed session
	 * @param req				  {@link HttpServletRequest}
	 */
	private void restoreDistributedSession(String distributedSessionId, HttpServletRequest req) {
		//skipping  if Distribution is disabled!!!
		if (!configuration.isDistributionEnabled())
			return;

		HttpSession httpSession = req.getSession(true);

		//check first whether we already have restored this session in the past.
		String apiSessionId = String.class.cast(httpSession.getAttribute(API_SESSION_ID_HTTP_SESSION_ATTRIBUTE_NAME));
		if (!StringUtils.isEmpty(apiSessionId) && apiSessionId.equals(distributedSessionId)) {
			LOG.debug("Session was already restored, skipping.");
			return;
		}

		if (restoreSession(distributedSessionId, httpSession) != null) {
			LOG.debug("APISession successfully restored APISession[" + distributedSessionId + "]");
		}

	}

	/**
	 * Add sessionId Cookie to response.
	 *
	 * @param sres	  {@link HttpServletResponse}
	 * @param sessionId session id
	 */
	private void addSessionIdCookieToResponse(HttpServletResponse sres, String sessionId) {
		Cookie distributedSessionCookie = new Cookie(configuration.getSessionIdCookieName(), sessionId);
		distributedSessionCookie.setPath("/");
		distributedSessionCookie.setMaxAge(-1);
		if (configuration.getSessionIdCookieDomain()!=null){
			distributedSessionCookie.setDomain(configuration.getSessionIdCookieDomain());
		}
		sres.addCookie(distributedSessionCookie);
	}

	/**
	 * Create session copy.
	 *
	 * @param req				  {@link HttpServletRequest}
	 * @param copySessionParameter source session id
	 * @throws ServletException on remote session create error
	 */
	private void copySession(HttpServletRequest req, String copySessionParameter) throws ServletException {
		HttpSession session = req.getSession(true);
		APISession apiSession;
		try {
			apiSession = APISessionManager.getInstance().createSessionCopy(copySessionParameter, session.getId());
		} catch (APISessionCreationException e) {
			LOG.error("copySession(" + req + ", " + copySessionParameter + ")", e);
			throw new ServletException("Creating remoteSession failed." + e.getMessage(), e);
		}
		session.setAttribute(API_SESSION_ID_HTTP_SESSION_ATTRIBUTE_NAME, apiSession.getId());
		if (APIConfig.associateSessions())
			session.setAttribute(API_SESSION_HTTP_SESSION_ATTRIBUTE_NAME, apiSession);
	}


	@Override
	public void init(FilterConfig config) throws ServletException {
		activityAPI = APIFinder.findAPI(ActivityAPI.class);
		configuration = APISessionDistributionConfig.getInstance();
	}

	/**
	 * Initializes the APISession.
	 * There are few options available.
	 * - HttpSession contains APISession id - if APISession found in SessionManager - simply populating data and that's all, otherwise
	 * checking for  API_SESSION_HTTP_SESSION_ATTRIBUTE_NAME parameter existence ( if APIConfig.associateSessions() enabled ), if session are paired - simply populating session,
	 * and last case -  trying to restore session, if restore failed or  distribution is disabled -  we creating new APISession.
	 * - HttpSession not contains APISession id -
	 * trying to restore distributed session (if distribution is enabled and cookie with sessionId exists), if restore failed for some reason,
	 * create new session.
	 *
	 * @param req {@link javax.servlet.http.HttpServletRequest}}
	 * @return {@link APISession}
	 * @throws javax.servlet.ServletException on errors
	 */
	protected APISession initSession(HttpServletRequest req) throws ServletException {
		APICallContext.getCallContext().reset();

		//ok, wir erstellen erstmal per request ne neue session, spaeter optimieren (ein problem z.b. fuer lb abfragen).
		//durch das "unroot" sollte es eben nicht mehr so sein, dass pro request "unnnoeitg eine session" erzeugt wird.
		HttpSession session = req.getSession(true);
		String apiSessionId = session == null ? null : (String) session.getAttribute(API_SESSION_ID_HTTP_SESSION_ATTRIBUTE_NAME);

		APISession apiSession;
		if (apiSessionId == null) {
			//check if cookies exist
			String distributedSessionId = getDistributedSessionIdFromCookies(req);
			//try to restore distributed session - if possible. if not - create new APISession
			apiSession = !StringUtils.isEmpty(distributedSessionId) ? restoreSession(distributedSessionId, session) : createAPISession(session);

			//create new session - if  distributed one not found or distribution is disabled
			apiSession = apiSession == null ? createAPISession(session) : apiSession;

			populatePropertiesToAPISession(apiSession, req, session);
			return apiSession;
		}

		apiSession = APISessionManager.getInstance().getSession(apiSessionId);
		//session found case
		if (apiSession != null) {
			populatePropertiesToAPISession(apiSession, req, session);
			return apiSession;
		}

		//session not found case
		APISession apiSessionFromHttpSession = (APISession) session.getAttribute(API_SESSION_HTTP_SESSION_ATTRIBUTE_NAME);
		if (apiSessionFromHttpSession != null)
			apiSession = apiSessionFromHttpSession;
		else {
			//check if cookies exist
			String distributedSessionId = getDistributedSessionIdFromCookies(req);
			//try to restore distributed session - if possible. if not - create new APISession
			apiSession = !StringUtils.isEmpty(distributedSessionId) ? restoreSession(distributedSessionId, session) : createAPISession(session);

			//create new session - if  distributed one not found or distribution is disabled
			apiSession = apiSession == null ? createAPISession(session) : apiSession;
		}
		populatePropertiesToAPISession(apiSession, req, session);
		return apiSession;

	}

	/**
	 * Populates properties to APISession and CallContext.
	 *
	 * @param apiSession {@link APISession}
	 * @param req		{@link HttpServletRequest}
	 * @param session	{@link HttpSession}
	 */
	private void populatePropertiesToAPISession(APISession apiSession, HttpServletRequest req, HttpSession session) {
		APICallContext currentContext = APICallContext.getCallContext();

		apiSession.setIpAddress(req.getRemoteAddr());
		apiSession.setUserAgent(req.getHeader(USER_AGENT_HEADER_CONSTANT));
		currentContext.setCurrentSession(apiSession);

		if (apiSession.getLocale() == null) {
			currentContext.setCurrentLocale(req.getLocale());
		} else {
			currentContext.setCurrentLocale(apiSession.getLocale());
		}
		currentContext.setCurrentUserId(apiSession.getCurrentUserId());

		//setting proper editor id - if so.
		Object editorId = session != null && session.getAttribute(CURRENT_USER_ID) != null ? String.class.cast(session.getAttribute(CURRENT_USER_ID)) : null;
		if (editorId != null && editorId instanceof String && !(String.class.cast(editorId).isEmpty())) {
			currentContext.setCurrentEditorId(String.class.cast(editorId));
		}
	}

	/**
	 * Restores a previously distributed session.
	 * If no session found null will be returned.
	 *
	 * @param distributedSessionId id of distributed api session
	 * @param session			  {@link HttpSession}
	 * @return {@link APISession}
	 */
	private APISession restoreSession(String distributedSessionId, HttpSession session) {
		try {
			APISession apiSession = APISessionManager.getInstance().restoreSession(distributedSessionId, session.getId());
			session.setAttribute(API_SESSION_ID_HTTP_SESSION_ATTRIBUTE_NAME, apiSession.getId());
			if (APIConfig.associateSessions())
				session.setAttribute(API_SESSION_HTTP_SESSION_ATTRIBUTE_NAME, apiSession);
			return apiSession;
		} catch (APISessionRestoreException e) {
			LOG.warn("restoreSession(" + distributedSessionId + "," + session + ")", e);
			return null;
		}

	}


	/**
	 * Search for sessionId in cookies.
	 *
	 * @param req {@link HttpServletRequest}
	 * @return null - if distribution is disabled of cookie not preset,
	 *         session id value if cookie present and it value is not null
	 */
	private String getDistributedSessionIdFromCookies(HttpServletRequest req) {
		Cookie sessionIdCookie = getDistributedSessionIdCookie(req);
		return sessionIdCookie != null ? sessionIdCookie.getValue() : null;
	}

	/**
	 * Return Session id cookie if  such exists and distribution is enabled.
	 *
	 * @param req {@link HttpServletRequest}
	 * @return {@link Cookie}
	 */
	private Cookie getDistributedSessionIdCookie(HttpServletRequest req) {
		if (!configuration.isDistributionEnabled())
			return null;
		Cookie[] cookies = req.getCookies();
		if (cookies == null || cookies.length == 0)
			return null;
		Cookie sessionIdCookie = null;
		for (Cookie curr : cookies)
			if (curr.getName().equals(configuration.getSessionIdCookieName()))
				sessionIdCookie = curr;
		return sessionIdCookie;
	}


	/**
	 * Creates a new APISession.
	 *
	 * @param session {@link HttpSession}
	 * @return {@link APISession}
	 * @throws javax.servlet.ServletException on distributed session create failure
	 */
	private APISession createAPISession(HttpSession session) throws ServletException {
		try {
			APISession apiSession = APISessionManager.getInstance().createSession(session.getId());
			session.setAttribute(API_SESSION_ID_HTTP_SESSION_ATTRIBUTE_NAME, apiSession.getId());
			if (APIConfig.associateSessions())
				session.setAttribute(API_SESSION_HTTP_SESSION_ATTRIBUTE_NAME, apiSession);
			return apiSession;
		} catch (APISessionCreationException e) {
			LOG.error("createAPISession(" + session + ")", e);
			throw new ServletException("Creating remoteSession failed." + e.getMessage(), e);
		}

	}

}
