package net.anotheria.anoplass.api.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.anotheria.anoplass.api.activity.ActivityAPI;
import net.anotheria.anoplass.api.common.APICallContext;
import net.anotheria.anoplass.api.common.APIException;
import net.anotheria.anoplass.api.common.APIFinder;
import net.anotheria.anoplass.api.session.APISession;
import net.anotheria.anoplass.api.session.APISessionDistributionException;
import net.anotheria.anoplass.api.session.APISessionDistributionHelper;
import net.anotheria.anoplass.api.session.APISessionManager;

import org.apache.log4j.Logger;


/**
 * The filter which performs bounding of a user request and session to the previously created or new APISession and APICallContext.
 * @author lrosenberg
 *
 */
public class APIFilter implements Filter{

	@Deprecated
	public static final String PARAM_COPY_SESSION = "srcsession";
	
	public static final String PARAM_DISTRIBUTED_SESSION_NAME = "asDiSeName";
	public static final String HTTPSA_SESSION_ID = "API_SESSION_ID";
	
	/**
	 * The activity api, which is notified about all actions by the user.
	 */
	private ActivityAPI activityAPI;
    private static final String CURRENT_USER_ID = "currentUserId";
    
    private static Logger log = Logger.getLogger(APIFilter.class);

    @Override public void destroy() {
		
	}

	@Override public void doFilter(ServletRequest sreq, ServletResponse sres,
			FilterChain chain) throws IOException, ServletException {

		if (!(sreq instanceof HttpServletRequest))
			return; 
		
		HttpServletRequest req = (HttpServletRequest) sreq;
		
		
		String copySessionParam = req.getParameter(PARAM_COPY_SESSION); 
		if (copySessionParam!=null && copySessionParam.length()>0)
			copySession(req, copySessionParam);
		
		String restoreSessionParameter = req.getParameter(PARAM_DISTRIBUTED_SESSION_NAME);
		if (restoreSessionParameter!=null && restoreSessionParameter.length()>0)
			restoreSession(req, restoreSessionParameter);
		
		@SuppressWarnings("unused")
		APISession session = initSession(req);
		
		String url = req.getRequestURL().toString();
		String qs = req.getQueryString();
		if (qs!=null && qs.length()>0)
			url += qs;
		
		activityAPI.notifyMyActivity(url);
		
		chain.doFilter(sreq, sres);
		
	}
	
	private void copySession(HttpServletRequest req, String copySessionParameter){
		HttpSession session = req.getSession(true);
		APISession apiSession = APISessionManager.getInstance().createSessionCopy(copySessionParameter, session.getId());
		session.setAttribute(HTTPSA_SESSION_ID, apiSession.getId());
		
	}

	private void restoreSession(HttpServletRequest req, String distributedSessionName) throws ServletException{
		HttpSession session = req.getSession(true);

		//check first whether we already have restored this session in the past.
		String oldApiSessionId = (String)session.getAttribute(HTTPSA_SESSION_ID);
		if (oldApiSessionId!=null){
			APISession oldSession = APISessionManager.getInstance().getSession(oldApiSessionId);
			if (oldSession!=null){
				String oldDistributedSessionName = (String)oldSession.getAttribute(PARAM_DISTRIBUTED_SESSION_NAME);
				if (oldDistributedSessionName!=null && oldDistributedSessionName.equals(distributedSessionName)){
					log.debug("Session was already restored, skipping.");
					return;
				}
			}
		}
		
		
		APISession apiSession = createAPISession(session);
		try{
			APISessionDistributionHelper.restoreSession(distributedSessionName, apiSession);
		}catch(APISessionDistributionException e){
			log.error("restoreSession("+req+", "+distributedSessionName+")", e);
			throw new ServletException("Restoring remote session failed: "+e.getMessage(), e);
		}
		apiSession.setAttribute(PARAM_DISTRIBUTED_SESSION_NAME, distributedSessionName);
		
	}

	@Override public void init(FilterConfig config) throws ServletException {
		try {
			activityAPI = APIFinder.findAPI(ActivityAPI.class);
		} catch (APIException e) {
			log.error("ActivityAPI - init failure init(" + config + ")", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Initializes the APISession.
	 * @param req
	 * @return
	 */
	protected APISession initSession(HttpServletRequest req) {		

		
		APICallContext currentContext = APICallContext.getCallContext();
		currentContext.reset();
		
		
		//ok, wir erstellen erstmal per request ne neue session, spaeter optimieren (ein problem z.b. fuer lb abfragen).
		//durch das "unroot" sollte es eben nicht mehr so sein, dass pro request "unnnoeitg eine session" erzeugt wird.
		HttpSession session = req.getSession(true);
		String apiSessionId = session == null ? null : (String) session.getAttribute(HTTPSA_SESSION_ID);
		APISession apiSession;
		if (apiSessionId==null){
			apiSession = createAPISession(session);
		}else{
			apiSession = APISessionManager.getInstance().getSession(apiSessionId);
			if(apiSession == null) {
				apiSession = createAPISession(session);
			} 
		}
		

		apiSession.setIpAddress(req.getRemoteAddr());
		apiSession.setUserAgent(req.getHeader("user-agent"));
		
		
		currentContext.setCurrentSession(apiSession);
		
		if (apiSession.getLocale()==null){
			currentContext.setCurrentLocale(req.getLocale());
		}else{
			currentContext.setCurrentLocale(apiSession.getLocale());
		}
		currentContext.setCurrentUserId(apiSession.getCurrentUserId());
        //setting proper editor id - if so.
        setEditorId(currentContext,session);
		return apiSession;
	}

    /**
     * Checking HttpSession for currentUserId-  which is currently ---> editorId
     * @param currentContext APICallContext instance
     * @param session Http sesion itself
     */
    private void setEditorId(APICallContext currentContext, HttpSession session) {
        Object editorId = session!=null&&session.getAttribute(CURRENT_USER_ID)!=null? (String) session.getAttribute(CURRENT_USER_ID) :null;
        if(editorId!=null && editorId instanceof String && !((String)editorId).isEmpty()){
            currentContext.setCurrentEditorId((String) editorId);
        }
    }

    /**
	 * Creates a new APISession.
	 * @param session
	 * @return
	 */
	private APISession createAPISession(HttpSession session) {
		APISession apiSession = APISessionManager.getInstance().createSession(session.getId());
		session.setAttribute(HTTPSA_SESSION_ID, apiSession.getId());
		return apiSession;
	}
	
}
