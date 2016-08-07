package com.diamondq.common.config.core.std;

import com.diamondq.common.config.spi.ConfigDataTuple;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.Nonnull;

public class ClassPathConfigSource extends AbstractPathDrivenConfigSource {

	public ClassPathConfigSource(String pPath) {
		super(Paths.get(pPath));
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigSource#getName()
	 */
	@Override
	public String getName() {
		return "classpath:" + mPath.toString();
	}

	@Override
	protected void processPath(Path pPath, List<ConfigDataTuple> pResults) throws IOException {
		@SuppressWarnings("nullness")
		@Nonnull
		ClassLoader classLoader = ClassPathConfigSource.class.getClassLoader();
		Enumeration<URL> urls = classLoader.getResources(pPath.toString());
		for (; urls.hasMoreElements();) {
			URL url = urls.nextElement();
			InputStream stream = url.openStream();
			pResults.add(ConfigDataTuple.builder().name(url.toExternalForm()).source(this).stream(stream).build());
		}
	}

}
