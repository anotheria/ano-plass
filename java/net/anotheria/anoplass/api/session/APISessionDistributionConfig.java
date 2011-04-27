package net.anotheria.anoplass.api.session;

import net.anotheria.util.TimeUnit;
import org.apache.log4j.Logger;
import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

/**
 * APISessionDistribution config.
 *
 * @author h3ll
 */

@ConfigureMe(name = "ano-plass-api-session-distribution")
public class APISessionDistributionConfig {

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
	 * APISessionDistributionConfig  'INSTANCE'.
	 */
	private static APISessionDistributionConfig CONFIG_INSTANCE;

	/**
	 * Get instance method.
	 *
	 * @return {@link APISessionDistributionConfig}
	 */
	public static synchronized APISessionDistributionConfig getInstance() {
		if (CONFIG_INSTANCE == null) {
			CONFIG_INSTANCE = new APISessionDistributionConfig();
			try {
				ConfigurationManager.INSTANCE.configure(CONFIG_INSTANCE);
			} catch (Exception e) {
				Logger.getLogger(APISessionDistributionConfig.class).error("Configuration failure.", e);
			}

		}
		return CONFIG_INSTANCE;

	}

	/**
	 * Private constructor.
	 */
	private APISessionDistributionConfig() {
		//Default value
		this.distributionEnabled = false;
		this.sessionIdCookieName = "a_s_id";
		this.apiSessionEventSenderQueueSize = DEFAULT_API_SESSION_EVENT_SENDER_CHANNEL_Q_SIZE;
		this.apiSessionEventSenderQueueSleepTime = DEFAULT_API_SESSION_EVENT_SENDER_CHANNEL_Q_SLEEP_TIME;
		this.apiSessionEventSenderQueueProcessingChannelsAmount = 10;
		this.sessionDistributorEventReceiverQueueSize = DEFAULT_SESSION_DISTRIBUTOR_EVENT_RECEIVER_Q_SIZE;
		this.sessionDistributorEventReceiverQueueSleepTime = DEFAULT_SESSION_DISTRIBUTOR_EVENT_RECEIVER_Q_SLEEP;
		this.distributedSessionKeepAliveCallInterval = TimeUnit.MINUTE.getMillis() * 5; // 5 minutes
	}


	public boolean isDistributionEnabled() {
		return distributionEnabled;
	}

	public void setDistributionEnabled(boolean aDistributionEnabled) {
		this.distributionEnabled = aDistributionEnabled;
	}

	public String getSessionIdCookieName() {
		return sessionIdCookieName;
	}

	public void setSessionIdCookieName(String aSessionIdCookieName) {
		this.sessionIdCookieName = aSessionIdCookieName;
	}

	public int getApiSessionEventSenderQueueSize() {
		return apiSessionEventSenderQueueSize;
	}

	public void setApiSessionEventSenderQueueSize(int aApiSessionEventSenderQueueSize) {
		this.apiSessionEventSenderQueueSize = aApiSessionEventSenderQueueSize;
	}

	public long getApiSessionEventSenderQueueSleepTime() {
		return apiSessionEventSenderQueueSleepTime;
	}

	public void setApiSessionEventSenderQueueSleepTime(long aApiSessionEventSenderQueueSleepTime) {
		this.apiSessionEventSenderQueueSleepTime = aApiSessionEventSenderQueueSleepTime;
	}

	public int getApiSessionEventSenderQueueProcessingChannelsAmount() {
		return apiSessionEventSenderQueueProcessingChannelsAmount;
	}

	public void setApiSessionEventSenderQueueProcessingChannelsAmount(int aApiSessionEventSenderQueueProcessingChannelsAmount) {
		this.apiSessionEventSenderQueueProcessingChannelsAmount = aApiSessionEventSenderQueueProcessingChannelsAmount;
	}

	public int getSessionDistributorEventReceiverQueueSize() {
		return sessionDistributorEventReceiverQueueSize;
	}

	public void setSessionDistributorEventReceiverQueueSize(int aSessionDistributorEventReceiverQueueSize) {
		this.sessionDistributorEventReceiverQueueSize = aSessionDistributorEventReceiverQueueSize;
	}

	public long getSessionDistributorEventReceiverQueueSleepTime() {
		return sessionDistributorEventReceiverQueueSleepTime;
	}

	public void setSessionDistributorEventReceiverQueueSleepTime(long aSessionDistributorEventReceiverQueueSleepTime) {
		this.sessionDistributorEventReceiverQueueSleepTime = aSessionDistributorEventReceiverQueueSleepTime;
	}

	public long getDistributedSessionKeepAliveCallInterval() {
		return distributedSessionKeepAliveCallInterval;
	}

	public void setDistributedSessionKeepAliveCallInterval(long aDistributedSessionKeepAliveCallInterval) {
		this.distributedSessionKeepAliveCallInterval = aDistributedSessionKeepAliveCallInterval;
	}
}
