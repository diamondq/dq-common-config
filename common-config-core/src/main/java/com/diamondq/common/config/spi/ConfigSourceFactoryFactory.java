package com.diamondq.common.config.spi;

public interface ConfigSourceFactoryFactory {

	/**
	 * Returns a factory for classpath based ConfigSource's
	 * 
	 * @return the factory
	 */
	public ConfigSourceFactory getClassPathConfigSourceFactory();

	public ConfigSourceFactory getEnvironmentalVariablesConfigSourceFactory();

	public ConfigSourceFactory getFileConfigSourceFactory();

	public ConfigSourceFactory getSystemPropertiesConfigSourceFactory();

	public ConfigSourceFactory getInMemoryConfigSourceFactory();
}
