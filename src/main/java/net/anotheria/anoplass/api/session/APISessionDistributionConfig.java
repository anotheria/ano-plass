package net.anotheria.anoplass.api.session;

import net.anotheria.util.TimeUnit;
import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.slf4j.LoggerFactory;

/**
 * APISessionDistribution config.
 *
 * @author h3ll
 * @version $Id: $Id
 */

@ConfigureMe(name = "ano-plass-api-session-distribution")
public final class APISessionDistributionConfig {

	/**
	 * Default apiSession event channel queue size.
	 */
	protected static final int DEFAULT_API_SESSION_EVENT_SENDER_CHANNEL_Q_SIZE = 5000;
	/**
	 * Default apiSession event channel queue sleep time.
	 */
	protected static final long DEFAULT_API_SESSION_EVENT_SENDER_CHANNEL_Q_SLEEP_TIME = 300;

	/**
	 * Default sessionDistributor event receiver queue size.
	 */
	protected static final int DEFAULT_SESSION_DISTRIBUTOR_EVENT_RECEIVER_Q_SIZE = 5000;
	/**
	 * Default sessionDistributor event receiver queue sleep time.
	 */
	protected static final long DEFAULT_SESSION_DISTRIBUTOR_EVENT_RECEIVER_Q_SLEEP = 300;
	/**
	 * Default value for distributedSessionIdCookie name.
	 */
	private static final String DEFAULT_SESSION_ID_COOKIE_NAE = "a_s_id";
	/**
	 * Default value for distributedSessionId URL parameter name.
	 */
	private static final String DEFAULT_DISTRIBUTED_SESSION_ID_PARAM_NAME = "asDiSeName";
	/**
	 * APISessionDistributionConfig 'distributionEnabled'.
	 */
	@Configure
	private boolean distributionEnabled;

	/**
	 * APISessionDistributionConfig sessionId cookie name.
	 */
	@Configure
	private String sessionIdCookieName;

	/**
	 * APISessionDistributionConfig distributedSessionParameterName.
	 * Represent URL parameter  name under which APISessionId
	 * can be found.
	 */
	@Configure
	private String distributedSessionParameterName;

	/**
	 * APISessionDistributionConfig event queue size.
	 */
	@Configure
	private int apiSessionEventSenderQueueSize;

	/**
	 * APISessionDistributionConfig event queue sleep time.
	 */
	@Configure
	private long apiSessionEventSenderQueueSleepTime;

	/**
	 * APISessionDistributionConfig threads amount.
	 */
	@Configure
	private int apiSessionEventSenderQueueProcessingChannelsAmount;

	/**
	 * APISessionDistributionConfig 'sessionDistributorEventReceiverQueueSize'.
	 * Size for sessionDistributor receiver queue.
	 */
	@Configure
	private int sessionDistributorEventReceiverQueueSize;
	/**
	 * APISessionDistributionConfig 'sessionDistributorEventReceiverQueueSleepTime'.
	 * Sleep time for sessionDistributor event receiver queue.
	 */
	@Configure
	private long sessionDistributorEventReceiverQueueSleepTime;

	/**
	 * APISessionDistributionConfig 'distributedSessionKeepAliveCallInterval'.
	 * Specify interval for keep alive calls.
	 */
	@Configure
	private long distributedSessionKeepAliveCallInterval;

	/**
	 * Domain which should be used to set the session id cookie.
	 */
	@Configure
	private String sessionIdCookieDomain;

	/**
	 * APISessionDistributionConfig  'INSTANCE'.
	 */
	private static APISessionDistributionConfig configuration;

	/**
	 * Get instance method.
	 *
	 * @return {@link net.anotheria.anoplass.api.session.APISessionDistributionConfig}
	 */
	public static synchronized APISessionDistributionConfig getInstance() {
		if (configuration == null) {
			configuration = new APISessionDistributionConfig();
			try {
				ConfigurationManager.INSTANCE.configure(configuration);
			}catch(IllegalArgumentException e){
				LoggerFactory.getLogger(APISessionDistributionConfig.class).warn("SessionDistribution is not configured, check ano-plass-api-session-distribution.json");
			} catch (Exception e) {
                LoggerFactory.getLogger(APISessionDistributionConfig.class).error("Configuration failure.", e);
			}

		}
		return configuration;

	}

	/**
	 * Private constructor.
	 */
	private APISessionDistributionConfig() {
		//Default value
		this.distributionEnabled = false;
		this.sessionIdCookieName = DEFAULT_SESSION_ID_COOKIE_NAE;
		this.distributedSessionParameterName = DEFAULT_DISTRIBUTED_SESSION_ID_PARAM_NAME;
		this.apiSessionEventSenderQueueSize = DEFAULT_API_SESSION_EVENT_SENDER_CHANNEL_Q_SIZE;
		this.apiSessionEventSenderQueueSleepTime = DEFAULT_API_SESSION_EVENT_SENDER_CHANNEL_Q_SLEEP_TIME;
		this.apiSessionEventSenderQueueProcessingChannelsAmount = 10;
		this.sessionDistributorEventReceiverQueueSize = DEFAULT_SESSION_DISTRIBUTOR_EVENT_RECEIVER_Q_SIZE;
		this.sessionDistributorEventReceiverQueueSleepTime = DEFAULT_SESSION_DISTRIBUTOR_EVENT_RECEIVER_Q_SLEEP;
		this.distributedSessionKeepAliveCallInterval = TimeUnit.MINUTE.getMillis() * 5; // 5 minutes
		this.sessionIdCookieDomain = "";
	}


	/**
	 * <p>isDistributionEnabled.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isDistributionEnabled() {
		return distributionEnabled;
	}

	/**
	 * <p>Setter for the field <code>distributionEnabled</code>.</p>
	 *
	 * @param aDistributionEnabled a boolean.
	 */
	public void setDistributionEnabled(boolean aDistributionEnabled) {
		this.distributionEnabled = aDistributionEnabled;
	}

	/**
	 * <p>Getter for the field <code>sessionIdCookieName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSessionIdCookieName() {
		return sessionIdCookieName;
	}

	/**
	 * <p>Setter for the field <code>sessionIdCookieName</code>.</p>
	 *
	 * @param aSessionIdCookieName a {@link java.lang.String} object.
	 */
	public void setSessionIdCookieName(String aSessionIdCookieName) {
		this.sessionIdCookieName = aSessionIdCookieName;
	}

	/**
	 * <p>Getter for the field <code>distributedSessionParameterName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDistributedSessionParameterName() {
		return distributedSessionParameterName;
	}

	/**
	 * <p>Setter for the field <code>distributedSessionParameterName</code>.</p>
	 *
	 * @param distributedSessionParameterName a {@link java.lang.String} object.
	 */
	public void setDistributedSessionParameterName(String distributedSessionParameterName) {
		this.distributedSessionParameterName = distributedSessionParameterName;
	}

	/**
	 * <p>Getter for the field <code>apiSessionEventSenderQueueSize</code>.</p>
	 *
	 * @return a int.
	 */
	public int getApiSessionEventSenderQueueSize() {
		return apiSessionEventSenderQueueSize;
	}

	/**
	 * <p>Setter for the field <code>apiSessionEventSenderQueueSize</code>.</p>
	 *
	 * @param aApiSessionEventSenderQueueSize a int.
	 */
	public void setApiSessionEventSenderQueueSize(int aApiSessionEventSenderQueueSize) {
		this.apiSessionEventSenderQueueSize = aApiSessionEventSenderQueueSize;
	}

	/**
	 * <p>Getter for the field <code>apiSessionEventSenderQueueSleepTime</code>.</p>
	 *
	 * @return a long.
	 */
	public long getApiSessionEventSenderQueueSleepTime() {
		return apiSessionEventSenderQueueSleepTime;
	}

	/**
	 * <p>Setter for the field <code>apiSessionEventSenderQueueSleepTime</code>.</p>
	 *
	 * @param aApiSessionEventSenderQueueSleepTime a long.
	 */
	public void setApiSessionEventSenderQueueSleepTime(long aApiSessionEventSenderQueueSleepTime) {
		this.apiSessionEventSenderQueueSleepTime = aApiSessionEventSenderQueueSleepTime;
	}

	/**
	 * <p>Getter for the field <code>apiSessionEventSenderQueueProcessingChannelsAmount</code>.</p>
	 *
	 * @return a int.
	 */
	public int getApiSessionEventSenderQueueProcessingChannelsAmount() {
		return apiSessionEventSenderQueueProcessingChannelsAmount;
	}

	/**
	 * <p>Setter for the field <code>apiSessionEventSenderQueueProcessingChannelsAmount</code>.</p>
	 *
	 * @param aApiSessionEventSenderQueueProcessingChannelsAmount a int.
	 */
	public void setApiSessionEventSenderQueueProcessingChannelsAmount(int aApiSessionEventSenderQueueProcessingChannelsAmount) {
		this.apiSessionEventSenderQueueProcessingChannelsAmount = aApiSessionEventSenderQueueProcessingChannelsAmount;
	}

	/**
	 * <p>Getter for the field <code>sessionDistributorEventReceiverQueueSize</code>.</p>
	 *
	 * @return a int.
	 */
	public int getSessionDistributorEventReceiverQueueSize() {
		return sessionDistributorEventReceiverQueueSize;
	}

	/**
	 * <p>Setter for the field <code>sessionDistributorEventReceiverQueueSize</code>.</p>
	 *
	 * @param aSessionDistributorEventReceiverQueueSize a int.
	 */
	public void setSessionDistributorEventReceiverQueueSize(int aSessionDistributorEventReceiverQueueSize) {
		this.sessionDistributorEventReceiverQueueSize = aSessionDistributorEventReceiverQueueSize;
	}

	/**
	 * <p>Getter for the field <code>sessionDistributorEventReceiverQueueSleepTime</code>.</p>
	 *
	 * @return a long.
	 */
	public long getSessionDistributorEventReceiverQueueSleepTime() {
		return sessionDistributorEventReceiverQueueSleepTime;
	}

	/**
	 * <p>Setter for the field <code>sessionDistributorEventReceiverQueueSleepTime</code>.</p>
	 *
	 * @param aSessionDistributorEventReceiverQueueSleepTime a long.
	 */
	public void setSessionDistributorEventReceiverQueueSleepTime(long aSessionDistributorEventReceiverQueueSleepTime) {
		this.sessionDistributorEventReceiverQueueSleepTime = aSessionDistributorEventReceiverQueueSleepTime;
	}

	/**
	 * <p>Getter for the field <code>distributedSessionKeepAliveCallInterval</code>.</p>
	 *
	 * @return a long.
	 */
	public long getDistributedSessionKeepAliveCallInterval() {
		return distributedSessionKeepAliveCallInterval;
	}

	/**
	 * <p>Setter for the field <code>distributedSessionKeepAliveCallInterval</code>.</p>
	 *
	 * @param aDistributedSessionKeepAliveCallInterval a long.
	 */
	public void setDistributedSessionKeepAliveCallInterval(long aDistributedSessionKeepAliveCallInterval) {
		this.distributedSessionKeepAliveCallInterval = aDistributedSessionKeepAliveCallInterval;
	}

	/**
	 * <p>Getter for the field <code>sessionIdCookieDomain</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSessionIdCookieDomain() {
		return sessionIdCookieDomain;
	}

	/**
	 * <p>Setter for the field <code>sessionIdCookieDomain</code>.</p>
	 *
	 * @param sessionIdCookieDomain a {@link java.lang.String} object.
	 */
	public void setSessionIdCookieDomain(String sessionIdCookieDomain) {
		this.sessionIdCookieDomain = sessionIdCookieDomain;
	}
}
