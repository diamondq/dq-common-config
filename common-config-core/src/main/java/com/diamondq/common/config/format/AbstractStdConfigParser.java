package com.diamondq.common.config.format;

import com.diamondq.common.config.spi.ConfigNode;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.NodeType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractStdConfigParser implements ConfigParser {

	public static final String	sMETA_KEY				= "_dqconfig_meta_";

	public static final String	sLIST_KEY				= "_dqconfig_list_";

	public static final String	sTYPE_FACTORY_KEY		= "factory";

	public static final String	sTYPE_FACTORY_ARG_KEY	= "arg";

	public static final String	sTYPE_TYPE_KEY			= "type";

	protected ConfigNode map(String pSourceName, String pName, Object o) {
		ConfigNode.Builder builder = ConfigNode.builder().name(pName);
		if (o instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) o;
			Optional<ConfigProp> type = Optional.empty();
			Optional<ConfigProp> factory = Optional.empty();
			Optional<ConfigProp> factoryArg = Optional.empty();
			for (Map.Entry<String, Object> pair : map.entrySet()) {

				String key = pair.getKey();

				/* Check if it's a meta indicator */

				if (sMETA_KEY.equals(key)) {
					Object metaObj = pair.getValue();
					if (metaObj instanceof Map) {
						@SuppressWarnings("unchecked")
						Map<String, Object> metaMap = (Map<String, Object>) metaObj;

						/* Store all the meta data into the meta portion of the node builder */

						for (Map.Entry<String, Object> metaPair : metaMap.entrySet()) {
							Object metaValueObj = metaPair.getValue();
							if ((metaValueObj instanceof Map) || (metaValueObj instanceof List))
								throw new IllegalStateException();
							String metaKey = metaPair.getKey();
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
					else
						throw new IllegalStateException();
				}
				else
					builder.putChildren(pair.getKey(), map(pSourceName, pair.getKey(), pair.getValue()));
			}
			builder = builder.type(NodeType.builder().isExplicitType(type.isPresent()).type(type).factory(factory)
				.factoryArg(factoryArg).build());

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

				String offsetStr = String.format("%05d", offset);
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

}
