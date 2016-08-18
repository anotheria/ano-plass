package net.anotheria.anoplass.api.validation;

/**
 * An error in the input validation.
 *
 * @author another
 * @version $Id: $Id
 */
public class ValidationError {
	/**
	 * Field in which the error occurred.
	 */
	private String field;
	/**
	 * A debug message for the developer.
	 */
	private String message;
	/**
	 * The cms key with error description.
	 */
	private String cmsKey;

	/**
	 * Default constructor.
	 */
	public ValidationError(){
		
	}

	/**
	 * Constructor.
	 *
	 * @param aField name of field
	 * @param aCmsKey  key
	 * @param aMessage message
	 */
	public ValidationError(String aField, String aCmsKey, String aMessage){
		field = aField;
		cmsKey = aCmsKey;
		message = aMessage;
	}
	
	
	
	/**
	 * <p>Getter for the field <code>message</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * <p>Setter for the field <code>message</code>.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * <p>Getter for the field <code>cmsKey</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCmsKey() {
		return cmsKey;
	}
	/**
	 * <p>Setter for the field <code>cmsKey</code>.</p>
	 *
	 * @param cmsKey a {@link java.lang.String} object.
	 */
	public void setCmsKey(String cmsKey) {
		this.cmsKey = cmsKey;
	}

	/**
	 * <p>Getter for the field <code>field</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getField() {
		return field;
	}

	/**
	 * <p>Setter for the field <code>field</code>.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 */
	public void setField(String field) {
		this.field = field;
	}
	
	/** {@inheritDoc} */
	@Override public String toString(){
		return "Field : "+getField()+", Key: "+getCmsKey()+", Message: "+getMessage();
	}
}
