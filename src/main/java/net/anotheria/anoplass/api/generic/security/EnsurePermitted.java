package net.anotheria.anoplass.api.generic.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>EnsurePermitted class.</p>
 *
 * @author another
 * @version $Id: $Id
 */
@Retention (RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnsurePermitted {
	String action();
	
}
