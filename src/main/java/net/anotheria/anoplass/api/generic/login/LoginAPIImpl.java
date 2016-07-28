package net.anotheria.anoplass.api.generic.login;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.APIInitException;
import net.anotheria.anoplass.api.AbstractAPIImpl;
import net.anotheria.anoplass.api.NoLoggedInUserException;
import net.anotheria.anoplass.api.generic.login.processors.SessionCleanupOnLogoutProcessor;
import net.anotheria.anoplass.api.generic.observation.ObservationAPI;
import net.anotheria.anoplass.api.generic.observation.ObservationSubjects;
import net.anotheria.anoplass.api.session.APISessionImpl;
import net.anotheria.util.StringUtils;

/**
 * An implementation for the login api.
 * @author lrosenberg
 *
 */
public class LoginAPIImpl extends AbstractAPIImpl implements LoginAPI{

	/**
	 * Login preprocessors. Each of them get called before each login. Can cancel a login.
	 */
	private List<LoginPreProcessor>  loginPreProcessors;
	/**
	 * Login postprocessors.
	 */
	private List<LoginPostProcessor> loginPostProcessors;
	/**
	 * Logout preprocessors.
	 */
	private List<LogoutPreProcessor>  logoutPreProcessors;
	/**
	 * Logout postprocessors.
	 */
	private List<LogoutPostProcessor> logoutPostProcessors;
	/**
	 * Link to the ObservationAPI. Used to announce changes in user logged in state.
	 */
	private ObservationAPI observationAPI;

	@Override public void init() throws APIInitException {
		super.init();

		loginPreProcessors = new CopyOnWriteArrayList<>();
		loginPostProcessors = new CopyOnWriteArrayList<>();

		logoutPreProcessors = new CopyOnWriteArrayList<>();
		logoutPostProcessors = new CopyOnWriteArrayList<>();

		addLogoutPostprocessor(new SessionCleanupOnLogoutProcessor());
		observationAPI = APIFinder.findAPI(ObservationAPI.class);

	}

	/**
	 * Adds a login postprocessor. Threadsafe.
	 */
	@Override public void addLoginPostprocessor(LoginPostProcessor postProcessor) {
		loginPostProcessors.add(postProcessor);

	}

	/**
	 * Adds a login preprocessor. Threadsafe.
	 */
	@Override public void addLoginPreprocessor(LoginPreProcessor preProcessor) {
		loginPreProcessors.add(preProcessor);

	}

	@Override public void logInUser(String userId) throws APIException {
		logInUser(userId, false);
	}

	@Override public void stealthLogInUser(String userId) throws APIException {
		logInUser(userId, true);
	}

	private void logInUser(String userId, boolean stealth) throws APIException {
		if (!stealth)
			callLoginPreprocessors(userId);

		((APISessionImpl)getSession()).setCurrentUserId(userId);
		getCallContext().setCurrentUserId(userId);

		if (!stealth)
			callLoginPostprocessors(userId);

		if (!stealth)
			observationAPI.fireSubjectUpdateForCurrentUser(ObservationSubjects.LOGIN, this.getClass().getName());
	}

	@Override public void logoutMe() throws APIException {
		try{
			String userId = getCallContext().getCurrentUserId();
			callLogoutPreprocessors(userId);


			((APISessionImpl)getSession()).setCurrentUserId(null);
			getCallContext().setCurrentUserId(null);

			callLogoutPostprocessors(userId);

			observationAPI.fireSubjectUpdateForCurrentUser(ObservationSubjects.LOGOUT, this.getClass().getName());

            //firing additional event with user id... case after  login we won't ever find out who was logged out.
            if (!StringUtils.isEmpty(userId))
                observationAPI.fireSubjectUpdateForUser(ObservationSubjects.LOGOUT, this.getClass().getName(), userId);
        }catch(NoLoggedInUserException ignored){
			log.trace("user not logged in",ignored);
		}
	}

	@Override public String getLogedUserId() throws NoLoggedInUserException {
		if(!isLogedIn())
			throw new NoLoggedInUserException("No loged in users!");
		return getCallContext().getCurrentUserId();
	}

	@Override public boolean isLogedIn() {
		//try{
			return !StringUtils.isEmpty(getCallContext().getCurrentUserId());
		/*}catch(APIException e){
			return false;
		}*/
	}

	@Override public void addLogoutPostprocessor(LogoutPostProcessor postProcessor) {
		logoutPostProcessors.add(postProcessor);
	}

	@Override public void addLogoutPreprocessor(LogoutPreProcessor preProcessor) {
		logoutPreProcessors.add(preProcessor);

	}

	//////////
	/**
	 * Calls all login preprocessors.
	 * @param userId user id
	 * @throws APIException on errors
	 */
	private void callLoginPreprocessors(String userId) throws APIException{
		for (LoginPreProcessor p : loginPreProcessors){
			try{
				p.preProcessLogin(userId);
			}catch(ProcessorException e){
				throw e;
			}catch(Exception e){
				log.error("Exception in loginpreprocessor: "+p,e);
			}
		}
	}

	/**
	 * Calls all login postprocessors.
	 * @param userId
	 */
	private void callLoginPostprocessors(String userId) {
		for (LoginPostProcessor p : loginPostProcessors){
			try{
				p.postProcessLogin(userId);
			}catch(Exception e){
				log.error("Exception in loginpostprocessor: "+p,e);
			}
		}
	}

	/**
	 * Calls all logout preprocessors.
	 * @param userId user id
	 * @throws APIException on errors
	 */
	private void callLogoutPreprocessors(String userId) throws APIException{
		for (LogoutPreProcessor p : logoutPreProcessors){
			try{
				p.preProcessLogout(userId);
			}catch(ProcessorException e){
				throw e;
			}catch(Exception e){
				log.error("Exception in logoutPreProcessor: "+p,e);
			}
		}
	}

	/**
	 * Calls all logout postprocessors.
	 * @param userId user id
	 */
	private void callLogoutPostprocessors(String userId) {
		for (LogoutPostProcessor p : logoutPostProcessors){
			try{
				p.postProcessLogout(userId);
			}catch(Exception e){
				log.error("Exception in logoutpostprocessor: "+p,e);
			}
		}

	}




}
