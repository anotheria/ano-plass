package net.anotheria.anoplass.api.generic.security;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>SecurityObject class.</p>
 *
 * @author another
 * @version $Id: $Id
 */
public class SecurityObject {
	private String id;
	private Map<String, Object> attributes;

	/**
	 * <p>Constructor for SecurityObject.</p>
	 */
	public SecurityObject(){
		attributes = new HashMap<String, Object>();
	}
	
	/**
	 * <p>Constructor for SecurityObject.</p>
	 *
	 * @param anId a {@link java.lang.String} object.
	 */
	public SecurityObject(String anId){
		this();
		id = anId;
	}
	
	/** {@inheritDoc} */
	@Override public String toString(){
		return "Id: "+id+", attributes: "+attributes;
	}
	
	/**
	 * <p>addAttribute.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
	 */
	public void addAttribute(String name, Object value){
		attributes.put(name, value);
	}
	
	/**
	 * <p>Getter for the field <code>attributes</code>.</p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<String,Object> getAttributes(){
		return attributes;
	}
	
	/**
	 * <p>Getter for the field <code>id</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getId(){
		return id;
	}
}
