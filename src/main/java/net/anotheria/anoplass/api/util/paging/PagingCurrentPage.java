package net.anotheria.anoplass.api.util.paging;

/**
 * PagingCurrentPage as subclass of PagingElement.
 */
public class PagingCurrentPage extends PagingElement{
	/**
	 * PagingCurrentPage 'caption'.
	 */
	private String caption;

	/**
	 * Constructor.
	 * @param aCaption caption
	 */
	public PagingCurrentPage(String aCaption){
		caption = aCaption;
	}

	@Override
	public String getCaption() {
		return caption;
	}

	@Override
	public String getPagingParameter() {
		return null;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public boolean isLinked() {
		return false;
	}

	@Override
	public boolean isSeparator() {
		return false;
	}
	
	@Override
	public String toString(){
		return "caption:"+caption;
	}
}
