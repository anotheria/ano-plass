package net.anotheria.anoplass.api.validation;

/**
 * An error in the input validation.
 * @author another
 *
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
	 * @param aField name of field
	 * @param aCmsKey  key
	 * @param aMessage message
	 */
	public ValidationError(String aField, String aCmsKey, String aMessage){
		field = aField;
		cmsKey = aCmsKey;
		message = aMessage;
	}
	
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCmsKey() {
		return cmsKey;
	}
	public void setCmsKey(String cmsKey) {
		this.cmsKey = cmsKey;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
	
	@Override public String toString(){
		return "Field : "+getField()+", Key: "+getCmsKey()+", Message: "+getMessage();
	}
}
