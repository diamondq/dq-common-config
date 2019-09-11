package com.diamondq.common.config;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation that can be attached to a method to indicate that a given config key should be bound there.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ConfigKey {

	String value();

}
