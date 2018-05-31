package com.diamondq.common.config.model;

import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigNodeResolver;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigSource;

import java.util.List;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

/**
 * The Immutable definition of the BootstrapConfig
 */
@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
public abstract class AbstractBootstrapConfig {

	/**
	 * Returns the environment
	 * 
	 * @return the environment
	 */
	public abstract String getEnvironment();

	/**
	 * Returns the list of profiles
	 * 
	 * @return the profiles
	 */
	public abstract List<String> getProfiles();

	/**
	 * Returns the config sources
	 * 
	 * @return the sources
	 */
	public abstract List<ConfigSource> getConfigSources();

	/**
	 * Returns the class builders
	 * 
	 * @return the class builders
	 */
	public abstract List<ConfigClassBuilder> getClassBuilders();

	/**
	 * Returns the node resolvers
	 * 
	 * @return the node resolvers
	 */
	public abstract List<ConfigNodeResolver> getNodeResolvers();

	/**
	 * Returns the parsers
	 * 
	 * @return the parsers
	 */
	public abstract List<ConfigParser> getParsers();

}
