package com.diamondq.common.config.core.std;

import com.diamondq.common.config.spi.ConfigDataTuple;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.ConfigSource;
import com.diamondq.common.config.spi.NodeType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ConfigSource based on the Environment Variables
 */
public class EnvironmentalVariablesConfigSource implements ConfigSource {
	private static final Logger sLogger = LoggerFactory.getLogger(EnvironmentalVariablesConfigSource.class);

	/**
	 * Default constructor
	 */
	public EnvironmentalVariablesConfigSource() {
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
		return Collections.emptyMap();
	}

	@Override
	public String getName() {
		return "env";
	}

	@Override
	public List<ConfigDataTuple> getConfiguration(String pEnvironment, List<String> pProfiles) {
		sLogger.trace("getConfiguration({}, {})", pEnvironment, pProfiles);
		Map<String, String> env = System.getenv();

		List<ConfigDataTuple> results = new ArrayList<>();

		StringBuilder prefixBuilder = new StringBuilder();
		if (pEnvironment.isEmpty() == false)
			prefixBuilder.append(formatEnvStr(pEnvironment));

		List<String> profiles = new ArrayList<>(pProfiles);
		profiles.add(0, "");

		int prefixReset = prefixBuilder.length();

		for (String profile : profiles) {

			Properties p = new Properties();

			prefixBuilder.setLength(prefixReset);
			if (profile.isEmpty() == false) {
				if (prefixReset > 0)
					prefixBuilder.append('-');
				prefixBuilder.append(profile);
			}
			String prefix = prefixBuilder.toString();
			if (prefix.isEmpty() == true)
				prefix = null;

			/* Add in all the keys that match the prefix */

			for (Map.Entry<String, String> pair : env.entrySet()) {
				String key = pair.getKey();
				if ((prefix == null) || (key.startsWith(prefix)))
					p.put(key, pair.getValue());
			}

			if (p.isEmpty() == true)
				continue;

			StringBuilder nameBuilder = new StringBuilder();
			nameBuilder.append("env://");
			if (prefix != null)
				nameBuilder.append(prefix).append('-');
			nameBuilder.append("env.properties");

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

			/* Build the tuple */

			ByteArrayInputStream stream = new ByteArrayInputStream(baos.toByteArray());
			ConfigDataTuple t =
				ConfigDataTuple.builder().source(this).name(nameBuilder.toString()).stream(stream).build();
			results.add(t);
		}
		sLogger.trace("getConfiguration(...) -> {}", results);
		return results;
	}

	private String formatEnvStr(String pEnvironment) {
		return pEnvironment;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{name=" + getName() + "}";
	}
}
