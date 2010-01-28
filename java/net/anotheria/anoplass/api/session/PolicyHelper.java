package net.anotheria.anoplass.api.session;

/**
 * Helper class for operations on APISession policies.
 * @author lrosenberg
 *
 */
public class PolicyHelper {
	/**
	 * Returns true if the policy autoExpire is set in this integer value.
	 * @param policy int param
	 * @return boolean value
	 */
	public static boolean isAutoExpiring(int policy){
		return isPolicySet(policy, APISession.POLICY_AUTOEXPIRE);
	}
	/**
	 * Returns true if the specified policy is set.
	 * @param policyToCheck int policy 1
	 * @param expectedPolicy int policy 2 which expected
	 * @return boolean value
	 */
	public static boolean isPolicySet(int policyToCheck, int expectedPolicy ){
		return !((policyToCheck & expectedPolicy) == 0);
	}
	/**
	 * Returns true if the distributed attribute policy is set.
	 * @param policy int value
	 * @return boolean value
	 */
	public static boolean isDistributed(int policy){
		return isPolicySet(policy, APISession.POLICY_DISTRIBUTED);
	}
}
