package com.diamondq.common.config.inject;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.core.StandardSetup;
import com.diamondq.common.config.core.impl.BootstrapConfigImpl;
import com.diamondq.common.config.core.std.CoreFactoryFactory;
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
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 * This is a CDI based Producer that will generate a Config given some injectables
 */
@ApplicationScoped
public class ConfigProducer {

	/**
	 * Retrieves a Config based on a variety of injectables
	 *
	 * @param pEnvironment the named environment
	 * @param pProfiles the named profiles
	 * @param pAppId the named application id
	 * @param pFactoryFactory the factory factory
	 * @param pDefaultLocale the default locale
	 * @param pParsers the set of parsers
	 * @param pClassBuilders the set of class builders
	 * @param pBootstrapSources the set of sources
	 * @param pNodeResolvers the set of resolvers
	 * @return the config
	 */
	@Produces
	@ApplicationScoped
	public Config getConfig(@Named("application.environment") String pEnvironment,
		@Named("application.profiles") String pProfiles, @Named("application.name") String pAppId,
		ConfigSourceFactoryFactory pFactoryFactory, Locale pDefaultLocale, Instance<ConfigParser> pParsers,
		Instance<ConfigClassBuilder> pClassBuilders, Instance<BootstrapConfigSourceFactory> pBootstrapSources,
		Instance<ConfigNodeResolver> pNodeResolvers) {
		BootstrapSetupConfigHolder holder = new BootstrapSetupConfigHolder();
		String[] profiles = pProfiles.split(",");
		List<String> extensions = new ArrayList<>();
		Collection<ConfigParser> orderedParsers = InjectUtils.orderByPriority(pParsers);
		Collection<ConfigClassBuilder> orderedClassBuilders = InjectUtils.orderByPriority(pClassBuilders);
		Collection<ConfigNodeResolver> nodeResolvers = InjectUtils.orderByPriority(pNodeResolvers);

		Collection<BootstrapConfigSourceFactory> orderedFactories = InjectUtils.orderByPriority(pBootstrapSources);
		for (ConfigParser p : orderedParsers)
			extensions.addAll(p.getFileExtensions());

		List<BootstrapConfigSourceFactory> bootstrapSources = new ArrayList<BootstrapConfigSourceFactory>(
			StandardSetup.getStandardBootstrapSources(pFactoryFactory, extensions, holder, pAppId));
		for (BootstrapConfigSourceFactory f : orderedFactories)
			bootstrapSources.add(f);

		Builder builder = BootstrapSetupConfig.builder().environment(pEnvironment).addProfiles(profiles);
		builder = builder.addAllNodeResolvers(nodeResolvers).addAllClassBuilders(orderedClassBuilders)
			.addAllBootstrapSources(bootstrapSources).addAllParsers(orderedParsers);
		BootstrapSetupConfig build = builder.build();
		holder.value = build;
		BootstrapConfigImpl impl = new BootstrapConfigImpl(build);
		impl.setLocale(pDefaultLocale);
		return impl.bootstrapConfig(null);
	}

	/**
	 * Returns the named environment based on the system property "application.environment"
	 *
	 * @return the environment
	 */
	@Produces
	@Named("application.environment")
	public String getEnvironment() {
		String prop = System.getProperty("application.environment", null);
		if (prop == null)
			return "";
		return prop;
	}

	/**
	 * Returns the name profiles based on the system property "application.profiles"
	 *
	 * @return the profiles
	 */
	@Produces
	@Named("application.profiles")
	public String getProfiles() {
		String prop = System.getProperty("application.profiles", null);
		if (prop == null)
			return "";
		return prop;
	}

	/**
	 * Returns the application name based on the system property "application.name"
	 *
	 * @return the name
	 */
	@Produces
	@Named("application.name")
	public String getAppId() {
		String prop = System.getProperty("application.name", null);
		if (prop == null)
			return "";
		return prop;
	}

	/**
	 * Returns the locale default
	 *
	 * @return the locale
	 */
	@Produces
	public Locale getDefaultLocale() {
		return Locale.getDefault();
	}

	/**
	 * Returns the factory factory
	 *
	 * @return the factory factory
	 */
	@Produces
	public ConfigSourceFactoryFactory getFactoryFactory() {
		return new CoreFactoryFactory();
	}
}
