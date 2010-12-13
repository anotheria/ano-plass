package net.anotheria.anoplass.api.generic.security;


/**
 * This interface acts as additional parameter to the api methods and annotations, it is useable if you want to use ENUMs instead of Strings.
 * In this case your enums simply have to implement this interface and return a string constant back.
 * @author lrosenberg
 *
 */
public interface ActionNameProvider {
	String getActionName();
}
