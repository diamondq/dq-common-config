package com.diamondq.common.config.model;

import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigNodeResolver;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigSource;

import java.util.List;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
public abstract class AbstractBootstrapConfig {

	public abstract String getEnvironment();

	public abstract List<String> getProfiles();

	public abstract List<ConfigSource> getConfigSources();

	public abstract List<ConfigClassBuilder> getClassBuilders();

	public abstract List<ConfigNodeResolver> getNodeResolvers();

	public abstract List<ConfigParser> getParsers();

}
