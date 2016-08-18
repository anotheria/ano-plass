package net.anotheria.anoplass.api.util.paging;

/**
 * PagingLink as subclass of PagingElement.
 *
 * @author another
 * @version $Id: $Id
 */
public class PagingLink extends PagingElement{
	/**
	 * PagingLink "pagingParameter".
	 */
	private String pagingParameter;
	/**
	 * PagingLink "caption".
	 */
	private String caption;

	/**
	 * Constructor.
	 *
	 * @param aCaption caption
	 * @param pageNumber number
	 */
	public PagingLink(String aCaption, int pageNumber){
		caption = aCaption;
		pagingParameter = ""+pageNumber;
	}

	/** {@inheritDoc} */
	@Override
	public String getPagingParameter() {
		return pagingParameter;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isActive() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isLinked() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSeparator() {
		return false;
	}
	
	/** {@inheritDoc} */
	@Override
	public String getCaption(){
		return caption;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString(){
		return "caption:"+caption+", pagingParameter:"+pagingParameter;
	}
}
