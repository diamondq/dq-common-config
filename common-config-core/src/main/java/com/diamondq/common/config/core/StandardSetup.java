package com.diamondq.common.config.core;

import com.diamondq.common.config.builders.ImmutableClassBuilder;
import com.diamondq.common.config.format.properties.PropertiesParser;
import com.diamondq.common.config.spi.BootstrapConfigSourceFactory;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigSourceFactoryFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StandardSetup {

	public static Iterable<? extends BootstrapConfigSourceFactory> getStandardBootstrapSources(
		ConfigSourceFactoryFactory pFactory, Collection<String> pFileSuffixes) {

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

		return results;

	}

	public static Collection<ConfigParser> getStandardParsers() {
		List<ConfigParser> results = new ArrayList<>();
		results.add(new PropertiesParser());
		return results;
	}

	public static Collection<ConfigClassBuilder> getStandardClassBuilders() {
		List<ConfigClassBuilder> results = new ArrayList<>();
		results.add(new ImmutableClassBuilder());
		return results;
	}

}
