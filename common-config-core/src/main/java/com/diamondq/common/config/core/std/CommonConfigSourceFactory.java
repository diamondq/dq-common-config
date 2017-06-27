package com.diamondq.common.config.core.std;

import com.diamondq.common.config.model.ConfigSourceType;
import com.diamondq.common.config.spi.ConfigSource;
import com.diamondq.common.config.spi.ConfigSourceFactory;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A standard factory that can generate all the basic kinds of ConfigSources
 */
public class CommonConfigSourceFactory implements ConfigSourceFactory {

	private final ConfigSourceType mType;

	/**
	 * Constructor
	 * 
	 * @param pType the type of ConfigSource that this factory will generate
	 */
	public CommonConfigSourceFactory(ConfigSourceType pType) {
		mType = pType;
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigSourceFactory#create(java.lang.String, java.util.Map)
	 */
	@Override
	public ConfigSource create(@Nullable String pArg1, @Nullable Map<String, String> pOtherArgs) {
		switch (mType) {
		case CLASSPATH: {
			if (pArg1 == null)
				throw new IllegalArgumentException();
			return new ClassPathConfigSource(pArg1);
		}
		case ENV: {
			return new EnvironmentalVariablesConfigSource();
		}
		case FILE: {
			if (pArg1 == null)
				throw new IllegalArgumentException();
			return new FileConfigSource(pArg1);
		}
		case INMEMORY: {
			if (pOtherArgs == null)
				throw new IllegalArgumentException();
			return new InMemoryConfigSource(pOtherArgs);
		}
		case SYSPROPS: {
			return new SystemPropertiesConfigSource();
		}
		}
		throw new IllegalArgumentException();
	}

}
