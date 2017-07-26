package com.diamondq.common.config.core.std;

import com.diamondq.common.config.model.ConfigSourceType;
import com.diamondq.common.config.spi.ConfigSourceFactory;
import com.diamondq.common.config.spi.ConfigSourceFactoryFactory;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

/**
 * A factory for the ConfigSourceFactory.
 */
@ApplicationScoped
@Alternative
@Priority(10)
public class CoreFactoryFactory implements ConfigSourceFactoryFactory {

	private final ConfigSourceFactory	mClassPathFactory;

	private final ConfigSourceFactory	mEnvFactory;

	private final ConfigSourceFactory	mFileFactory;

	private final ConfigSourceFactory	mSysPropsFactory;

	private final ConfigSourceFactory	mInMemFactory;

	/**
	 * Default constructor
	 */
	public CoreFactoryFactory() {
		mClassPathFactory = new CommonConfigSourceFactory(ConfigSourceType.CLASSPATH);
		mEnvFactory = new CommonConfigSourceFactory(ConfigSourceType.ENV);
		mFileFactory = new CommonConfigSourceFactory(ConfigSourceType.FILE);
		mSysPropsFactory = new CommonConfigSourceFactory(ConfigSourceType.SYSPROPS);
		mInMemFactory = new CommonConfigSourceFactory(ConfigSourceType.INMEMORY);
	}

	@Override
	public ConfigSourceFactory getClassPathConfigSourceFactory() {
		return mClassPathFactory;
	}

	@Override
	public ConfigSourceFactory getEnvironmentalVariablesConfigSourceFactory() {
		return mEnvFactory;
	}

	@Override
	public ConfigSourceFactory getFileConfigSourceFactory() {
		return mFileFactory;
	}

	@Override
	public ConfigSourceFactory getSystemPropertiesConfigSourceFactory() {
		return mSysPropsFactory;
	}

	@Override
	public ConfigSourceFactory getInMemoryConfigSourceFactory() {
		return mInMemFactory;
	}
}
