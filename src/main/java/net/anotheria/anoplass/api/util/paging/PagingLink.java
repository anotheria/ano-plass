package net.anotheria.anoplass.api.util.paging;

/**
 * PagingLink as subclass of PagingElement.
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
	 * @param aCaption caption
	 * @param pageNumber number
	 */
	public PagingLink(String aCaption, int pageNumber){
		caption = aCaption;
		pagingParameter = String.valueOf(pageNumber);
	}

	@Override
	public String getPagingParameter() {
		return pagingParameter;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public boolean isLinked() {
		return true;
	}

	@Override
	public boolean isSeparator() {
		return false;
	}
	
	@Override
	public String getCaption(){
		return caption;
	}
	
	@Override
	public String toString(){
		return "caption:"+caption+", pagingParameter:"+pagingParameter;
	}
}
