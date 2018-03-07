package com.diamondq.common.config.core;

import com.diamondq.common.config.Bootstrap;
import com.diamondq.common.config.Config;
import com.diamondq.common.config.core.impl.ConfigImpl;
import com.diamondq.common.config.model.BootstrapConfig;
import com.diamondq.common.config.model.BootstrapSetupConfig;
import com.diamondq.common.config.spi.ConfigDataTuple;
import com.diamondq.common.config.spi.ConfigNode;
import com.diamondq.common.config.spi.ConfigNodeResolver;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.ConfigSource;
import com.diamondq.common.config.spi.NodeType;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Main implementation of the Bootstrap interface
 */
public class BootstrapConfigImpl implements Bootstrap {

	private static final XLogger		sLogger	= XLoggerFactory.getXLogger(BootstrapConfigImpl.class);

	private final BootstrapSetupConfig	mSetupConfig;

	private volatile Locale				mLocale;

	/**
	 * Default constructor
	 *
	 * @param pSetupConfig the bootstrap config
	 */
	public BootstrapConfigImpl(BootstrapSetupConfig pSetupConfig) {
		mSetupConfig = pSetupConfig;
		Locale locale = pSetupConfig.getDefaultLocale().orElse(Locale.getDefault());
		if (locale == null)
			throw new IllegalStateException("Unable to find a locale.");
		mLocale = locale;
	}

	/**
	 * @see com.diamondq.common.config.Bootstrap#getCurrentBootstrapInterfaceVersion()
	 */
	@Override
	public int getCurrentBootstrapInterfaceVersion() {
		return Bootstrap.CURRENT_INTERFACE_VERSION;
	}

	@Override
	public void setLocale(Locale pLocale) {
		mLocale = pLocale;
	}

	/**
	 * @see com.diamondq.common.config.Bootstrap#bootstrapConfig(java.util.Set)
	 */
	@Override
	public Config bootstrapConfig(@Nullable Set<String> pFilterTo) {
		sLogger.entry();
		try {
			sLogger.debug("Starting bootstrap config");

			/* Get the list of bootstrap profiles */

			List<String> profiles = mSetupConfig.getProfiles();

			/* Get the environment */

			String environment = mSetupConfig.getEnvironment();

			/* Now, get the list of source factories, and sort them */

			List<ConfigSource> sortedSources = mSetupConfig.getBootstrapSources().stream()
				.sorted((a, b) -> a.getBootstrapPriority() - b.getBootstrapPriority())
				.map(t -> t.create(environment, profiles)).collect(Collectors.toList());

			sLogger.trace("Bootstrap Sources: {}", sortedSources);

			/* Now run the query */

			ConfigNode bootstrapProperties = resolve(sortedSources, environment, profiles, mSetupConfig.getParsers(),
				mSetupConfig.getNodeResolvers());
			if (sLogger.isDebugEnabled()) {
				sLogger.debug("Final document:");
				Set<String> f = new HashSet<>();
				f.add(".bootstrap");
				f.add(".secrets");
				DebugUtils.debug("", bootstrapProperties, true, true, f);
			}

			/* Now, bind against the BootstrapConfig */

			ConfigImpl bootstrapConfigImpl = new ConfigImpl(bootstrapProperties, mSetupConfig.getClassBuilders());
			bootstrapConfigImpl.setThreadLocale(mLocale);
			BootstrapConfig bootstrapConfig = bootstrapConfigImpl.bind("bootstrap", BootstrapConfig.class);

			sLogger.debug("Bootstrap Config: {}", bootstrapConfig);

			if (bootstrapConfig == null)
				throw new IllegalArgumentException();

			/*
			 * Next, let's use this information to load the main set of sources
			 */

			String actualEnvironment = bootstrapConfig.getEnvironment();
			List<String> actualProfiles = bootstrapConfig.getProfiles();
			List<ConfigSource> actualSources = bootstrapConfig.getConfigSources();

			/* Now run the actual query */

			ConfigNode finalProperties = resolve(actualSources, actualEnvironment, actualProfiles,
				bootstrapConfig.getParsers(), bootstrapConfig.getNodeResolvers());
			if (sLogger.isDebugEnabled()) {
				sLogger.debug("Final document:");
				DebugUtils.debug("", finalProperties, false, false, pFilterTo);
			}

			ConfigImpl finalConfigImpl = new ConfigImpl(finalProperties, bootstrapConfig.getClassBuilders());
			finalConfigImpl.setThreadLocale(mLocale);
			return LoggerUtils.nonNullExit(sLogger, finalConfigImpl);
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * For each source, we load the ConfigNode's and merge them into the growing set of data
	 *
	 * @param pSortedSources
	 * @param pEnvironment
	 * @param pProfiles
	 * @param pNodeResolvers
	 * @return
	 * @throws IOException
	 */
	private ConfigNode resolve(List<ConfigSource> pSortedSources, String pEnvironment, List<String> pProfiles,
		List<ConfigParser> pConfigParsers, List<ConfigNodeResolver> pNodeResolvers) throws IOException {
		sLogger.entry(pSortedSources, pEnvironment, pProfiles, pConfigParsers, pNodeResolvers);

		/* Define the root node */

		ConfigNode rootNode = ConfigNode.builder().name("").type(NodeType.builder().build()).build();
		boolean firstMerge = true;

		/* For each source in order */

		for (ConfigSource cs : pSortedSources) {

			/* Get the data stream */

			sLogger.trace("Getting configuration from {}", cs);

			List<ConfigDataTuple> dataList = cs.getConfiguration(pEnvironment, pProfiles);

			try {

				for (ConfigDataTuple data : dataList) {

					/* Figure out which parser is able to handle this data */

					Optional<String> mediaType = data.getMediaType();
					String fileName = data.getName();

					ConfigParser parser = null;
					for (ConfigParser p : pConfigParsers) {
						if (p.canParse(mediaType, fileName) == true) {
							parser = p;
							break;
						}
					}

					if (parser == null) {
						String format;
						if (mediaType.isPresent() == true)
							format = I18N.getFormat(mLocale, "bootstrap.noparser.both", mediaType.get(), fileName);
						else
							format = I18N.getFormat(mLocale, "bootstrap.noparser.name", fileName);
						throw new IllegalArgumentException(format);
					}

					/* Parse into a data map */

					sLogger.trace("Parsing with {}", parser.getClass().getSimpleName());
					List<ConfigNode> dataMapList = parser.parse(data);

					if (sLogger.isTraceEnabled()) {
						int offset = 0;
						for (ConfigNode cn : dataMapList) {
							sLogger.trace("Parsed document #{}", offset++);
							DebugUtils.trace(data.getName(), cn);
						}
					}

					/* Merge the data map into the root node */

					for (ConfigNode dataMap : dataMapList) {
						rootNode = recursiveMerge(rootNode, dataMap);

						if ((sLogger.isTraceEnabled()) && (firstMerge == false)) {
							sLogger.trace("Merged document:");
							DebugUtils.trace(data.getName(), rootNode);
						}
						firstMerge = false;
					}
				}

			}
			finally {

				/* Make sure that the stream is closed */

				try {
					for (ConfigDataTuple cdt : dataList)
						cdt.getStream().close();
				}
				catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}

		}

		/* Now, perform any PropertiesResolver */

		for (ConfigNodeResolver cnr : pNodeResolvers) {

			sLogger.trace("Resolving with {}", cnr);

			try {
				rootNode = cnr.resolve(rootNode);
			}
			catch (RuntimeException ex) {
				sLogger.debug("Tree before resolving (for debugging the error):");
				DebugUtils.debug("", rootNode, false, false, null);
				throw ex;
			}
			if (sLogger.isTraceEnabled()) {
				sLogger.trace("Resolved document:");
				DebugUtils.trace("", rootNode);
			}
		}

		return LoggerUtils.nonNullExit(sLogger, rootNode);
	}

	private ConfigNode recursiveMerge(@Nullable ConfigNode pTarget, ConfigNode pSource) {

		/* Take each entry, and merge it into the provided node */

		if (pTarget == null) {

			/*
			 * If the provided node does not have a value under the given key, then simply put the node into that key
			 */

			pTarget = pSource;
		}
		else {

			ConfigNode.Builder builder = ConfigNode.builder().name(pTarget.getName());

			/* Value */

			Optional<ConfigProp> mergeValue = pSource.getValue();
			if (mergeValue.isPresent())
				builder = builder.value(mergeValue);
			else {
				Optional<ConfigProp> childValue = pTarget.getValue();
				if (childValue.isPresent())
					builder = builder.value(childValue);
			}

			/* Type */

			NodeType mergeType = pSource.getType();
			NodeType childType = pTarget.getType();
			if ((mergeType.isExplicitType() == true) || (mergeType.getFactory().isPresent())) {
				NodeType.Builder nodeTypeBuilder = NodeType.builder();

				/* Type */

				if (mergeType.isExplicitType() == true)
					nodeTypeBuilder = nodeTypeBuilder.isExplicitType(true).type(mergeType.getType());
				else if (childType.isExplicitType() == true)
					nodeTypeBuilder = nodeTypeBuilder.isExplicitType(true).type(childType.getType());
				else
					nodeTypeBuilder = nodeTypeBuilder.isExplicitType(false).type(mergeType.getType());

				/* Factory */

				if (mergeType.getFactory().isPresent())
					nodeTypeBuilder = nodeTypeBuilder.factory(mergeType.getFactory());
				else if (childType.getFactory().isPresent())
					nodeTypeBuilder = nodeTypeBuilder.factory(childType.getFactory());

				/* Factory Arg */

				if (mergeType.getFactoryArg().isPresent())
					nodeTypeBuilder = nodeTypeBuilder.factoryArg(mergeType.getFactoryArg());
				else if (childType.getFactoryArg().isPresent())
					nodeTypeBuilder = nodeTypeBuilder.factoryArg(childType.getFactoryArg());

				builder.type(nodeTypeBuilder.build());
			}
			else
				builder.type(pTarget.getType());

			/* Handle the metadata */

			Map<String, ConfigProp> metaData = pSource.getMetaData();
			builder.putAllMetaData(pTarget.getMetaData());
			builder.putAllMetaData(metaData);

			/* Handle the children */

			boolean reset = false;
			ConfigProp mergeKey = pSource.getMetaData().get("merge");
			if ((mergeKey != null) && (mergeKey.getValue().isPresent() == true)) {
				String mergeValueStr = mergeKey.getValue().get();
				if ("reset".equals(mergeValueStr))
					reset = true;
			}
			Map<String, ConfigNode> childChildren;
			Map<String, ConfigNode> mergeChildren = pSource.getChildren();
			if (reset == true)
				childChildren = Collections.emptyMap();
			else
				childChildren = new HashMap<>(pTarget.getChildren());

			for (Map.Entry<String, ConfigNode> mergePair : mergeChildren.entrySet()) {
				String mergeChildKey = mergePair.getKey();
				ConfigNode targetChildNode = childChildren.remove(mergeChildKey);
				ConfigNode mergeChildNode = mergePair.getValue();
				if (targetChildNode == null)
					builder.putChildren(mergeChildKey, mergeChildNode);
				else
					builder.putChildren(mergeChildKey, recursiveMerge(targetChildNode, mergeChildNode));
			}

			/* Add any remanining children directly */

			builder.putAllChildren(childChildren);

			pTarget = builder.build();
		}

		return pTarget;
	}

}
