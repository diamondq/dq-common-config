package com.diamondq.common.config.spi;

/**
 * A factory to generate a ConfigSourceFactory
 */
public interface ConfigSourceFactoryFactory {

	/**
	 * Returns a factory for classpath based ConfigSource's
	 * 
	 * @return the factory
	 */
	public ConfigSourceFactory getClassPathConfigSourceFactory();

	/**
	 * Returns a factory for environment variable ConfigSource's
	 * 
	 * @return the factory
	 */
	public ConfigSourceFactory getEnvironmentalVariablesConfigSourceFactory();

	/**
	 * Returns a factory for file based ConfigSource's
	 * 
	 * @return the factory
	 */
	public ConfigSourceFactory getFileConfigSourceFactory();

	/**
	 * Returns a factory for System Properties based ConfigSource's
	 * 
	 * @return the factory
	 */
	public ConfigSourceFactory getSystemPropertiesConfigSourceFactory();

	/**
	 * Returns a factory for In Memory ConfigSource's
	 * 
	 * @return the factory
	 */
	public ConfigSourceFactory getInMemoryConfigSourceFactory();
}
