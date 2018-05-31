package com.diamondq.common.config.model;

import com.diamondq.common.config.spi.BootstrapConfigSourceFactory;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigNodeResolver;
import com.diamondq.common.config.spi.ConfigParser;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

/**
 * Immutable model for the setup config
 */
@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
public abstract class AbstractBootstrapSetupConfig {

	/**
	 * Returns the environment
	 * 
	 * @return the environment
	 */
	public abstract String getEnvironment();

	/**
	 * Returns the profiles
	 * 
	 * @return the profiles
	 */
	public abstract List<String> getProfiles();

	/**
	 * Returns the set of bootstrap sources
	 * 
	 * @return the sources
	 */
	public abstract Set<BootstrapConfigSourceFactory> getBootstrapSources();

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

	/**
	 * Returns the class builders
	 * 
	 * @return the class builders
	 */
	public abstract List<ConfigClassBuilder> getClassBuilders();

	/**
	 * Returns the default locale
	 * 
	 * @return the default locale
	 */
	public abstract Optional<Locale> getDefaultLocale();
}
