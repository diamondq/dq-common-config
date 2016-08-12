package com.diamondq.common.config.inject;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.core.BootstrapConfigImpl;
import com.diamondq.common.config.core.StandardSetup;
import com.diamondq.common.config.core.std.CoreFactoryFactory;
import com.diamondq.common.config.model.BootstrapSetupConfig;
import com.diamondq.common.config.model.BootstrapSetupConfig.Builder;
import com.diamondq.common.config.model.BootstrapSetupConfigHolder;
import com.diamondq.common.config.spi.BootstrapConfigSourceFactory;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigSourceFactoryFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

public class ConfigProducer {

	@Produces
	public Config getConfig(@Named("config.environment") String pEnvironment,
		@Named("config.profiles") String pProfiles, @Named("config.appid") String pAppId,
		ConfigSourceFactoryFactory pFactoryFactory, Locale pDefaultLocale) {
		BootstrapSetupConfigHolder holder = new BootstrapSetupConfigHolder();
		String[] profiles = pProfiles.split(",");
		List<ConfigParser> parsers = getParsers();
		List<String> extensions =
			parsers.stream().flatMap(p -> p.getFileExtensions().stream()).collect(Collectors.toList());
		List<ConfigClassBuilder> classBuilders = getClassBuilders();
		List<BootstrapConfigSourceFactory> bootstrapSources = new ArrayList<BootstrapConfigSourceFactory>(
			StandardSetup.getStandardBootstrapSources(pFactoryFactory, extensions, holder, pAppId, null, null));
		bootstrapSources.addAll(getBootstrapSources());

		Builder builder = BootstrapSetupConfig.builder().environment(pEnvironment).addProfile(profiles);
		builder =
			builder.addAllClassBuilders(classBuilders).addAllBootstrapSources(bootstrapSources).addAllParsers(parsers);
		BootstrapSetupConfig build = builder.build();
		holder.value = build;
		BootstrapConfigImpl impl = new BootstrapConfigImpl(build);
		impl.setLocale(pDefaultLocale);
		return impl.bootstrapConfig();
	}

	@Produces
	public ConfigSourceFactoryFactory getFactoryFactory() {
		return new CoreFactoryFactory();
	}
}
