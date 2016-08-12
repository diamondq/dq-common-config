package com.diamondq.common.config.core;

import com.diamondq.common.config.spi.ConfigNode;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.NodeType;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugUtils {

	private static final Logger sLogger = LoggerFactory.getLogger(DebugUtils.class);

	public static void debug(String pRootSource, ConfigNode pValue) {

		if (sLogger.isDebugEnabled() == false)
			return;

		recursiveDebug(true, "", pRootSource, pValue);

	}

	public static void trace(String pRootSource, ConfigNode pValue) {

		if (sLogger.isTraceEnabled() == false)
			return;

		recursiveDebug(false, "", pRootSource, pValue);

	}

	private static void recursiveDebug(boolean pIsDebug, String pPrefix, String pRootSource, ConfigNode pValue) {

		StringBuilder typeBuilder = new StringBuilder();
		NodeType type = pValue.getType();
		boolean useType = false;
		if (type.isExplicitType() == true) {
			useType = true;
			typeBuilder.append('[');
			if (type.getType().isPresent()) {
				writeProp(pRootSource, typeBuilder, type.getType().get());
			}
		}
		if (type.getFactory().isPresent()) {
			if (useType == false) {
				useType = true;
				typeBuilder.append('[');
			}
			else
				typeBuilder.append(',');
			typeBuilder.append("factory=");
			writeProp(pRootSource, typeBuilder, type.getFactory().get());
		}
		if (type.getFactoryArg().isPresent()) {
			if (useType == false) {
				useType = true;
				typeBuilder.append('[');
			}
			else
				typeBuilder.append(',');
			typeBuilder.append("arg=");
			writeProp(pRootSource, typeBuilder, type.getFactoryArg().get());
		}
		if (useType == true)
			typeBuilder.append(']');

		/* Value */

		Optional<ConfigProp> value = pValue.getValue();
		StringBuilder valueBuilder = new StringBuilder();
		if (value.isPresent() == true) {
			writeProp(pRootSource, valueBuilder, value.get());
		}

		/* Metadata */

		StringBuilder metaBuilder = new StringBuilder();
		boolean useMeta = false;
		for (Map.Entry<String, ConfigProp> metaPair : pValue.getMetaData().entrySet()) {
			if (useMeta == false) {
				useMeta = true;
				metaBuilder.append('{');
			}
			else
				metaBuilder.append(',');
			metaBuilder.append(metaPair.getKey()).append("=");
			writeProp(pRootSource, metaBuilder, metaPair.getValue());
		}
		if (useMeta == true)
			metaBuilder.append('}');
		if (pIsDebug)
			sLogger.debug("{}{}={}{}{}", pPrefix, pValue.getName(), typeBuilder.toString(), valueBuilder.toString(),
				metaBuilder.toString());
		else
			sLogger.trace("{}{}={}{}{}", pPrefix, pValue.getName(), typeBuilder.toString(), valueBuilder.toString(),
				metaBuilder.toString());

		/* Children */

		for (Map.Entry<String, ConfigNode> childPair : pValue.getChildren().entrySet()) {
			recursiveDebug(pIsDebug, pPrefix + pValue.getName() + ".", pRootSource, childPair.getValue());
		}
	}

	private static void writeProp(String pRootSource, StringBuilder pBuilder, ConfigProp p) {
		String value = p.getValue().orElse("(NULL)");
		pBuilder.append(value);
		Optional<String> origValue = p.getOriginalValue();
		if ((origValue.isPresent() == true) && (origValue.get().equals(value) == false))
			pBuilder.append("<--").append(origValue.get());
		String ps = p.getConfigSource();
		if (pRootSource.equals(ps) == false)
			pBuilder.append('(').append(ps).append(')');
	}

}