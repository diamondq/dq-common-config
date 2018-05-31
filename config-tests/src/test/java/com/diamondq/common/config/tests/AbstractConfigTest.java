package com.diamondq.common.config.tests;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.core.StandardSetup;
import com.diamondq.common.config.core.impl.BootstrapConfigImpl;
import com.diamondq.common.config.core.std.CoreFactoryFactory;
import com.diamondq.common.config.model.BootstrapSetupConfig;
import com.diamondq.common.config.model.BootstrapSetupConfigHolder;
import com.diamondq.common.config.spi.BootstrapConfigSourceFactory;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigNodeResolver;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigSourceFactoryFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * General config test
 */
public class AbstractConfigTest {

	protected Config buildConfig(String pAppId) {
		BootstrapSetupConfigHolder holder = new BootstrapSetupConfigHolder();
		String env = getEnvironment();
		List<String> profiles = getProfiles();
		List<ConfigParser> parsers = getParsers();
		List<String> extensions =
			parsers.stream().flatMap(p -> p.getFileExtensions().stream()).collect(Collectors.toList());
		List<ConfigClassBuilder> classBuilders = getClassBuilders();
		List<ConfigNodeResolver> nodeResolvers = getNodeResolvers();
		ConfigSourceFactoryFactory factory = getFactoryFactory();
		List<BootstrapConfigSourceFactory> bootstrapSources = getBootstrapSources(factory, extensions, holder, pAppId);
		BootstrapSetupConfig build = BootstrapSetupConfig.builder().environment(env).profiles(profiles)
			.addAllNodeResolvers(nodeResolvers).addAllClassBuilders(classBuilders)
			.addAllBootstrapSources(bootstrapSources).addAllParsers(parsers).build();
		holder.value = build;
		BootstrapConfigImpl impl = new BootstrapConfigImpl(build);
		Locale locale = getLocale();
		impl.setLocale(locale);
		return impl.bootstrapConfig(null);
	}

	private List<ConfigNodeResolver> getNodeResolvers() {
		return new ArrayList<>(StandardSetup.getStandardNodeResolvers());
	}

	protected Locale getLocale() {
		return Locale.ENGLISH;
	}

	protected ConfigSourceFactoryFactory getFactoryFactory() {
		return new CoreFactoryFactory();
	}

	protected List<BootstrapConfigSourceFactory> getBootstrapSources(ConfigSourceFactoryFactory pFactory,
		Collection<String> pExtensions, BootstrapSetupConfigHolder pHolder, String pAppId) {
		return new ArrayList<BootstrapConfigSourceFactory>(
			StandardSetup.getStandardBootstrapSources(pFactory, pExtensions, pHolder, pAppId));
	}

	protected List<ConfigClassBuilder> getClassBuilders() {
		return new ArrayList<>(StandardSetup.getStandardClassBuilders());
	}

	protected String getEnvironment() {
		return "";
	}

	protected List<ConfigParser> getParsers() {
		return new ArrayList<>(StandardSetup.getStandardParsers());
	}

	protected List<String> getProfiles() {
		List<String> profiles = new ArrayList<>();
		profiles.add("test");
		return profiles;
	}
}
