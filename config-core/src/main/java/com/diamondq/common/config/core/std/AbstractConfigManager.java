package com.diamondq.common.config.core.std;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.ConfigManager;
import com.diamondq.common.config.core.StandardSetup;
import com.diamondq.common.config.core.impl.BootstrapConfigImpl;
import com.diamondq.common.config.inject.InjectUtils;
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

import javax.enterprise.inject.Instance;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class AbstractConfigManager implements ConfigManager {

  protected volatile @Nullable Locale                    mLocale;

  protected final String                                 mDefaultEnvironment;

  protected final @NonNull String[]                      mDefaultProfiles;

  protected final String                                 mDefaultAppId;

  protected final ConfigSourceFactoryFactory             mDefaultFactoryFactory;

  protected final Locale                                 mDefaultLocale;

  protected final Instance<ConfigParser>                 mDefaultParsers;

  protected final Instance<ConfigClassBuilder>           mDefaultClassBuilders;

  protected final Instance<BootstrapConfigSourceFactory> mDefaultBootstrapSources;

  protected final Instance<ConfigNodeResolver>           mDefaultNodeResolvers;

  protected AbstractConfigManager(String pEnvironment, @NonNull
  String[] pProfiles, String pAppId, ConfigSourceFactoryFactory pFactoryFactory, Locale pDefaultLocale,
    Instance<ConfigParser> pParsers, Instance<ConfigClassBuilder> pClassBuilders,
    Instance<BootstrapConfigSourceFactory> pBootstrapSources, Instance<ConfigNodeResolver> pNodeResolvers) {
    mDefaultEnvironment = pEnvironment;
    mDefaultProfiles = pProfiles;
    mDefaultAppId = pAppId;
    mDefaultFactoryFactory = pFactoryFactory;
    mDefaultLocale = pDefaultLocale;
    mDefaultParsers = pParsers;
    mDefaultClassBuilders = pClassBuilders;
    mDefaultBootstrapSources = pBootstrapSources;
    mDefaultNodeResolvers = pNodeResolvers;
  }

  /**
   * @see com.diamondq.common.config.ConfigManager#setLocale(java.util.Locale)
   */
  @Override
  public void setLocale(Locale pLocale) {
    mLocale = pLocale;
  }

  /**
   * @see com.diamondq.common.config.ConfigManager#getConfig()
   */
  @Override
  public Config getConfig() {
    BootstrapSetupConfigHolder holder = new BootstrapSetupConfigHolder();
    List<String> extensions = new ArrayList<>();
    Collection<ConfigParser> orderedParsers = InjectUtils.orderByPriority(mDefaultParsers);
    Collection<ConfigClassBuilder> orderedClassBuilders = InjectUtils.orderByPriority(mDefaultClassBuilders);
    Collection<ConfigNodeResolver> nodeResolvers = InjectUtils.orderByPriority(mDefaultNodeResolvers);

    Collection<BootstrapConfigSourceFactory> orderedFactories = InjectUtils.orderByPriority(mDefaultBootstrapSources);
    for (ConfigParser p : orderedParsers)
      extensions.addAll(p.getFileExtensions());

    List<BootstrapConfigSourceFactory> bootstrapSources = new ArrayList<BootstrapConfigSourceFactory>(
      StandardSetup.getStandardBootstrapSources(mDefaultFactoryFactory, extensions, holder, mDefaultAppId));
    for (BootstrapConfigSourceFactory f : orderedFactories)
      bootstrapSources.add(f);

    Builder builder = BootstrapSetupConfig.builder().environment(mDefaultEnvironment).addProfiles(mDefaultProfiles);
    builder = builder.addAllNodeResolvers(nodeResolvers).addAllClassBuilders(orderedClassBuilders)
      .addAllBootstrapSources(bootstrapSources).addAllParsers(orderedParsers);
    BootstrapSetupConfig build = builder.build();
    holder.value = build;
    BootstrapConfigImpl impl = new BootstrapConfigImpl(build);
    impl.setLocale(mDefaultLocale);
    return impl.bootstrapConfig(null);
  }

}
