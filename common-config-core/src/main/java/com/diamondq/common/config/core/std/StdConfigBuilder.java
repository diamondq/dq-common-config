package com.diamondq.common.config.core.std;

import com.diamondq.common.config.model.ConfigSourceType;
import com.diamondq.common.config.spi.ConfigSource;
import com.diamondq.common.config.spi.ConfigSourceFactory;

public abstract class StdConfigBuilder {

	public static final CoreFactoryFactory	sFactory	= new CoreFactoryFactory();

	protected final ConfigSourceFactory		mFactory;

	public StdConfigBuilder(ConfigSourceFactory pFactory) {
		mFactory = pFactory;
	}

	public static StdConfigBuilder builder(String pArg) {

		ConfigSourceType type = ConfigSourceType.valueOf(pArg);
		switch (type) {
		case FILE:
			return new FileStdConfigBuilder(sFactory.getFileConfigSourceFactory());
		default:
			throw new IllegalArgumentException();
		}
	}

	public static class FileStdConfigBuilder extends StdConfigBuilder {

		private String mFile;

		public FileStdConfigBuilder(ConfigSourceFactory pFactory) {
			super(pFactory);
		}

		public FileStdConfigBuilder file(String pFile) {
			mFile = pFile;
			return this;
		}

		@Override
		public ConfigSource build() {
			return mFactory.create(mFile, null);
		}

	}

	public abstract ConfigSource build();
}
