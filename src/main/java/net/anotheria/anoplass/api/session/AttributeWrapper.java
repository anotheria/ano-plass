package net.anotheria.anoplass.api.session;

import java.io.Serializable;

/**
 * Used to wrap around all attributes put in APISession. Implements support for extended attribute function like
 * expiration.
 *
 * @author lrosenberg
 * @version $Id: $Id
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
	 *
	 * @param aKey a {@link java.lang.String} object.
	 * @param aValue a {@link java.lang.Object} object.
	 * @param aPolicy a int.
	 */
	public AttributeWrapper(String aKey, Object aValue, int aPolicy){
		this(aKey, aValue, aPolicy, PolicyHelper.isAutoExpiring(aPolicy) ? System.currentTimeMillis()+APISession.DEFAULT_EXPIRE_PERIOD : 0L);
	}
	
	/**
	 * Creates a new attribute wrapper with an expiration timestamp.
	 *
	 * @param aKey a {@link java.lang.String} object.
	 * @param aValue a {@link java.lang.Object} object.
	 * @param aPolicy a int.
	 * @param expiresWhen a long.
	 */
	public AttributeWrapper(String aKey, Object aValue, int aPolicy, long expiresWhen){
		key = aKey;
		value = aValue;
		policy = aPolicy;
		expiryTimestamp = expiresWhen;
	}
	
	/**
	 * <p>Getter for the field <code>policy</code>.</p>
	 *
	 * @return a int.
	 */
	public int getPolicy() {
		return policy;
	}
	/**
	 * <p>Setter for the field <code>policy</code>.</p>
	 *
	 * @param aPolicy a int.
	 */
	public void setPolicy(int aPolicy) {
		policy = aPolicy;
	}
	/**
	 * <p>Getter for the field <code>value</code>.</p>
	 *
	 * @return a {@link java.lang.Object} object.
	 */
	public Object getValue() {
		return isExpired() ? null : value;
	}
	/**
	 * <p>Setter for the field <code>value</code>.</p>
	 *
	 * @param aValue a {@link java.lang.Object} object.
	 */
	public void setValue(Object aValue) {
		value = aValue;
	}

	/**
	 * <p>Getter for the field <code>key</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * <p>Setter for the field <code>key</code>.</p>
	 *
	 * @param aKey a {@link java.lang.String} object.
	 */
	public void setKey(String aKey) {
		key = aKey;
	}
	
	/**
	 * <p>isExpiring.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isExpiring(){
		return (policy & APISession.POLICY_AUTOEXPIRE) == APISession.POLICY_AUTOEXPIRE;
	}

	/**
	 * <p>isFlashing.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isFlashing(){
		return (policy & APISession.POLICY_FLASH) == APISession.POLICY_FLASH;
	}

    /**
     * <p>Getter for the field <code>expiryTimestamp</code>.</p>
     *
     * @return a long.
     */
    public long getExpiryTimestamp() {
        return expiryTimestamp;
    }

    /**
     * <p>Setter for the field <code>expiryTimestamp</code>.</p>
     *
     * @param expiryTimestamp a long.
     */
    public void setExpiryTimestamp(long expiryTimestamp) {
        this.expiryTimestamp = expiryTimestamp;
    }

	/**
	 * Returns true if the attribute is expired. Only attributes with policy autoexpire can expire.
	 *
	 * @return a boolean.
	 */
	public boolean isExpired(){
		return isExpiring() && (System.currentTimeMillis() > expiryTimestamp); 
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString(){
		return new StringBuilder("Key: ").append(getKey()).append(", Value: ").append(getValue()).append(", Policy: ").append(getPolicy()).toString();
	}

	/**
	 * Returns true if the underlying value is serializeable.
	 *
	 * @return a boolean.
	 */
	public boolean isSerializable(){
		return value instanceof Serializable;
	}

	/**
	 * <p>isDirty.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * <p>Setter for the field <code>dirty</code>.</p>
	 *
	 * @param dirty a boolean.
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}


}
