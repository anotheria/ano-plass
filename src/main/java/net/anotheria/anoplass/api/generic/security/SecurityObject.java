package net.anotheria.anoplass.api.generic.security;

import java.util.HashMap;
import java.util.Map;

public class SecurityObject {
	private String id;
	private Map<String, Object> attributes;

	public SecurityObject(){
		attributes = new HashMap<String, Object>();
	}
	
	public SecurityObject(String anId){
		this();
		id = anId;
	}
	
	@Override public String toString(){
		return "Id: "+id+", attributes: "+attributes;
	}
	
	public void addAttribute(String name, Object value){
		attributes.put(name, value);
	}
	
	public Map<String,Object> getAttributes(){
		return attributes;
	}
	
	public String getId(){
		return id;
	}
}
