package com.diamondq.common.config.core.std;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.ConfigKey;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigSource;
import com.diamondq.common.config.spi.ConfigSourceFactoryFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class StdConfigListBuilder {

	private static final XLogger sLogger = XLoggerFactory.getXLogger(StdConfigListBuilder.class);

	public static class ListBuilder {

		private List<ConfigSource> mList;

		public ListBuilder(List<ConfigSource> pList) {
			mList = pList;
		}

		@ConfigKey("*")
		public void set(ConfigSource pValue) {
			mList.add(pValue);
		}

		public List<?> build() {
			return Collections.unmodifiableList(mList);
		}
	}

	public static ListBuilder builder(Config pConfig) {
		sLogger.entry();

		List<ConfigSource> sources = new ArrayList<>();

		@SuppressWarnings("unchecked")
		List<ConfigParser> parsers = pConfig.bind("bootstrap.parsers", List.class);

		String appName = pConfig.bind("application.id", String.class);

		/* Get all the supported extensions */

		Set<String> extensions = new HashSet<>();
		for (ConfigParser cp : parsers)
			extensions.addAll(cp.getFileExtensions());

		ConfigSourceFactoryFactory factory = new CoreFactoryFactory();

		for (String extension : extensions)
			sources.add(factory.getClassPathConfigSourceFactory().create("library." + extension, null));

		/* application.xxx packaged inside the JARs */

		for (String extension : extensions)
			sources.add(factory.getClassPathConfigSourceFactory().create("application." + extension, null));

		/* application.xxx packaged outside the JARs */

		for (String extension : extensions)
			sources.add(factory.getFileConfigSourceFactory().create("application." + extension, null));

		if (appName != null) {

			/* appname.xxx packaged inside the JARs */

			for (String extension : extensions)
				sources.add(factory.getClassPathConfigSourceFactory().create(appName + "." + extension, null));

			/* appname.xxx packaged outside the JARs */

			for (String extension : extensions)
				sources.add(factory.getFileConfigSourceFactory().create(appName + "." + extension, null));
		}

		/* OS Environmental variables */

		sources.add(factory.getEnvironmentalVariablesConfigSourceFactory().create(null, null));

		/* System properties */

		sources.add(factory.getSystemPropertiesConfigSourceFactory().create(null, null));

		return sLogger.exit(new ListBuilder(sources));
	}
}
