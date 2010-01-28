package net.anotheria.anoplass.api.util.paging;

/**
 * A PagingElement flavour used to represent a separator.
 * @author lrosenberg
 */
public class Separator extends PagingElement{

	@Override
	public String getCaption() {
		return null;
	}

	@Override
	public String getPagingParameter() {
		return null;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public boolean isLinked() {
		return false;
	}

	@Override
	public boolean isSeparator() {
		return true;
	}
}