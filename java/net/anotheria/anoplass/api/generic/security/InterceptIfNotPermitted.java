package net.anotheria.anoplass.api.generic.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention (RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InterceptIfNotPermitted {
	String action();

	Class<? extends SecurityInvocationHandler> handler() default ReturnFalseOrNullIfDeniedInvocationHandler.class;
}
