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
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;

public class ConfigProducer {

    @Produces
    @Singleton
    public Config getConfig(@Named("config.environment") String pEnvironment, @Named("config.profiles") String pProfiles,
        @Named("config.appid") String pAppId, ConfigSourceFactoryFactory pFactoryFactory, Locale pDefaultLocale,
        Instance<ConfigParser> pParsers, Instance<ConfigClassBuilder> pClassBuilders,
        Instance<BootstrapConfigSourceFactory> pBootstrapSources) {
        BootstrapSetupConfigHolder holder = new BootstrapSetupConfigHolder();
        String[] profiles = pProfiles.split(",");
        List<String> extensions = new ArrayList<>();
        Collection<ConfigParser> orderedParsers = InjectUtils.orderByPriority(pParsers);
        Collection<ConfigClassBuilder> orderedClassBuilders = InjectUtils.orderByPriority(pClassBuilders);
        Collection<BootstrapConfigSourceFactory> orderedFactories = InjectUtils.orderByPriority(pBootstrapSources);
        for (ConfigParser p : orderedParsers)
            extensions.addAll(p.getFileExtensions());

        List<BootstrapConfigSourceFactory> bootstrapSources =
            new ArrayList<BootstrapConfigSourceFactory>(StandardSetup.getStandardBootstrapSources(pFactoryFactory, extensions, holder,
                                                                                                  pAppId, null, null));
        for (BootstrapConfigSourceFactory f : orderedFactories)
            bootstrapSources.add(f);

        Builder builder = BootstrapSetupConfig.builder().environment(pEnvironment).addProfile(profiles);
        builder = builder.addAllClassBuilders(orderedClassBuilders).addAllBootstrapSources(bootstrapSources).addAllParsers(orderedParsers);
        BootstrapSetupConfig build = builder.build();
        holder.value = build;
        BootstrapConfigImpl impl = new BootstrapConfigImpl(build);
        impl.setLocale(pDefaultLocale);
        return impl.bootstrapConfig();
    }

    @Produces
    @Named("config.environment")
    public String getEnvironment() {
        return System.getProperty("config.environment", "");
    }

    @Produces
    @Named("config.profiles")
    public String getProfiles() {
        return System.getProperty("config.profiles", "");
    }

    @Produces
    @Named("config.appid")
    public String getAppId() {
        return System.getProperty("config.appid", "");
    }

    @Produces
    public Locale getDefaultLocale() {
        return Locale.getDefault();
    }

    @Produces
    public ConfigSourceFactoryFactory getFactoryFactory() {
        return new CoreFactoryFactory();
    }
}
