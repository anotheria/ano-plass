package net.anotheria.anoplass.api.session;

import java.io.Serializable;

/**
 * Used to wrap around all attributes put in APISession. Implements support for extended attribute function like 
 * expiration. 
 * @author lrosenberg
 *
 */
public class AttributeWrapper implements Serializable {
	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 2709101589774990085L;
	/**
	 * The attribute value.
	 */
	private Object value;
	/**
	 * The policy.
	 */
	private int policy;
	/**
	 * Attribute name (key).
	 */
	private String key;
	/**
	 * Time when this attribute will expire in millis.
	 */
	private long expiryTimestamp;
	
	/**
	 * If true the attribute has been edited recently. Needed by persistence policies.
	 */
	private boolean dirty;
	
	/**
	 * Creates a new AttributeWrapper.
	 * @param aKey
	 * @param aValue
	 * @param aPolicy
	 */
	public AttributeWrapper(String aKey, Object aValue, int aPolicy){
		this(aKey, aValue, aPolicy, PolicyHelper.isAutoExpiring(aPolicy) ? System.currentTimeMillis()+APISession.DEFAULT_EXPIRE_PERIOD : 0L);
	}
	
	/**
	 * Creates a new attribute wrapper with an expiration timestamp.
	 * @param aKey
	 * @param aValue
	 * @param aPolicy
	 * @param expiresWhen
	 */
	public AttributeWrapper(String aKey, Object aValue, int aPolicy, long expiresWhen){
		key = aKey;
		value = aValue;
		policy = aPolicy;
		expiryTimestamp = expiresWhen;
	}
	
	public int getPolicy() {
		return policy;
	}
	public void setPolicy(int aPolicy) {
		policy = aPolicy;
	}
	public Object getValue() {
		return isExpired() ? null : value;
	}
	public void setValue(Object aValue) {
		value = aValue;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String aKey) {
		key = aKey;
	}
	
	public boolean isExpiring(){
		return (policy & APISession.POLICY_AUTOEXPIRE) == APISession.POLICY_AUTOEXPIRE;
	}

	public boolean isFlashing(){
		return (policy & APISession.POLICY_FLASH) == APISession.POLICY_FLASH;
	}

    public long getExpiryTimestamp() {
        return expiryTimestamp;
    }

    public void setExpiryTimestamp(long expiryTimestamp) {
        this.expiryTimestamp = expiryTimestamp;
    }

    /**
	 * Returns true if the attribute is expired. Only attributes with policy autoexpire can expire.
	 * @return
	 */
	public boolean isExpired(){
		return isExpiring() && (System.currentTimeMillis() > expiryTimestamp); 
	}
	
	@Override
	public String toString(){
		return new StringBuilder("Key: ").append(getKey()).append(", Value: ").append(getValue()).append(", Policy: ").append(getPolicy()).toString();
	}

	/**
	 * Returns true if the underlying value is serializeable.
	 * @return
	 */
	public boolean isSerializable(){
		return value instanceof Serializable;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}


}
