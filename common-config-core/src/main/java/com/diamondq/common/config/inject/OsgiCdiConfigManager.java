package com.diamondq.common.config.inject;

import com.diamondq.common.config.ConfigManager;
import com.diamondq.common.config.core.std.AbstractConfigManager;
import com.diamondq.common.config.spi.BootstrapConfigSourceFactory;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigNodeResolver;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigSourceFactoryFactory;

import java.util.Locale;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.ops4j.pax.cdi.api.SingletonScoped;

@SingletonScoped
@OsgiServiceProvider(classes = {ConfigManager.class})
public class OsgiCdiConfigManager extends AbstractConfigManager {

	@Inject
	public OsgiCdiConfigManager(@Named("application.environment") String pEnvironment,
		@Named("application.profiles") String pProfiles, @Named("application.name") String pAppId,
		ConfigSourceFactoryFactory pFactoryFactory, Locale pDefaultLocale, Instance<ConfigParser> pParsers,
		Instance<ConfigClassBuilder> pClassBuilders, Instance<BootstrapConfigSourceFactory> pBootstrapSources,
		Instance<ConfigNodeResolver> pNodeResolvers) {
		super(pEnvironment, pProfiles.split(","), pAppId, pFactoryFactory, pDefaultLocale, pParsers, pClassBuilders,
			pBootstrapSources, pNodeResolvers);
		System.out.println("OsgiCdiConfigManager constructor");
	}
}
