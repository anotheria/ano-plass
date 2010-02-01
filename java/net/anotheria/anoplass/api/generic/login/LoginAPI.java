package net.anotheria.anoplass.api.generic.login;

import net.anotheria.anoplass.api.API;
import net.anotheria.anoplass.api.APIException;

/**
 * Basic API for login/out purposes.
 * @author lrosenberg
 *
 */
public interface LoginAPI extends API{
	/**
	 * Logs the user with the given id in.
	 * @param userId the userId to log in.
	 * @throws APIException
	 */
	void logInUser(String userId) throws APIException;
	/**
	 * Logouts the current user.
	 * @throws APIException if error occurs
	 */
	void logoutMe() throws APIException;
	
	/**
	 * Returns the id of the currently logged in user.
	 * @return string id
	 * @throws APIException if no logged in user in current session, or error occurs
	 */
	String getLogedUserId() throws  APIException;

	/**
	 * Returns true if there is a current userid.
	 * @return boolean value
	 */
	boolean isLogedIn();

	/**
	 * Adds a login preprocessor. Each login preprocessor is called before a user can actually login. LoginPreprocessor can prevent login by throwing an exception.
	 * @param preProcessor
	 */
	void addLoginPreprocessor(LoginPreProcessor preProcessor);
	/**
	 * Adds a login post processor. Each login postprocessor is called _after_ a user logins. LoginPostProcessor can't prevent login.
	 * @param postProcessor
	 */
	void addLoginPostprocessor(LoginPostProcessor postProcessor);
	/**
	 * Adds a logout preprocessor.
	 * @param preProcessor
	 */
	void addLogoutPreprocessor(LogoutPreProcessor preProcessor);
	/**
	 * Adds a logout postprocessor.
	 * @param preProcessor
	 */
	void addLogoutPostprocessor(LogoutPostProcessor preProcessor);
}
