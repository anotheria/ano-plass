package net.anotheria.anoplass.api.mock;

/**
 * A mock method which always return a predefined integer value.
 *
 * @author another
 * @version $Id: $Id
 */
public class ReturnIntegerMockMethod extends ReturnObjectMockMethod{
	/**
	 * Constructor.
	 *
	 * @param aValue int param
	 */
	public ReturnIntegerMockMethod(int aValue){
		super(aValue);
	}
}
