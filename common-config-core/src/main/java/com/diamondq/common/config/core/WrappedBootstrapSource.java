package com.diamondq.common.config.core;

import com.diamondq.common.config.spi.BootstrapConfigSourceFactory;
import com.diamondq.common.config.spi.ConfigSource;

import java.util.List;

public class WrappedBootstrapSource implements BootstrapConfigSourceFactory {

	private final ConfigSource mSource;

	public WrappedBootstrapSource(ConfigSource pSource) {
		mSource = pSource;
	}

	/**
	 * @see com.diamondq.common.config.spi.BootstrapConfigSourceFactory#getBootstrapPriority()
	 */
	@Override
	public int getBootstrapPriority() {
		return 0;
	}

	/**
	 * @see com.diamondq.common.config.spi.BootstrapConfigSourceFactory#create(java.lang.String, java.util.List)
	 */
	@Override
	public ConfigSource create(String pEnvironment, List<String> pProfiles) {
		return mSource;
	}

}
