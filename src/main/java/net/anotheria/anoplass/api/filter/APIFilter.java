package net.anotheria.anoplass.api.filter;

import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.activity.ActivityAPI;
import net.anotheria.anoplass.api.session.APISession;
import net.anotheria.anoplass.api.session.APISessionCreationException;
import net.anotheria.anoplass.api.session.APISessionDistributionConfig;
import net.anotheria.anoplass.api.session.APISessionManager;
import net.anotheria.util.StringUtils;
import net.anotheria.util.concurrency.IdBasedLock;
import net.anotheria.util.concurrency.IdBasedLockManager;
import net.anotheria.util.concurrency.SafeIdBasedLockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;


/**
 * The filter which performs bounding of a user request and session to the previously created or new APISession and APICallContext.
 *
 * @author lrosenberg
 * @version $Id: $Id
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
	 * Cloudflare ip country header constant.
	 * This header is added to requests by enabling Cloudflare IP Geolocation in the dashboard.
	 */
	private static final String CF_IP_COUNTRY_HEADER_CONSTANT = "CF-IPCountry";
	/**
	 * Cloudflare connecting ip header constant.
	 * This header will only be sent on the traffic from Cloudflare's edge to your origin webserver.
	 */
	private static final String CF_CONNECTING_IP_HEADER_CONSTANT = "CF-Connecting-IP";

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
	private static final Logger LOG = LoggerFactory.getLogger(APIFilter.class);
	/**
	 * Id based lock manager instance.
	 */
	private IdBasedLockManager<String> lockManager;

	/**
	 * APISessionDistributionConfig instance.
	 */
	private APISessionDistributionConfig configuration;


	/** {@inheritDoc} */
	@Override
	public void destroy() {

	}

	/** {@inheritDoc} */
	@Override
	public void doFilter(ServletRequest sReq, ServletResponse sRes, FilterChain chain) throws IOException, ServletException {

		if (!(sReq instanceof HttpServletRequest))
			return;

		HttpServletRequest req = (HttpServletRequest) sReq;

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
		chain.doFilter(sReq, sRes);
	}


	/**
	 * Add or update cookie wit distributed session id, only if Distribution is enabled.
	 *
	 * @param res     {@link HttpServletResponse}
	 * @param req     {@link HttpServletRequest}
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
	 * Add sessionId Cookie to response.
	 *
	 * @param sres      {@link HttpServletResponse}
	 * @param sessionId session id
	 */
	private void addSessionIdCookieToResponse(HttpServletResponse sres, String sessionId) {
		Cookie distributedSessionCookie = new Cookie(configuration.getSessionIdCookieName(), sessionId);
		distributedSessionCookie.setPath("/");
		distributedSessionCookie.setMaxAge(-1);
		if (!StringUtils.isEmpty(configuration.getSessionIdCookieDomain()))
			distributedSessionCookie.setDomain(configuration.getSessionIdCookieDomain());
		sres.addCookie(distributedSessionCookie);
	}


	/** {@inheritDoc} */
	@Override
	public void init(FilterConfig config) throws ServletException {
		activityAPI = APIFinder.findAPI(ActivityAPI.class);
		configuration = APISessionDistributionConfig.getInstance();
		lockManager = new SafeIdBasedLockManager<String>();
	}

	/**
	 * Initializes the APISession.
	 * Simply obtain using {@link net.anotheria.anoplass.api.session.APISessionManager} instance.
	 *
	 * @param req {@link jakarta.servlet.http.HttpServletRequest}}
	 * @return {@link net.anotheria.anoplass.api.session.APISession}
	 * @throws jakarta.servlet.ServletException on errors
	 */
	protected APISession initSession(HttpServletRequest req) throws ServletException {

		final HttpSession session = req.getSession(true);
		if (session == null)
			throw new ServletException("Could not obtain HttpSession!");

		IdBasedLock<String> lock = lockManager.obtainLock(session.getId());
		lock.lock();
		try {
			// preparing required data for call
			Object apiSessionIdObject = session.getAttribute(API_SESSION_ID_HTTP_SESSION_ATTRIBUTE_NAME);
			String apiSessionId = String.class.isInstance(apiSessionIdObject) ? String.class.cast(apiSessionIdObject) : null;
			final String dSessionIdFromCookies = getDistributedSessionIdFromCookies(req);
			final String dSessionIdFromRequest = req.getParameter(configuration.getDistributedSessionParameterName());
			final String userAgent = req.getHeader(USER_AGENT_HEADER_CONSTANT);
			final String editorId = (session.getAttribute(CURRENT_USER_ID) instanceof String) ? String.class.cast(session.getAttribute(CURRENT_USER_ID)) : null;
			final String cfIpCountry = req.getHeader(CF_IP_COUNTRY_HEADER_CONSTANT);
			final String cfConnectingIp = req.getHeader(CF_CONNECTING_IP_HEADER_CONSTANT);
			//debug info
			if (LOG.isDebugEnabled()) {
				LOG.debug("apiSessionId : " + apiSessionId);
				LOG.debug("dSessionIdFromCookies : " + dSessionIdFromCookies);
				LOG.debug("dSessionIdFromRequest : " + dSessionIdFromRequest);
				LOG.debug("userAgent : " + userAgent);
				LOG.debug("editorId : " + editorId);
				LOG.debug("cfIpCountry : " + cfIpCountry);
				LOG.debug("cfConnectingIp : " + cfConnectingIp);
			}

			//calling obtain session
			String remoteIpAddress = !StringUtils.isEmpty(cfConnectingIp) ? cfConnectingIp : req.getRemoteAddr();
			APISession apiSession = APISessionManager.getInstance().obtainSession(session.getId(), apiSessionId, dSessionIdFromCookies,
					dSessionIdFromRequest, remoteIpAddress, userAgent, req.getLocale(), editorId);
			if (!StringUtils.isEmpty(cfIpCountry)){
				apiSession.setAttribute(CF_IP_COUNTRY_HEADER_CONSTANT, cfIpCountry);
			}
			if (!StringUtils.isEmpty(cfConnectingIp)){
				apiSession.setAttribute(CF_CONNECTING_IP_HEADER_CONSTANT, cfConnectingIp);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("APISession - successfully obtained!");
			}

			session.setAttribute(API_SESSION_ID_HTTP_SESSION_ATTRIBUTE_NAME, apiSession.getId());
			return apiSession;
		} catch (APISessionCreationException e) {
			LOG.error("obtainSession(" + session + ")", e);
			throw new ServletException("APISession obtain failed! " + e.getMessage(), e);
		} finally {
			lock.unlock();
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

}
