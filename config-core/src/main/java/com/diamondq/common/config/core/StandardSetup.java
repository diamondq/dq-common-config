package com.diamondq.common.config.core;

import com.diamondq.common.config.builders.ImmutableClassBuilder;
import com.diamondq.common.config.builders.ListClassBuilder;
import com.diamondq.common.config.builders.NoParamConstructorBuilder;
import com.diamondq.common.config.format.dockersecrets.DockerSecretsParser;
import com.diamondq.common.config.format.properties.PropertiesParser;
import com.diamondq.common.config.model.BootstrapSetupConfigHolder;
import com.diamondq.common.config.resolver.Resolver;
import com.diamondq.common.config.spi.BootstrapConfigSourceFactory;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigNodeResolver;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigSourceFactoryFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The standard setup. Responsible for building the bootstrap enviroment that is most commonly used
 */
public class StandardSetup {

	/**
	 * Returns a list of bootstrap sources
	 *
	 * @param pFactory the factory
	 * @param pFileSuffixes the file suffixes
	 * @param pHolder the config holder
	 * @param pApplicationId the app id
	 * @return the list of sources
	 */
	public static List<BootstrapConfigSourceFactory> getStandardBootstrapSources(ConfigSourceFactoryFactory pFactory,
		Collection<String> pFileSuffixes, BootstrapSetupConfigHolder pHolder, String pApplicationId) {

		List<BootstrapConfigSourceFactory> results = new ArrayList<>();

		/* bootstrap.xxx packaged inside the JARs */

		for (String suffix : pFileSuffixes)
			results.add(new WrappedBootstrapSource(
				pFactory.getClassPathConfigSourceFactory().create("bootstrap." + suffix, null)));

		/* bootstrap.xxx packaged outside the JARs */

		for (String suffix : pFileSuffixes)
			results.add(
				new WrappedBootstrapSource(pFactory.getFileConfigSourceFactory().create("bootstrap." + suffix, null)));

		/* Add the System properties */

		results.add(new WrappedBootstrapSource(pFactory.getSystemPropertiesConfigSourceFactory().create(null, null)));

		/* Add the environmental variables */

		results.add(
			new WrappedBootstrapSource(pFactory.getEnvironmentalVariablesConfigSourceFactory().create(null, null)));

		/* Add the Docker Secrets */

		results.add(new WrappedBootstrapSource(pFactory.getDockerSecretsConfigSourceFactory().create(null, null)));

		/* Add the overall bootstrap config data */

		results.add(new WrappedBootstrapSource(new BootstrapSetupSource(pHolder)));

		/* Add the application data */

		Map<String, String> data = new HashMap<>();
		data.put("application.name", pApplicationId);
		results.add(new WrappedBootstrapSource(pFactory.getInMemoryConfigSourceFactory().create(null, data)));

		return results;

	}

	/**
	 * Returns the standard set of parsers (Property files)
	 *
	 * @return the list of parsers
	 */
	public static Collection<ConfigParser> getStandardParsers() {
		List<ConfigParser> results = new ArrayList<>();
    results.add(new PropertiesParser());
    results.add(new DockerSecretsParser());
		return results;
	}

	/**
	 * Returns the standard set of class builders. This currently means Immutable classes, No Param Construction
	 * Classes, and List classes.
	 *
	 * @return the list of class builders
	 */
	public static List<ConfigClassBuilder> getStandardClassBuilders() {
		List<ConfigClassBuilder> results = new ArrayList<>();
		results.add(new ImmutableClassBuilder());
		results.add(new NoParamConstructorBuilder());
		results.add(new ListClassBuilder());
		return results;
	}

	/**
	 * Returns the standard set of node resolvers, which currently means the standard resolver
	 *
	 * @return the list of resolvers
	 */
	public static Collection<ConfigNodeResolver> getStandardNodeResolvers() {
		List<ConfigNodeResolver> results = new ArrayList<>();
		results.add(new Resolver());
		return results;
	}

}
