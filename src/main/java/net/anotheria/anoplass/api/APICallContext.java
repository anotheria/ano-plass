package net.anotheria.anoplass.api;

import net.anotheria.anoplass.api.session.APISession;
import net.anotheria.anoplass.api.validation.ValidationError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * A context in which the api is executed. The context is assigned to the Thread (as ThreadLocal) and therefore don't need to be synchronized. 
 * However, the context is killed (or lost) as soon as the thread finishes execution.
 * Important: If you start a new thread it does NOT share the same context, and hence same session.
 * @author lrosenberg
 *
 */
public class APICallContext {
	/**
	 * The default locale if no specific locale has been set. Currently its en_GB.
	 */
	public static final Locale DEFAULT_LOCALE = new Locale("en", "GB");

	/**
	 * Currently assigned locale.
	 */
	private Locale currentLocale;
	/**
	 * Currently assigned session.
	 */
	private APISession currentSession;

	/**
	 * Currently assigned domain name.
	 */
	private String currentServerName;

	/**
	 * A context wide scope. It exists only as long as the request lasts. 
	 */
	private Map<String, Object> scope = new HashMap<>();
	/**
	 * Currently assigned userId if there is a logged in user.
	 */
	private String currentUserId;
	/**
	 * Currently assigned (cms-) editor id. This is the id/username the editor logs in with. Can be used in guards to ensure that 
	 * there is a logged in editor in the site.
	 */
	private String currentEditorId;
	/**
	 * Errors due to validation of the request.
	 */
	private List<ValidationError> validationErrors;
	
	/**
	 * Creates a new APICallContext. After creation the context is reset by calling reset().
	 */
	private APICallContext(){
		reset();
	}
	
	/**
	 * Returns the currentUserId if such concept is supported by the site.
	 * @return the userid used to log in in the site if required.
	 */
	public String getCurrentUserId() { 
		return currentUserId;
	}

	/**
	 * Sets the currentUserId. This is a site-specific feature, the call context doesn't evaluate the userId.
	 * @param aCurrentUserId any string which is meaningful to the application.
	 */
	public void setCurrentUserId(String aCurrentUserId) {
		currentUserId = aCurrentUserId;
	}

	/**
	 * Returns current editorId if any. 
	 * @return string used as username by the editor if he logs in into the site.
	 */
	public String getCurrentEditorId() {
		return currentEditorId;
	}

	/**
	 * Sets the current editor id.
	 * @param aCurrentEditorId the username of the editor as in users.xml (for now).
	 */
	public void setCurrentEditorId(String aCurrentEditorId) {
		currentEditorId = aCurrentEditorId;
	}

	/**
	 * Called upon association with a Thread, to remove all tracks of the previous service.
	 */
	public void reset(){
		currentLocale = DEFAULT_LOCALE;
		currentServerName = "";
		currentSession = null;
		scope = new HashMap<>();
		currentUserId = null;
		currentEditorId = null;
		validationErrors = new ArrayList<>();
	}

	/**
	 * Sets an attribute into internal scope.
	 * @param name name of the attribute.
	 * @param value value of the attribute.
	 */
	public void setAttribute(String name, Object value) {
		scope.put(name, value);
	}
	
	/**
	 * Returns the attribute with given name from internal scope or null if no such atribute is present.
	 * @param name the name of the attribute.
	 * @return attribute value object or null.
	 */
	public Object getAttribute(String name) {
		return scope.get(name);
	}
	
	/**
	 * Removes an attribute from internal scope. If no such attribute is present, the call is ignored.
	 * @param name the name of the attribute to remove.
	 */
	public void removeAttribute(String name) {
		scope.remove(name);
	}
	
	/**
	 * Returns the currentLocale. If no currentLocale has been set, the DEFAULT_LOCALE is returned.
	 * @return the locale associated with this request.
	 */
	public Locale getCurrentLocale() {
		return currentLocale == null ? DEFAULT_LOCALE : currentLocale;
	}

	/**
	 * Sets current locale. Usually performed by a locale resolution mechanism at the start of the request, for example a locale filter. Also updates the locale in session (if session exists).
	 * @param aCurrentLocale the locale to set.
	 */
	public void setCurrentLocale(Locale aCurrentLocale) {
		currentLocale = aCurrentLocale;
		if (currentSession!=null)
			currentSession.setLocale(currentLocale);
	}
	
	/**
	 * Returns the currently assigned session.
	 * @return the assigned APISession.
	 */
	public APISession getCurrentSession() {
		return currentSession;
	}

	/**
	 * Sets the current session.
	 * @param aCurrentSession APISession instance
	 */
	public void setCurrentSession(APISession aCurrentSession) {
		currentSession = aCurrentSession;
	}
	
	
	@Override public String toString(){
        return "User: " +
			(isMember() ? currentUserId : "guest")+
			" session: "+currentSession+", locale: "+currentLocale+ ", serverName: "+currentServerName+
			", scope contains "+scope.size()+" elements.";
	}
	
	/**
	 * Returns true if there is no logged in user in this context.
	 * @return true for not logged in user, false otherwise.
	 */
	public boolean isGuest(){
		return !isMember();
	}
	
	/**
	 * Returns true if there is a logged in user in this context.
	 * @return true if a the currentUserId is not null.
	 */
	public boolean isMember(){
		return currentUserId!=null;
	}
	
	/**
	 * Returns true if there is an editorId in the session.
	 * @return true if the currentEditorId is not null.
	 */
	public boolean isEditor(){
		return currentEditorId != null;
	}
	
	/**
	 * Adds a validationError to the context.
	 * @param error error to add.
	 */
	public void addValidationError(ValidationError error){
		validationErrors.add(error);
	}
	
	/**
	 * Adds some validation errors to the context.
	 * @param errors a list of errors to add.
	 */
	public void addValidationErrors(Collection<ValidationError> errors){
		validationErrors.addAll(errors);
	}
	
	/**
	 * Returns the validation errors.
	 * @return list of ValidationError
	 */
	public List<ValidationError> getValidationErrors() {
		return validationErrors;
	}

	/**
	 * Sets the list of validation errors, reseting previously set or added errors.
	 * @param someValidationErrors the validation errors to set.
	 */
	public void setValidationErrors(List<ValidationError> someValidationErrors) {
		validationErrors = someValidationErrors;
	}

	/**
	 * Returns true if there are validation errors.
	 * @return boolean param
	 */
	public boolean hasValidationErrors(){
		return !validationErrors.isEmpty();
	}

	/**
	 * Removes validationErrors.
	 */
	public void resetValidationErrors(){
		validationErrors = new ArrayList<>();
	}

	/**
	 * Get server hostname.
	 *
	 * @return get server hostname
	 */
	public String getCurrentServerName() {
		return currentServerName;
	}

	/**
	 * Set server name.
	 *
	 * @param currentServerName server hostname
	 */
	public void setCurrentServerName(String currentServerName) {
		this.currentServerName = currentServerName;
	}
	
	/**
	 * Used by the api if you want to span a new thread but share the values with that thread. As soon as the parameters are shared 
	 * each thread works on its own copy which is not shared.
	 * @param anotherContext the APICallContext to copy from
	 */
	void copyFromAnotherContext(APICallContext anotherContext){
		currentLocale = anotherContext.currentLocale;
		currentSession = anotherContext.currentSession;
		currentServerName = anotherContext.currentServerName;
		scope = new HashMap<>(); scope.putAll(anotherContext.scope);
		currentUserId = anotherContext.currentUserId;
		currentEditorId = anotherContext.currentEditorId;
		validationErrors = new ArrayList<>(); validationErrors.addAll(anotherContext.validationErrors);
	}
	
	
	////////////// END ////////////////////
	
	/**
	 * The thread local variable associated with the current thread.
	 */
	private static InheritableThreadLocal<APICallContext> apiCallContext = new InheritableThreadLocal<APICallContext>() {
		@Override
		protected synchronized APICallContext initialValue() {
			return new APICallContext();
		}

		@Override
		protected APICallContext childValue(APICallContext parentValue) {
			APICallContext ret = new APICallContext();
			ret.copyFromAnotherContext(parentValue);
			return ret;
		}
	};
	
	/**
	 * Returns the APICallContext assigned to this thread. 
	 * @return previously assigned or new ApiCallContext object.
	 */
	public static APICallContext getCallContext(){
		return apiCallContext.get();
	}
	
	/**
	 * Removes the thread local instance of the api call context, in order to prevent webapp redeployment leaks.
	 */
	public static void remove(){
		apiCallContext.remove();
	}

}
