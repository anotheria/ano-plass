package net.anotheria.anoplass.api.generic.observation;

import net.anotheria.anoplass.api.APIFactory;

/**
 * The factory for the ObservationAPI implementation.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class ObservationAPIFactory implements APIFactory<ObservationAPI> {
	/** {@inheritDoc} */
	@Override
	public ObservationAPI createAPI() {
		return new ObservationAPIImpl();
	}
}
