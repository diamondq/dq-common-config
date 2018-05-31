package com.diamondq.common.config.core.std;

import com.diamondq.common.config.model.ConfigSourceType;
import com.diamondq.common.config.spi.ConfigSource;
import com.diamondq.common.config.spi.ConfigSourceFactory;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * ConfigBuilder that follows the Builder pattern.
 */
public abstract class StdConfigBuilder {

	static final CoreFactoryFactory		sFactory	= new CoreFactoryFactory();

	protected final ConfigSourceFactory	mFactory;

	protected StdConfigBuilder(ConfigSourceFactory pFactory) {
		mFactory = pFactory;
	}

	/**
	 * Builds the ConfigSource
	 *
	 * @return the ConfigSource
	 */
	public abstract ConfigSource build();

	/**
	 * Generates a builder based on type
	 *
	 * @param pArg the type (ie. a string version of the ConfigSourceType enum)
	 * @return the builder
	 */
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
		case DOCKERSECRETS:
			return new DockerSecretsConfigBuilder(sFactory.getDockerSecretsConfigSourceFactory());
		}
		throw new IllegalStateException();
	}

	/**
	 * A File based builder
	 */
	public static class FileStdConfigBuilder extends StdConfigBuilder {

		private @Nullable String mFile;

		FileStdConfigBuilder(ConfigSourceFactory pFactory) {
			super(pFactory);
		}

		/**
		 * Adds the file
		 *
		 * @param pValue the file
		 * @return the builder
		 */
		public FileStdConfigBuilder file(String pValue) {
			mFile = pValue;
			return this;
		}

		/**
		 * @see com.diamondq.common.config.core.std.StdConfigBuilder#build()
		 */
		@Override
		public ConfigSource build() {
			return mFactory.create(mFile, null);
		}

	}

	/**
	 * Classpath ConfigBuilder
	 */
	public static class ClasspathStdConfigBuilder extends StdConfigBuilder {

		private @Nullable String mClassPath;

		ClasspathStdConfigBuilder(ConfigSourceFactory pFactory) {
			super(pFactory);
		}

		/**
		 * Adds the classpath
		 *
		 * @param pValue the classpath
		 * @return the builder
		 */
		public ClasspathStdConfigBuilder classpath(String pValue) {
			mClassPath = pValue;
			return this;
		}

		/**
		 * @see com.diamondq.common.config.core.std.StdConfigBuilder#build()
		 */
		@Override
		public ConfigSource build() {
			return mFactory.create(mClassPath, null);
		}

	}

	/**
	 * The Environment Variables ConfigBuilder
	 */
	public static class EnvStdConfigBuilder extends StdConfigBuilder {

		EnvStdConfigBuilder(ConfigSourceFactory pFactory) {
			super(pFactory);
		}

		/**
		 * @see com.diamondq.common.config.core.std.StdConfigBuilder#build()
		 */
		@Override
		public ConfigSource build() {
			return mFactory.create(null, null);
		}

	}

	/**
	 * The System Properties Config Builder
	 */
	public static class SysStdConfigBuilder extends StdConfigBuilder {

		SysStdConfigBuilder(ConfigSourceFactory pFactory) {
			super(pFactory);
		}

		/**
		 * @see com.diamondq.common.config.core.std.StdConfigBuilder#build()
		 */
		@Override
		public ConfigSource build() {
			return mFactory.create(null, null);
		}

	}

	/**
	 * The Docker Secrets Config Builder
	 */
	public static class DockerSecretsConfigBuilder extends StdConfigBuilder {

		DockerSecretsConfigBuilder(ConfigSourceFactory pFactory) {
			super(pFactory);
		}

		/**
		 * @see com.diamondq.common.config.core.std.StdConfigBuilder#build()
		 */
		@Override
		public ConfigSource build() {
			return mFactory.create(null, null);
		}

	}

	/**
	 * The In-Memory Config Builder
	 */
	public static class InMemoryStdConfigBuilder extends StdConfigBuilder {

		InMemoryStdConfigBuilder(ConfigSourceFactory pFactory) {
			super(pFactory);
		}

		/**
		 * @see com.diamondq.common.config.core.std.StdConfigBuilder#build()
		 */
		@Override
		public ConfigSource build() {
			return mFactory.create(null, null);
		}

	}

}
