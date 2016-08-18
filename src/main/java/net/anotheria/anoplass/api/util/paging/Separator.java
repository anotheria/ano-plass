package net.anotheria.anoplass.api.util.paging;

/**
 * A PagingElement flavour used to represent a separator.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class Separator extends PagingElement{

	/** {@inheritDoc} */
	@Override
	public String getCaption() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public String getPagingParameter() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isActive() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isLinked() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSeparator() {
		return true;
	}
}
