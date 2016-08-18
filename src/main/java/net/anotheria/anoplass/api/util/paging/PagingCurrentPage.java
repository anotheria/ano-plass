package net.anotheria.anoplass.api.util.paging;

/**
 * PagingCurrentPage as subclass of PagingElement.
 *
 * @author another
 * @version $Id: $Id
 */
public class PagingCurrentPage extends PagingElement{
	/**
	 * PagingCurrentPage 'caption'.
	 */
	private String caption;

	/**
	 * Constructor.
	 *
	 * @param aCaption caption
	 */
	public PagingCurrentPage(String aCaption){
		caption = aCaption;
	}

	/** {@inheritDoc} */
	@Override
	public String getCaption() {
		return caption;
	}

	/** {@inheritDoc} */
	@Override
	public String getPagingParameter() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isActive() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isLinked() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSeparator() {
		return false;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString(){
		return "caption:"+caption;
	}
}
