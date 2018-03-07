package com.diamondq.common.config.core.std;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.core.StandardSetup;
import com.diamondq.common.config.core.impl.BootstrapConfigImpl;
import com.diamondq.common.config.model.BootstrapSetupConfig;
import com.diamondq.common.config.model.BootstrapSetupConfig.Builder;
import com.diamondq.common.config.model.BootstrapSetupConfigHolder;
import com.diamondq.common.config.spi.BootstrapConfigSourceFactory;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigNodeResolver;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigSourceFactoryFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * The standard bootstrap algorithm. Used in most cases
 */
public class StandardBootstrap {

	/**
	 * Default constructor
	 */
	public StandardBootstrap() {

	}

	/**
	 * Generate the bootstrap config
	 * 
	 * @return the config
	 */
	public Config bootstrap() {
		BootstrapSetupConfigHolder holder = new BootstrapSetupConfigHolder();
		String[] profiles = getProfiles().split(",");
		List<String> extensions = new ArrayList<>();

		Collection<ConfigParser> parsers = getParsers();
		Collection<ConfigClassBuilder> classBuilders = getClassBuilders();
		Collection<ConfigNodeResolver> nodeResolvers = getNodeResolvers();
		Collection<BootstrapConfigSourceFactory> factories = getBootstrapSources();

		for (ConfigParser p : parsers)
			extensions.addAll(p.getFileExtensions());

		List<BootstrapConfigSourceFactory> bootstrapSources = new ArrayList<BootstrapConfigSourceFactory>(
			StandardSetup.getStandardBootstrapSources(getFactoryFactory(), extensions, holder, getAppId()));
		for (BootstrapConfigSourceFactory f : factories)
			bootstrapSources.add(f);

		Builder builder = BootstrapSetupConfig.builder().environment(getEnvironment()).addProfile(profiles);
		builder = builder.addAllNodeResolvers(nodeResolvers).addAllClassBuilders(classBuilders)
			.addAllBootstrapSources(bootstrapSources).addAllParsers(parsers);
		BootstrapSetupConfig build = builder.build();
		holder.value = build;
		BootstrapConfigImpl impl = new BootstrapConfigImpl(build);
		impl.setLocale(getDefaultLocale());
		Set<String> filterSet = new HashSet<>();
		filterSet.add(".application");
		filterSet.add(".web");
		return impl.bootstrapConfig(filterSet);
	}

	protected Collection<BootstrapConfigSourceFactory> getBootstrapSources() {
		return Collections.emptyList();
	}

	protected Collection<ConfigNodeResolver> getNodeResolvers() {
		return StandardSetup.getStandardNodeResolvers();
	}

	protected Collection<ConfigClassBuilder> getClassBuilders() {
		return StandardSetup.getStandardClassBuilders();
	}

	protected Collection<ConfigParser> getParsers() {
		Collection<ConfigParser> results = new ArrayList<>(StandardSetup.getStandardParsers());
		ServiceLoader<ConfigParser> loader = ServiceLoader.load(ConfigParser.class);
		for (ConfigParser cp : loader)
			results.add(cp);
		return results;
	}

	protected String getEnvironment() {
		String prop = System.getProperty("application.environment", null);
		if (prop == null)
			return "";
		return prop;
	}

	protected String getProfiles() {
		String prop = System.getProperty("application.profiles", null);
		if (prop == null)
			return "";
		return prop;
	}

	protected String getAppId() {
		String prop = System.getProperty("application.name", null);
		if (prop == null)
			return "";
		return prop;
	}

	protected Locale getDefaultLocale() {
		return Locale.getDefault();
	}

	protected ConfigSourceFactoryFactory getFactoryFactory() {
		return new CoreFactoryFactory();
	}
}
