package net.anotheria.anoplass.api.session.utility;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoplass.api.session.APISessionDistributionConfig;
import net.anotheria.util.StringUtils;
import net.anotheria.util.UrlHelper;

/**
 * APISession id parameter helper, utility class for creating DistributedSessionId parameter, and adding it to URL.
 *
 * @author h3ll
 */

public final class APISessionIdParameterHelper {

	/**
	 * {@link APISessionDistributionConfig} instance.
	 */
	private static APISessionDistributionConfig config = APISessionDistributionConfig.getInstance();

	/**
	 * Create key=value pair where key is DistributedSessionName parameter and value is actually
	 * distributed APISessionId.
	 * Method will return empty String if APISessionDistribution is disabled.
	 *
	 * @return APISessionId parameter string
	 */
	public static String createSessionIdParameterString() {
		if (!config.isDistributionEnabled())
			return "";
		return config.getDistributedSessionParameterName() + '=' + APICallContext.getCallContext().getCurrentSession().getId();
	}

	/**
	 * Add distributed session parameter to incoming URL if distribution is enabled. Otherwise same URL will be returned.
	 *
	 * @param incomingUrl incoming URL
	 * @return UR with diSeName parameter.
	 */
	public static String addAPISessionIdParameterToUrl(String incomingUrl) {
		if (StringUtils.isEmpty(incomingUrl))
			throw new IllegalArgumentException("Invalid incomingUrl parameter.");

		if (!config.isDistributionEnabled())
			return incomingUrl;

		UrlHelper urlHelper = new UrlHelper(incomingUrl);
		urlHelper.addParameter(createSessionIdParameterString());
		return urlHelper.toString();

	}

	/**
	 * Private constructor.
	 */
	private APISessionIdParameterHelper() {
		throw new IllegalAccessError("invalid invocation :)");
	}

}
