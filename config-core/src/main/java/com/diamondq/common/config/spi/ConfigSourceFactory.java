package com.diamondq.common.config.spi;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A ConfigSourceFactory is responsible for creating a ConfigSource within a given engine.
 */
public interface ConfigSourceFactory {

	/**
	 * Call to create a specific ConfigSource
	 * 
	 * @param pArg1 the first argument (if needed)
	 * @param pOtherArgs the other arguments (if needed)
	 * @return the ConfigSource
	 */
	public ConfigSource create(@Nullable String pArg1, @Nullable Map<String, String> pOtherArgs);

}
