package com.diamondq.common.config.core;

import com.diamondq.common.config.format.AbstractStdConfigParser;
import com.diamondq.common.config.model.BootstrapSetupConfig;
import com.diamondq.common.config.model.BootstrapSetupConfigHolder;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigDataTuple;
import com.diamondq.common.config.spi.ConfigNodeResolver;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.ConfigReconstructable;
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

public class BootstrapSetupSource implements ConfigSource {

	private final BootstrapSetupConfigHolder mHolder;

	public BootstrapSetupSource(BootstrapSetupConfigHolder pHolder) {
		mHolder = pHolder;
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

	/**
	 * @see com.diamondq.common.config.spi.ConfigSource#getName()
	 */
	@Override
	public String getName() {
		return "bootstrapSetup";
	}

	private void storeReconstructable(ConfigReconstructable pReconstructable, String pPrefix, int pOffset,
		Properties pProps) {
		StringBuilder sb = new StringBuilder();
		sb.append(pPrefix);
		int len = sb.length();

		/* Type */

		NodeType nodeType = pReconstructable.getReconstructionNodeType();
		if ((nodeType.getType().isPresent() == true) && (nodeType.getType().get().getValue().isPresent() == true)) {
			sb.append(AbstractStdConfigParser.sMETA_KEY);
			sb.append(String.format("%05d", pOffset));
			sb.append('.');
			sb.append(AbstractStdConfigParser.sTYPE_TYPE_KEY);
			pProps.put(sb.toString(), nodeType.getType().get().getValue().get());
			sb.setLength(len);
		}

		if ((nodeType.getFactory().isPresent() == true)
			&& (nodeType.getFactory().get().getValue().isPresent() == true)) {
			sb.append(AbstractStdConfigParser.sMETA_KEY);
			sb.append(String.format("%05d", pOffset));
			sb.append('.');
			sb.append(AbstractStdConfigParser.sTYPE_FACTORY_KEY);
			pProps.put(sb.toString(), nodeType.getFactory().get().getValue().get());
			sb.setLength(len);
		}

		if ((nodeType.getFactoryArg().isPresent() == true)
			&& (nodeType.getFactoryArg().get().getValue().isPresent() == true)) {
			sb.append(AbstractStdConfigParser.sMETA_KEY);
			sb.append(String.format("%05d", pOffset));
			sb.append('.');
			sb.append(AbstractStdConfigParser.sTYPE_FACTORY_ARG_KEY);
			pProps.put(sb.toString(), nodeType.getFactoryArg().get().getValue().get());
			sb.setLength(len);
		}

		/* Properties */

		Map<String, String> params = pReconstructable.getReconstructionParams();
		for (Map.Entry<String, String> pair : params.entrySet()) {
			sb.append(String.format("%05d", pOffset));
			sb.append('.');
			sb.append(pair.getKey());
			pProps.put(sb.toString(), pair.getValue());
			sb.setLength(len);
		}
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigSource#getConfiguration(java.lang.String, java.util.List)
	 */
	@Override
	public List<ConfigDataTuple> getConfiguration(String pEnvironment, List<String> pProfiles) {

		/* Now write the properties out to a stream */

		Properties p = new Properties();

		BootstrapSetupConfig config = mHolder.value;

		/* Environment */

		String env = config.getEnvironment();
		p.put("bootstrap.environment", env);

		/* Profiles */

		List<String> profiles = config.getProfiles();
		int offset = 0;
		for (String profile : profiles)
			p.put(String.format("bootstrap.profiles.%05d", offset++), profile);

		/* Parsers */

		List<ConfigParser> parsers = config.getParsers();
		offset = 0;
		for (ConfigParser parser : parsers)
			storeReconstructable(parser, "bootstrap.parsers.", offset++, p);

		/* Node Resolvers */

		List<ConfigNodeResolver> nodeResolvers = config.getNodeResolvers();
		offset = 0;
		for (ConfigNodeResolver nodeResolver : nodeResolvers)
			storeReconstructable(nodeResolver, "bootstrap.node-resolvers.", offset++, p);

		/* Class Builders */

		List<ConfigClassBuilder> classBuilders = config.getClassBuilders();
		offset = 0;
		for (ConfigClassBuilder classBuilder : classBuilders)
			storeReconstructable(classBuilder, "bootstrap.class-builders.", offset++, p);

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

		ConfigDataTuple t =
			ConfigDataTuple.builder().source(this).name("bootstrapSetup.properties").stream(stream).build();
		List<ConfigDataTuple> results = new ArrayList<>();
		results.add(t);
		return results;
	}

}
