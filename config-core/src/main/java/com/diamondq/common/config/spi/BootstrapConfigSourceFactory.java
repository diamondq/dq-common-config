package com.diamondq.common.config.spi;

import java.util.List;

/**
 * Interface for Bootstrap ConfigSource Factories
 */
public interface BootstrapConfigSourceFactory {

	/**
	 * The priority of this factory
	 * 
	 * @return the priority
	 */
	public int getBootstrapPriority();

	/**
	 * Creates a ConfigSource
	 * 
	 * @param pEnvironment the environment
	 * @param pProfiles the profiles
	 * @return the ConfigSource
	 */
	public ConfigSource create(String pEnvironment, List<String> pProfiles);
}
