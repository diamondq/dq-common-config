package com.diamondq.common.config.core;

import com.diamondq.common.config.builders.ImmutableClassBuilder;
import com.diamondq.common.config.builders.ListClassBuilder;
import com.diamondq.common.config.builders.NoParamConstructorBuilder;
import com.diamondq.common.config.format.properties.PropertiesParser;
import com.diamondq.common.config.model.BootstrapSetupConfigHolder;
import com.diamondq.common.config.spi.BootstrapConfigSourceFactory;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigSourceFactoryFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandardSetup {

	public static List<BootstrapConfigSourceFactory> getStandardBootstrapSources(ConfigSourceFactoryFactory pFactory,
		Collection<String> pFileSuffixes, BootstrapSetupConfigHolder pHolder, String pApplicationId,
		String pApplicationVersion, String pApplicationName) {

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

		/* Add the overall bootstrap config data */

		results.add(new WrappedBootstrapSource(new BootstrapSetupSource(pHolder)));

		/* Add the application data */

		Map<String, String> data = new HashMap<>();
		if (pApplicationId != null)
			data.put("application.id", pApplicationId);
		if (pApplicationName != null)
			data.put("application.name", pApplicationName);
		if (pApplicationVersion != null)
			data.put("application.version", pApplicationVersion);
		results.add(new WrappedBootstrapSource(pFactory.getInMemoryConfigSourceFactory().create(null, data)));

		return results;

	}

	public static Collection<ConfigParser> getStandardParsers() {
		List<ConfigParser> results = new ArrayList<>();
		results.add(new PropertiesParser());
		return results;
	}

	public static List<ConfigClassBuilder> getStandardClassBuilders() {
		List<ConfigClassBuilder> results = new ArrayList<>();
		results.add(new ImmutableClassBuilder());
		results.add(new NoParamConstructorBuilder());
		results.add(new ListClassBuilder());
		return results;
	}

}
