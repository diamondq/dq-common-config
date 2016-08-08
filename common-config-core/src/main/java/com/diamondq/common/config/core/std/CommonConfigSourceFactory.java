package com.diamondq.common.config.core.std;

import com.diamondq.common.config.model.ConfigSourceType;
import com.diamondq.common.config.spi.ConfigSource;
import com.diamondq.common.config.spi.ConfigSourceFactory;

import java.util.Map;

public class CommonConfigSourceFactory implements ConfigSourceFactory {

	private final ConfigSourceType mType;

	public CommonConfigSourceFactory(ConfigSourceType pType) {
		mType = pType;
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigSourceFactory#create(java.lang.String, java.util.Map)
	 */
	@Override
	public ConfigSource create(String pArg1, Map<String, String> pOtherArgs) {
		switch (mType) {
		case CLASSPATH:
			return new ClassPathConfigSource(pArg1);
		case ENV:
			return new EnvironmentalVariablesConfigSource();
		case FILE:
			return new FileConfigSource(pArg1);
		case INMEMORY:
			return new InMemoryConfigSource(pOtherArgs);
		case SYSPROPS:
			return new SystemPropertiesConfigSource();
		default:
			throw new IllegalArgumentException();
		}
	}

}
