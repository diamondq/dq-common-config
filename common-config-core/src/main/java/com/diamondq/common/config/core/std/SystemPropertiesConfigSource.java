package com.diamondq.common.config.core.std;

import com.diamondq.common.config.spi.ConfigDataTuple;
import com.diamondq.common.config.spi.ConfigSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemPropertiesConfigSource implements ConfigSource {
	private static final Logger sLogger = LoggerFactory.getLogger(SystemPropertiesConfigSource.class);

	@Override
	public Map<String, String> getReconstructionDetails() {
		return null;
	}

	@Override
	public String getName() {
		return "sys";
	}

	@Override
	public List<ConfigDataTuple> getConfiguration(String pEnvironment, List<String> pProfiles) {
		sLogger.trace("getConfiguration({}, {})", pEnvironment, pProfiles);

		/* Now write the properties out to a stream */

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			try {
				System.getProperties().store(baos, "");
			}
			finally {
				baos.close();
			}
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		ByteArrayInputStream stream = new ByteArrayInputStream(baos.toByteArray());

		List<ConfigDataTuple> results = new ArrayList<>();
		ConfigDataTuple t = ConfigDataTuple.builder().source(this).name("system.properties").stream(stream).build();
		results.add(t);
		sLogger.trace("getConfiguration(...) -> {}", results);
		return results;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{name=" + getName() + "}";
	}
}
