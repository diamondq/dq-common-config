package com.diamondq.common.config.core.std;

import com.diamondq.common.config.core.impl.LoggerUtils;
import com.diamondq.common.config.spi.ConfigDataTuple;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.ConfigSource;
import com.diamondq.common.config.spi.NodeType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * A ConfigSource based on a Map of key/values. Usually used for testing purposes.
 */
public class InMemoryConfigSource implements ConfigSource {
	private static final XLogger		sLogger	= XLoggerFactory.getXLogger(InMemoryConfigSource.class);

	private final Map<String, String>	mArgs;

	/**
	 * Default constructor
	 * 
	 * @param pArgs the key/values
	 */
	public InMemoryConfigSource(Map<String, String> pArgs) {
		mArgs = pArgs;
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigReconstructable#getReconstructionNodeType()
	 */
	@Override
	public NodeType getReconstructionNodeType() {
		return NodeType.builder().isExplicitType(true)
			.type(ConfigProp.builder().configSource("").value(getClass().getName()).build()).build();
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigReconstructable#getReconstructionParams()
	 */
	@Override
	public Map<String, String> getReconstructionParams() {
		return mArgs;
	}

	@Override
	public String getName() {
		return "inmemory";
	}

	@Override
	public List<ConfigDataTuple> getConfiguration(String pEnvironment, List<String> pProfiles) {
		sLogger.entry(pEnvironment, pProfiles);

		Properties p = new Properties();
		p.putAll(mArgs);

		/* Now write the properties out to a stream */

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			try {
				p.store(baos, "");
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
		return LoggerUtils.nonNullExit(sLogger, results);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{name=" + getName() + "}";
	}
}
