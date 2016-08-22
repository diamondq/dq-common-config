package com.diamondq.common.config.core;

import com.diamondq.common.config.spi.ConfigNode;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.NodeType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugUtils {

	private static final Logger sLogger = LoggerFactory.getLogger(DebugUtils.class);

	public static void debug(String pRootSource, ConfigNode pValue) {

		if (sLogger.isDebugEnabled() == false)
			return;

		Set<String> skip = new HashSet<>();
		for (String k : System.getenv().keySet())
			skip.add("." + k);
		@SuppressWarnings({"cast", "unchecked", "rawtypes"})
		Set<String> sysprops = (Set<String>) (Set) System.getProperties().keySet();
		for (String k : sysprops)
			skip.add("." + k);

		/* Remove some special ones */

		for (Iterator<String> i = skip.iterator(); i.hasNext();)
			if (i.next().startsWith(".application."))
				i.remove();

		recursiveDebug(true, skip, "", pRootSource, pValue);

	}

	public static void trace(String pRootSource, ConfigNode pValue) {

		if (sLogger.isTraceEnabled() == false)
			return;

		recursiveDebug(false, Collections.emptySet(), "", pRootSource, pValue);

	}

	private static void recursiveDebug(boolean pIsDebug, Set<String> pSkipSet, String pPrefix, String pRootSource,
		ConfigNode pValue) {

		/* Value */

		String valueSource;
		Optional<ConfigProp> value = pValue.getValue();
		StringBuilder valueBuilder = new StringBuilder();
		if (value.isPresent() == true)
			valueSource = writeProp(pRootSource, valueBuilder, value.get());
		else
			valueSource = pRootSource;

		StringBuilder typeBuilder = new StringBuilder();
		NodeType type = pValue.getType();
		boolean useType = false;
		if (type.isExplicitType() == true) {
			useType = true;
			typeBuilder.append('[');
			if (type.getType().isPresent()) {
				writeProp(valueSource, typeBuilder, type.getType().get());
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
			writeProp(valueSource, typeBuilder, type.getFactory().get());
		}
		if (type.getFactoryArg().isPresent()) {
			if (useType == false) {
				useType = true;
				typeBuilder.append('[');
			}
			else
				typeBuilder.append(',');
			typeBuilder.append("arg=");
			writeProp(valueSource, typeBuilder, type.getFactoryArg().get());
		}
		if (useType == true)
			typeBuilder.append(']');

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
			writeProp(valueSource, metaBuilder, metaPair.getValue());
		}
		if (useMeta == true)
			metaBuilder.append('}');
		String escapedName = pValue.getName().replaceAll("\\\\", "\\\\").replaceAll("\\.", "\\\\.");
		String fullName = pPrefix + escapedName;
		if (pSkipSet.contains(fullName) == false) {
			if (pIsDebug)
				sLogger.debug("{}={}{}{}", fullName, typeBuilder.toString(), valueBuilder.toString(),
					metaBuilder.toString());
			else
				sLogger.trace("{}={}{}{}", fullName, typeBuilder.toString(), valueBuilder.toString(),
					metaBuilder.toString());

			/* Children */

			for (Map.Entry<String, ConfigNode> childPair : pValue.getChildren().entrySet()) {
				recursiveDebug(pIsDebug, pSkipSet, pPrefix + escapedName + ".", pRootSource, childPair.getValue());
			}
		}
	}

	private static String writeProp(String pRootSource, StringBuilder pBuilder, ConfigProp p) {
		String value = p.getValue().orElse("(NULL)");
		pBuilder.append(value);
		Optional<String> origValue = p.getOriginalValue();
		if ((origValue.isPresent() == true) && (origValue.get().equals(value) == false))
			pBuilder.append("<--").append(origValue.get());
		String ps = p.getConfigSource();
		if (pRootSource.equals(ps) == false)
			pBuilder.append('(').append(ps).append(')');

		return ps;
	}

}
