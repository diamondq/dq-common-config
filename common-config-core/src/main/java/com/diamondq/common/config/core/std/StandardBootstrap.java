package com.diamondq.common.config.core.std;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.core.BootstrapConfigImpl;
import com.diamondq.common.config.core.StandardSetup;
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

public class StandardBootstrap {

	public StandardBootstrap() {

	}

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

	private Collection<BootstrapConfigSourceFactory> getBootstrapSources() {
		return Collections.emptyList();
	}

	private Collection<ConfigNodeResolver> getNodeResolvers() {
		return StandardSetup.getStandardNodeResolvers();
	}

	private Collection<ConfigClassBuilder> getClassBuilders() {
		return StandardSetup.getStandardClassBuilders();
	}

	private Collection<ConfigParser> getParsers() {
		Collection<ConfigParser> results = new ArrayList<>(StandardSetup.getStandardParsers());
		ServiceLoader<ConfigParser> loader = ServiceLoader.load(ConfigParser.class);
		for (ConfigParser cp : loader)
			results.add(cp);
		return results;
	}

	public String getEnvironment() {
		return System.getProperty("application.environment", "");
	}

	public String getProfiles() {
		return System.getProperty("application.profiles", "");
	}

	public String getAppId() {
		return System.getProperty("application.name", "");
	}

	public Locale getDefaultLocale() {
		return Locale.getDefault();
	}

	public ConfigSourceFactoryFactory getFactoryFactory() {
		return new CoreFactoryFactory();
	}
}
