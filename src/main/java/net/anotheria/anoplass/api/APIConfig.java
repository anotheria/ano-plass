package net.anotheria.anoplass.api;

import net.anotheria.anoplass.api.activity.ActivityAPI;
import net.anotheria.anoplass.api.activity.ActivityAPIFactory;
import net.anotheria.anoplass.api.generic.login.LoginAPI;
import net.anotheria.anoplass.api.generic.login.LoginAPIFactory;
import net.anotheria.anoplass.api.generic.observation.ObservationAPI;
import net.anotheria.anoplass.api.generic.observation.ObservationAPIFactory;
import org.configureme.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * APIConfig.
 *
 * @author another
 * @version $Id: $Id
 */
public class APIConfig {

	/**
	 * SERVICE POLICY SINGLE INSTANCE Constant.
	 */
	public static final int SERVICE_POLICY_SINGLE_INSTANCE = 1;
	/**
	 * SERVICE POLICY MULTI INSTANCE Constant.
	 */
	public static final int SERVICE_POLICY_MULTI_INSTANCE = 2;
	/**
	 * SERVICE POLICY DEFAULT - Constant.
	 */
	public static final int SERVICE_POLICY_DEFAULT = SERVICE_POLICY_MULTI_INSTANCE;
	/**
	 * APIConfig "servicePolicy". 
	 */
	private static int servicePolicy;
	/**
	 * APIConfig "aliasMap". Actually alias holder.
	 */
	private static Map<Class<? extends API>, List<Class<? extends API>>> aliasMap;

	/**
	 * APIConfigurable instance.
	 */
	private static APIConfigurable configurable;

    /**
     * {@link Logger} logger.
     */
	private static Logger log = LoggerFactory.getLogger(APIConfig.class);
	
	/**
	 * Initialisation.
	 */
	static {
		servicePolicy = SERVICE_POLICY_DEFAULT;

		aliasMap = new HashMap<Class<? extends API>, List<Class<? extends API>>>();
		// addAlias(ITargetingAPI.class, ITargetingTestingAPI.class);

		configurable = new APIConfigurable();
		try{
			ConfigurationManager.INSTANCE.configure(configurable);
		}catch(IllegalArgumentException apiConfigNotFound){
			log.warn("apiconfig not found, running with default values");
		}
	}

	static {
		servicePolicy = SERVICE_POLICY_DEFAULT;

		aliasMap = new HashMap<Class<? extends API>, List<Class<? extends API>>>();
		// addAlias(ITargetingAPI.class, ITargetingTestingAPI.class);

		configurable = new APIConfigurable();
		try{
			ConfigurationManager.INSTANCE.configure(configurable);
		}catch(IllegalArgumentException apiConfigNotFound){
			log.warn("apiconfig not found, running with default values");
		}
	}
	@SuppressWarnings("unused")
	private static void addAlias(Class<? extends API> interfaceClass,
			Class<? extends API> aliasClass) {
		List<Class<? extends API>> aliases = aliasMap.get(interfaceClass);
		if (aliases == null) {
			aliases = new ArrayList<Class<? extends API>>();
			aliasMap.put(interfaceClass, aliases);
		}
		aliases.add(aliasClass);
	}

	/**
	 * Return configured Aliases for selected API.
	 *
	 * @param source API.
	 * @return Collection
	 */
	public static List<Class<? extends API>> getAliases(
			Class<? extends API> source) {
		return aliasMap.get(source);
	}

	/**
	 * Creates factories map.
	 *
	 * @return Collection
	 */
	public static Map<Class<? extends API>, APIFactory<? extends API>> getFactories() {

		Map<Class<? extends API>, APIFactory<? extends API>> ret = new HashMap<Class<? extends API>, APIFactory<? extends API>>();

		ret.put(ActivityAPI.class, new ActivityAPIFactory());
		ret.put(LoginAPI.class, new LoginAPIFactory());
		ret.put(ObservationAPI.class, new ObservationAPIFactory());

		// ret.put(ISystemMailAPI.class, new MailAPIFactory());
		return ret;
	}

	/**
	 * Returns configured interface aliases.
	 *
	 * @return collection.
	 */
	public static Map<Class<? extends API>, List<Class<? extends API>>> getInterfaceAliases() {
		return aliasMap;
	}

	/**
	 * Returns current service policy.
	 *
	 * @return int value
	 */
	public static int getServicePolicy() {
		return servicePolicy;
	}

	/**
	 * Returns true if verboseMethodCall is enabled, false otherwise.
	 *
	 * @return boolean value
	 */
	public static boolean verboseMethodCalls() {
		return configurable.isVerboseMethodCalls();
	}

	/**
	 * <p>associateSessions.</p>
	 *
	 * @return a boolean.
	 */
	public static boolean associateSessions(){
		return configurable.isAssociateSessions();
	}

	/**
	 * <p>enableAPIMonitoring.</p>
	 *
	 * @return a boolean.
	 */
	public static boolean enableAPIMonitoring(){
		return configurable.isEnableApiMonitoring();
	}

}
