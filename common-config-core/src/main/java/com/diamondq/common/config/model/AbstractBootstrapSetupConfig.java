package com.diamondq.common.config.model;

import com.diamondq.common.config.spi.BootstrapConfigSourceFactory;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigNodeResolver;
import com.diamondq.common.config.spi.ConfigParser;

import java.util.List;
import java.util.Set;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
public abstract class AbstractBootstrapSetupConfig {

	public abstract String getEnvironment();

	public abstract List<String> getProfiles();

	public abstract Set<BootstrapConfigSourceFactory> getBootstrapSources();

	public abstract List<ConfigNodeResolver> getNodeResolvers();

	public abstract List<ConfigParser> getParsers();

	public abstract List<ConfigClassBuilder> getClassBuilders();
}
