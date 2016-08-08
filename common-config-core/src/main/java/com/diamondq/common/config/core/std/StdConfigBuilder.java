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
		case CLASSPATH:
			return new ClasspathStdConfigBuilder(sFactory.getClassPathConfigSourceFactory());
		case ENV:
			return new EnvStdConfigBuilder(sFactory.getEnvironmentalVariablesConfigSourceFactory());
		case INMEMORY:
			return new InMemoryStdConfigBuilder(sFactory.getInMemoryConfigSourceFactory());
		case SYSPROPS:
			return new SysStdConfigBuilder(sFactory.getSystemPropertiesConfigSourceFactory());
		default:
			throw new IllegalStateException();
		}
	}

	public static class FileStdConfigBuilder extends StdConfigBuilder {

		private String mFile;

		public FileStdConfigBuilder(ConfigSourceFactory pFactory) {
			super(pFactory);
		}

		public FileStdConfigBuilder file(String pValue) {
			mFile = pValue;
			return this;
		}

		public ConfigSource build() {
			return mFactory.create(mFile, null);
		}

	}

	public static class ClasspathStdConfigBuilder extends StdConfigBuilder {

		private String mClassPath;

		public ClasspathStdConfigBuilder(ConfigSourceFactory pFactory) {
			super(pFactory);
		}

		public ClasspathStdConfigBuilder classpath(String pValue) {
			mClassPath = pValue;
			return this;
		}

		public ConfigSource build() {
			return mFactory.create(mClassPath, null);
		}

	}

	public static class EnvStdConfigBuilder extends StdConfigBuilder {

		public EnvStdConfigBuilder(ConfigSourceFactory pFactory) {
			super(pFactory);
		}

		public ConfigSource build() {
			return mFactory.create(null, null);
		}

	}

	public static class SysStdConfigBuilder extends StdConfigBuilder {

		public SysStdConfigBuilder(ConfigSourceFactory pFactory) {
			super(pFactory);
		}

		public ConfigSource build() {
			return mFactory.create(null, null);
		}

	}

	public static class InMemoryStdConfigBuilder extends StdConfigBuilder {

		public InMemoryStdConfigBuilder(ConfigSourceFactory pFactory) {
			super(pFactory);
		}

		public ConfigSource build() {
			return mFactory.create(null, null);
		}

	}

}
