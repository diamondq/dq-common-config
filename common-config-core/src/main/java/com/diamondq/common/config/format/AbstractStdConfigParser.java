package com.diamondq.common.config.format;

import com.diamondq.common.config.spi.ConfigNode;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.NodeType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract base for different format parsers
 */
public abstract class AbstractStdConfigParser implements ConfigParser {

	/**
	 * The main meta key for any given object. It may be used as a prefix when attached to a Map.
	 */
	public static final String							sMETA_KEY				= "_dqconfig_meta_";

	/**
	 * The key for meta information about the list itself.
	 */
	public static final String							sLIST_KEY				= "_dqconfig_list_";

	/**
	 * The constant representing the factory to use
	 */
	public static final String							sTYPE_FACTORY_KEY		= "factory";

	/**
	 * The constant representing the argument to the factory to provide
	 */
	public static final String							sTYPE_FACTORY_ARG_KEY	= "arg";

	/**
	 * The constant representing the type to use
	 */
	public static final String							sTYPE_TYPE_KEY			= "type";

	private static final ConcurrentMap<String, String>	sPrefixMap				= new ConcurrentHashMap<>();

	private static final AtomicInteger					sPrefixCount			= new AtomicInteger(0);

	private static class MetaInfo {
		Optional<ConfigProp>	type		= Optional.empty();

		Optional<ConfigProp>	factory		= Optional.empty();

		Optional<ConfigProp>	factoryArg	= Optional.empty();

		Map<String, ConfigProp>	meta		= new HashMap<>();
	}

	private MetaInfo resolveMeta(Object metaObj, String pSourceName) {
		if (metaObj instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> metaMap = (Map<String, Object>) metaObj;

			/* Store all the meta data into the meta portion of the node builder */

			MetaInfo result = new MetaInfo();
			for (Map.Entry<String, Object> metaPair : metaMap.entrySet()) {
				Object metaValueObj = metaPair.getValue();
				if ((metaValueObj instanceof Map) || (metaValueObj instanceof List))
					throw new IllegalStateException();
				String metaKey = metaPair.getKey();
				if (metaKey == null)
					continue;
				if (sTYPE_FACTORY_KEY.equals(metaKey))
					result.factory = Optional
						.of(ConfigProp.builder().configSource(pSourceName).value(metaValueObj.toString()).build());
				else if (sTYPE_FACTORY_ARG_KEY.equals(metaKey))
					result.factoryArg = Optional
						.of(ConfigProp.builder().configSource(pSourceName).value(metaValueObj.toString()).build());
				else if (sTYPE_TYPE_KEY.equals(metaKey))
					result.type = Optional
						.of(ConfigProp.builder().configSource(pSourceName).value(metaValueObj.toString()).build());
				else
					result.meta.put(metaKey,
						ConfigProp.builder().configSource(pSourceName).value(metaValueObj.toString()).build());
			}

			return result;
		}
		else
			throw new IllegalStateException();
	}

	protected ConfigNode map(String pSourceName, String pName, Object o) {
		String prefix = sPrefixMap.get(pSourceName);
		if (prefix == null) {
			String newPrefix = String.format("%03d", sPrefixCount.incrementAndGet());
			if ((prefix = sPrefixMap.putIfAbsent(pSourceName, newPrefix)) == null)
				prefix = newPrefix;
		}
		ConfigNode.Builder builder = ConfigNode.builder().name(pName);
		if (o instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) o;
			MetaInfo parentMeta = new MetaInfo();
			Map<String, MetaInfo> pendingMeta = new HashMap<>();
			Map<String, ConfigNode> existingNodes = new HashMap<>();
			for (Map.Entry<String, Object> pair : map.entrySet()) {

				String key = pair.getKey();
				if (key == null)
					continue;

				/* Check if it's a meta indicator */

				if (sMETA_KEY.equals(key)) {
					Object metaObj = pair.getValue();
					parentMeta = resolveMeta(metaObj, pSourceName);
				}
				else if (key.startsWith(sMETA_KEY)) {
					Object metaObj = pair.getValue();
					key = key.substring(sMETA_KEY.length());
					MetaInfo meta = resolveMeta(metaObj, pSourceName);
					ConfigNode childNode = existingNodes.get(key);
					if (childNode != null) {
						childNode = mergeMeta(meta, childNode);
						existingNodes.put(key, childNode);
						builder.putChildren(key, childNode);
					}
					else
						pendingMeta.put(key, meta);
				}
				else {
					ConfigNode childNode = map(pSourceName, key, pair.getValue());
					MetaInfo meta = pendingMeta.remove(key);
					if (meta != null) {
						childNode = childNode.withType(NodeType.builder().isExplicitType(meta.type.isPresent())
							.type(meta.type).factory(meta.factory).factoryArg(meta.factoryArg).build());
					}
					existingNodes.put(key, childNode);
					builder.putChildren(key, childNode);
				}
			}
			if (pendingMeta.isEmpty() == false) {

				/*
				 * For each of the pending meta data that never found a matching entry, create an 'empty' property and
				 * hopefully it'll merge into the 'real' one in a later config source
				 */

				for (Map.Entry<String, MetaInfo> pair : pendingMeta.entrySet()) {

					String key = pair.getKey();
					if (key == null)
						continue;
					ConfigNode.Builder emptyBuilder = ConfigNode.builder().name(key);
					emptyBuilder = emptyBuilder.type(NodeType.builder().isExplicitType(false)
						.type(ConfigProp.builder().configSource(pSourceName).value(String.class.getName()).build())
						.build());

					emptyBuilder =
						emptyBuilder.value(ConfigProp.builder().configSource(pSourceName).value(o.toString()).build());
					ConfigNode emptyNode = emptyBuilder.build();
					MetaInfo emptyMeta = pair.getValue();
					emptyNode = mergeMeta(emptyMeta, emptyNode);
					builder.putChildren(key, emptyNode);
				}
			}
			builder = builder.type(NodeType.builder().isExplicitType(parentMeta.type.isPresent()).type(parentMeta.type)
				.factory(parentMeta.factory).factoryArg(parentMeta.factoryArg).build());

		}
		else if (o instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) o;
			Optional<ConfigProp> type = Optional.empty();
			Optional<ConfigProp> factory = Optional.empty();
			Optional<ConfigProp> factoryArg = Optional.empty();
			int offset = 0;
			for (Object c : list) {

				if (offset == 0) {
					if (c instanceof Map) {
						@SuppressWarnings("unchecked")
						Map<String, Object> metaListMap = (Map<String, Object>) c;
						Object metaMapObj = metaListMap.get(sLIST_KEY);
						if (metaMapObj != null) {

							if (metaMapObj instanceof Map) {
								@SuppressWarnings("unchecked")
								Map<String, Object> metaMap = (Map<String, Object>) metaMapObj;

								/* Store all the meta data into the meta portion of the node builder */

								for (Map.Entry<String, Object> metaPair : metaMap.entrySet()) {
									Object metaValueObj = metaPair.getValue();
									if ((metaValueObj instanceof Map) || (metaValueObj instanceof List))
										throw new IllegalStateException();

									String metaKey = metaPair.getKey();
									if (metaKey == null)
										continue;
									if (sTYPE_FACTORY_KEY.equals(metaKey))
										factory = Optional.of(ConfigProp.builder().configSource(pSourceName)
											.value(metaValueObj.toString()).build());
									else if (sTYPE_FACTORY_ARG_KEY.equals(metaKey))
										factoryArg = Optional.of(ConfigProp.builder().configSource(pSourceName)
											.value(metaValueObj.toString()).build());
									else if (sTYPE_TYPE_KEY.equals(metaKey))
										type = Optional.of(ConfigProp.builder().configSource(pSourceName)
											.value(metaValueObj.toString()).build());
									else
										builder.putMetaData(metaKey, ConfigProp.builder().configSource(pSourceName)
											.value(metaValueObj.toString()).build());
								}

							}

							/* Skip this item since it's attached to it's parent */

							continue;
						}
					}
				}

				String offsetStr = String.format("%s-%05d", prefix, offset);
				builder.putChildren(offsetStr, map(pSourceName, offsetStr, c));
				offset++;
			}
			builder = builder.type(NodeType.builder().isExplicitType(type.isPresent()).type(type).factory(factory)
				.factoryArg(factoryArg).build());
		}
		else {
			if (o instanceof Boolean) {
				builder = builder.type(NodeType.builder().isExplicitType(true)
					.type(ConfigProp.builder().configSource(pSourceName).value(Boolean.class.getName()).build())
					.build());
			}
			else
				builder = builder.type(NodeType.builder().isExplicitType(false)
					.type(ConfigProp.builder().configSource(pSourceName).value(String.class.getName()).build())
					.build());

			builder = builder.value(ConfigProp.builder().configSource(pSourceName).value(o.toString()).build());

		}
		return builder.build();
	}

	private ConfigNode mergeMeta(MetaInfo pMeta, ConfigNode pChildNode) {
		if ((pMeta.type.isPresent()) || (pMeta.factory.isPresent()) || (pMeta.factoryArg.isPresent()))
			pChildNode = pChildNode.withType(NodeType.builder().isExplicitType(pMeta.type.isPresent()).type(pMeta.type)
				.factory(pMeta.factory).factoryArg(pMeta.factoryArg).build());
		if (pMeta.meta.isEmpty() == false) {
			SortedMap<String, ConfigProp> existingMeta = pChildNode.getMetaData();
			Map<String, ConfigProp> newMeta;
			if (existingMeta.isEmpty() == true)
				newMeta = pMeta.meta;
			else {
				newMeta = new HashMap<>();
				newMeta.putAll(existingMeta);
				newMeta.putAll(pMeta.meta);
			}
			pChildNode = pChildNode.withMetaData(newMeta);
		}
		return pChildNode;
	}

}
