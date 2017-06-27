package com.diamondq.common.config.format.properties;

import com.diamondq.common.config.format.AbstractStdConfigParser;
import com.diamondq.common.config.spi.ConfigDataTuple;
import com.diamondq.common.config.spi.ConfigNode;
import com.diamondq.common.config.spi.ConfigNode.Builder;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.NodeType;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import javax.inject.Singleton;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The properties file format allows meta data and type data to be added via sibling keys.
 * 
 * <pre>
 * parentkey.key=value
 * parentkey._dqconfig_meta_key.factory=com.example.factory
 * parentkey._dqconfig_meta_key.otherMeta=metaValue
 * </pre>
 */
@Singleton
public class PropertiesParser implements ConfigParser {

	protected static final Set<String>	sFileExtensions;

	private static final NodeType		sNodeType	= NodeType.builder().isExplicitType(true)
		.type(ConfigProp.builder().configSource("").value(PropertiesParser.class.getName()).build()).build();

	static {
		Set<String> r = new HashSet<>();
		r.add("props");
		r.add("properties");
		sFileExtensions = Collections.unmodifiableSet(r);
	}

	/**
	 * Default constructor
	 */
	public PropertiesParser() {

	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigParser#getReconstructionNodeType()
	 */
	@Override
	public NodeType getReconstructionNodeType() {
		return sNodeType;
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigParser#getReconstructionParams()
	 */
	@Override
	public Map<String, String> getReconstructionParams() {
		return Collections.emptyMap();
	}

	static class MutableConfigNode {
		public String							name;

		public NodeType							type;

		public @Nullable ConfigProp				value;

		public Map<String, ConfigProp>			metaData;

		public Map<String, MutableConfigNode>	children;

		MutableConfigNode() {
			name = "";
			metaData = new HashMap<>();
			children = new HashMap<>();
			type = NodeType.builder().isExplicitType(false).build();
		}
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigParser#parse(com.diamondq.common.config.spi.ConfigDataTuple)
	 */
	@Override
	public List<ConfigNode> parse(ConfigDataTuple pData) throws IOException {
		InputStream stream = pData.getStream();
		List<ConfigNode> results = new ArrayList<>();
		Properties p = new Properties();
		p.load(stream);

		String configSource = pData.getName();
		MutableConfigNode root = new MutableConfigNode();
		root.name = "";
		@SuppressWarnings({"cast", "unchecked", "rawtypes"})
		Map<String, String> map = (Map<String, String>) (Map) p;
		map.entrySet().stream().map(m -> {
			String[] outputKey;
			String key = m.getKey();
			if (key != null)
				outputKey = key.split("\\.");
			else
				outputKey = null;
			return new AbstractMap.SimpleImmutableEntry<>(outputKey, m.getValue());
		}).forEach(e -> {

			/* Find the MutableConfigNode */

			MutableConfigNode node = root;
			String metaKey = null;
			String[] keys = e.getKey();
			if (keys != null)
				for (String k : keys) {
					if (metaKey != null) {
						if (metaKey.isEmpty() == false)
							throw new IllegalArgumentException("Multi-level meta not supported");
						metaKey = k;
					}
					else {
						if (k.startsWith(AbstractStdConfigParser.sMETA_KEY)) {
							/**
							 * a.b.c = value <br/>
							 * a.b._dqconfig_meta_c.factory = f
							 */
							k = k.substring(AbstractStdConfigParser.sMETA_KEY.length());
							metaKey = "";
						}
						else if (k.equals(AbstractStdConfigParser.sLIST_KEY)) {
							k = k.substring(AbstractStdConfigParser.sMETA_KEY.length());
							metaKey = "";
						}

						MutableConfigNode child = node.children.get(k);
						if (child == null) {
							child = new MutableConfigNode();
							child.name = k;
							node.children.put(k, child);
						}
						node = child;

					}
				}

			/* Now assign the data */

			String value = e.getValue();

			ConfigProp valueProp = ConfigProp.builder().value(value).configSource(configSource).build();
			if (metaKey != null) {
				if (AbstractStdConfigParser.sTYPE_FACTORY_ARG_KEY.equals(metaKey))
					node.type = node.type.withFactoryArg(valueProp);
				else if (AbstractStdConfigParser.sTYPE_FACTORY_KEY.equals(metaKey))
					node.type = node.type.withFactory(valueProp);
				else if (AbstractStdConfigParser.sTYPE_TYPE_KEY.equals(metaKey))
					node.type = node.type.withType(valueProp).withIsExplicitType(true);
				else
					node.metaData.put(metaKey, valueProp);
			}
			else
				node.value = valueProp;

		});

		/* Now freeze the MutableNode's into a ConfigNode */

		results.add(recursiveFreeze(root));
		return results;
	}

	private ConfigNode recursiveFreeze(MutableConfigNode pRoot) {
		Builder builder = ConfigNode.builder();
		builder = builder.name(pRoot.name).type(pRoot.type);
		if (pRoot.value != null)
			builder = builder.value(pRoot.value);
		builder = builder.putAllMetaData(pRoot.metaData);

		for (Map.Entry<String, MutableConfigNode> pair : pRoot.children.entrySet()) {
			String key = pair.getKey();
			if (key == null)
				continue;
			builder = builder.putChildren(key, recursiveFreeze(pair.getValue()));
		}

		return builder.build();
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigParser#canParse(java.util.Optional, java.lang.String)
	 */
	@Override
	public boolean canParse(Optional<String> pMediaType, @Nullable String pFileName) {
		if (pFileName == null)
			return false;
		int offset = pFileName.lastIndexOf('.');
		String suffix = pFileName.substring(offset + 1);
		if (sFileExtensions.contains(suffix))
			return true;
		return false;
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigParser#getFileExtensions()
	 */
	@Override
	public Collection<String> getFileExtensions() {
		return sFileExtensions;
	}
}
