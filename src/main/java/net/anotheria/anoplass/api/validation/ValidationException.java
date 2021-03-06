package net.anotheria.anoplass.api.validation;

import java.util.ArrayList;
import java.util.List;

import net.anotheria.anoplass.api.APIException;

/**
 * ValidationException. Happens on validation errors.
 *
 * @author another
 * @version $Id: $Id
 */
public class ValidationException extends APIException {
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ValidationException 'errors'. List of validation errors.
	 */
	private List<ValidationError> errors;

	/**
	 * Constructor.
	 *
	 * @param someErrors errors
	 */
	public ValidationException(List<ValidationError> someErrors){
		errors = someErrors;
	}
	
	/**
	 * <p>Constructor for ValidationException.</p>
	 */
	public ValidationException(){
		this(new ArrayList<ValidationError>());
	}

	/**
	 * Returns errors.
	 *
	 * @return collection
	 */
	public List<ValidationError> getErrors(){
		return errors;
	}
	
}
