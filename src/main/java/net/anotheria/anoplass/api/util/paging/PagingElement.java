package net.anotheria.anoplass.api.util.paging;

/**
 * A paging element is an element in the set of paging links returned by the PagingControl
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public abstract class PagingElement {
	/**
	 * Returns true if the element is active (selected)
	 *
	 * @return boolean value
	 */
	public abstract boolean isActive();
	
	/**
	 * Returns true if the element is a separator and not a link.
	 *
	 * @return boolean value
	 */
	public abstract boolean isSeparator();
	
	/**
	 * Returns true if the element is a link
	 *
	 * @return boolean value
	 */
	public abstract boolean isLinked();
		
	/**
	 * Returns the paging parameter to apply to the link
	 *
	 * @return String value
	 */
	public abstract String getPagingParameter();
	
	/**
	 * Return the caption of the element.
	 *
	 * @return String value
	 */
	public abstract String getCaption();
	
}
