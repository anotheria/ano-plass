package net.anotheria.anoplass.api;

import net.anotheria.anoplass.api.session.APISession;
import net.anotheria.anoplass.api.session.PolicyHelper;
import net.anotheria.anoplass.api.validation.ValidationError;
import net.anotheria.anoplass.api.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;


/**
 * A base class for API implementation which provides basic functionality. One of the things this class is providing are the so-called 'private' attributes.
 * If an attribute is set via setAttribute method in the AbstractAPI instead of directly getCurrentSession().setAttribute() the name of the attribute is prefixed 
 * by the name of the API Implementation class, which allows multiple apis to store attributes under the same name without disturbing each other's values. 
 * If an API AAPIImpl and BAPIImpl both stores an attribute under "foo" it is stored internally under "x.y.z.AAPIImpl.foo" and "x.y.v.BAPIImpl.foo", therefore
 * allowing the API Implementations to use sound names for attributes without carying for exclusivity.  
 * @author lrosenberg
 *
 */
public abstract class AbstractAPIImpl implements API{
	/**
	 * A protected log instance is created in the constructor.
	 */
	protected Logger log;
	/**
	 * The attribute prefix which is used on all attributes put via setAttributeInSession... methods to prevent name collision.
	 */
	private final String ATTRIBUTE_PREFIX = getClass().getName()+".";
	/**
	 * Instance of the config.
	 */
	private static APIConfig apiConfig;

	/**
	 * Constant for MINUTE in milliseconds.
	 */
	public static final long MINUTE = 1000L*60;
	/**
	 * Constant for HOUR in milliseconds.
	 */
	public static final long HOUR = MINUTE*60;
	/**
	 * Constant for DAY in milliseconds.
	 */
	public static final long DAY = HOUR*24;

	/**
	 * Protected constructor.
	 */
	protected AbstractAPIImpl(){
		log = LoggerFactory.getLogger(this.getClass());
		apiConfig = new APIConfig();
	}


	@Override public void deInit() {
	}

	
	@Override public void init() throws APIInitException {
	}

	/**
	 * Sets a private attribute in session.
	 * @param name the name of the attribute.
	 * @param attribute the value of the attribute.
	 */
	protected void setAttributeInSession(String name, Object attribute){
		getSession().setAttribute(getPrivateAttributeName(name), attribute);
	}
	
	/**
	 * Sets attribute in session honouring the specified policy(ies).
	 * @param name the name of the attribute.
	 * @param policy the policy of the attribute. Multiple policies can be ORed.
	 * @param attribute the value of the attribute.
	 */
	protected void setAttributeInSession(String name, int policy, Object attribute){
		if (PolicyHelper.isAutoExpiring(policy))
			setAttributeInSession(name, policy, attribute, System.currentTimeMillis()+getExpirePeriodForAttribute(name));
		else
			getSession().setAttribute(getPrivateAttributeName(name), policy, attribute);
	}

	/**
	 * Set attribute in session, with the  policy and expiration time.
	 * 
	 * @param name attribute name
	 * @param policy actually policy itself
	 * @param attribute value
	 * @param expiresWhen expire
	 */
	protected void setAttributeInSession(String name, int policy, Object attribute, long expiresWhen){
		getSession().setAttribute(getPrivateAttributeName(name), policy, attribute, expiresWhen);
	}

	/**
	 * Set attribute in session, with expiration time.
	 *
	 * @param name attribute name
	 * @param attribute value
	 * @param expiresWhen expire
	 */
	protected void setAttributeInSession(String name, Object attribute, long expiresWhen){
		setAttributeInSession(name, APISession.POLICY_AUTOEXPIRE, attribute, expiresWhen);
	}

	/**
	 * Returns attribute value form session.
	 * @param name attribute name
	 * @return object
	 */
	protected Object getAttributeFromSession(String name){
		return getSession().getAttribute(getPrivateAttributeName(name));
	}
	
	/**
	 * Removes a private attribute from session.
	 * @param key attribute name
	 */
	public void removeAttributeFromSession(String key) {
		getSession().removeAttribute(getPrivateAttributeName(key));
	}
	
	/**
	 * Returns the prefix for all internally stored attributes.
	 * @return String prefix
	 */
	private String getSessionAttributePrefix(){
		return ATTRIBUTE_PREFIX;
	}
	
	/**
	 * Returns a 'private' attribute name which allows to reduce the visibility of the attribute.
	 * @param name name parameter
	 * @return attribute name
	 */
	protected String getPrivateAttributeName(String name){
		return new StringBuilder(getSessionAttributePrefix()).append(name).toString();
	}
	
	/**
	 * Returns the current CallContext. Convenience method.
	 * @return API Call Context
	 */
	protected APICallContext getCallContext(){
		return APICallContext.getCallContext();
	}
	
	/**
	 * Returns the current session from the CallContext. Convenience method.
	 * @return API session
	 */
	protected APISession getSession(){
		return getCallContext().getCurrentSession();
	}
	
	/**
	 * Returns the current locale from the CallContext. Convenience method.
	 * @return current Locale
	 */
	protected Locale getCurrentLocale(){
		return getCallContext().getCurrentLocale();
	}
	
	/**
	 * Returns the current userId from the CallContext. Convenience method.
	 * @return current user id
	 */
	protected String getCurrentUserId() {
		return getCallContext().getCurrentUserId();
	}
	/**
	 * Returns the current userId from the CallContext. The difference between this method and the getCurrentUserId method, is that this method will throw an Exception if 
	 * no user is logged in. This allows api methods to rely on this check and not to check their-self anymore.
	 * @return userId as string
	 * @throws NoLoggedInUserException when user is not logged in
	 */
	protected String getLoggedInUserId() throws NoLoggedInUserException{
		String userId = getCurrentUserId();
		if (userId==null || userId.length()==0){
			throw new NoLoggedInUserException("No logged in user.");
		}
		return userId;
	}
	
	/**
	 * Called to determine the expiration period for an attribute. This can be overwritten to parametrize the default expiration values.
	 * @param name name of the attribute.
	 * @return duration of the expiaration period in millis.
	 */
	protected long getExpirePeriodForAttribute(String name){
		return getDefaultExpirePeriodForAttribute();
	}
	/**
	 * Returns the default expiration period. Returned by not overwritten version of getExpirePeriodForAttribute(name).
	 * @return long value
	 */
	protected long getDefaultExpirePeriodForAttribute(){
		return APISession.DEFAULT_EXPIRE_PERIOD;
	}


	/**
	 * Returns APIConfig.
	 * @return APIConfig
	 */
	protected static APIConfig getApiConfig() {
		return apiConfig;
	}
	
	//////// VALIDATION /////
	/**
	 * Adds a validation error to the current context.
	 * @param field - field name
	 * @param cmsKey - cmsKey
	 * @param description - description
	 */
	protected void addValidationError(String field, String cmsKey, String description){
		addValidationError(new ValidationError(field, cmsKey, description));
	}
	
	/**
	 * Adds a validation error to the current context.
	 * @param error ValidationError instance
	 */
	protected void addValidationError(ValidationError error){
		APICallContext.getCallContext().addValidationError(error);
	}
	/**
	 * Used to abort execution if validation is unsuccessful.
	 * @throws net.anotheria.anoplass.api.validation.ValidationException when validation error occurs
	 */
	protected void checkValidationAndThrowException() throws ValidationException {
		if (APICallContext.getCallContext().hasValidationErrors())
			throw new ValidationException(APICallContext.getCallContext().getValidationErrors());
	}

}

