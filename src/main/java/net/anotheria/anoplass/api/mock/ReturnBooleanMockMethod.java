package net.anotheria.anoplass.api.mock;

/**
 * A mock method which always return a boolean value.
 *
 * @author another
 * @version $Id: $Id
 */
public class ReturnBooleanMockMethod extends ReturnObjectMockMethod{
	/**
	 * Constructor.
	 *
	 * @param aValue boolean param
	 */
	public ReturnBooleanMockMethod(boolean aValue){
		super(aValue);
	}

}
