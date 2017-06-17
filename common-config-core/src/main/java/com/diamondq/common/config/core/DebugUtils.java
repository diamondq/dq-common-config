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

	public static void debug(String pRootSource, ConfigNode pValue, boolean pSkipEnv, boolean pSkipProperties,
		Set<String> pFilterTo) {

		if (sLogger.isDebugEnabled() == false)
			return;

		StringBuilder info = new StringBuilder();
		Set<String> skip = new HashSet<>();
		if (pSkipEnv == true) {
			for (String k : System.getenv().keySet())
				skip.add("." + k);
			info.append("environmental variables");
		}
		if (pSkipProperties == true) {
			@SuppressWarnings({"cast", "unchecked", "rawtypes"})
			Set<String> sysprops = (Set<String>) (Set) System.getProperties().keySet();
			for (String k : sysprops)
				skip.add("." + k);
			if (info.length() > 0)
				info.append(", ");
			info.append("system properties");
		}

		/* Remove some special ones */

		for (Iterator<String> i = skip.iterator(); i.hasNext();)
			if (i.next().startsWith(".application."))
				i.remove();

		if ((pFilterTo != null) && (pFilterTo.isEmpty() == false)) {
			if (info.length() > 0)
				info.append(", ");
			info.append("specific entries");
		}
		if (info.length() > 0)
			sLogger.debug("Filtering config to skip {}", info.toString());
		sLogger.debug(recursiveDebug(skip, pFilterTo, "", pRootSource, pValue));

	}

	public static void trace(String pRootSource, ConfigNode pValue) {

		if (sLogger.isTraceEnabled() == false)
			return;

		sLogger.trace(recursiveDebug(Collections.emptySet(), Collections.emptySet(), "", pRootSource, pValue));

	}

	private static String recursiveDebug(Set<String> pSkipSet, Set<String> pFilterTo, String pPrefix,
		String pRootSource, ConfigNode pValue) {

		/* Value */

		String valueSource;
		Optional<ConfigProp> value = pValue.getValue();
		StringBuilder valueBuilder = new StringBuilder();
		if (value.isPresent() == true) {
			ConfigProp redactProp = pValue.getMetaData().get("redact");
			boolean redact;
			if ((redactProp != null) && (redactProp.getValue().isPresent() == true)
				&& ("true".equalsIgnoreCase(redactProp.getValue().get())))
				redact = true;
			else
				redact = false;
			valueSource = writeProp(pRootSource, valueBuilder, value.get(), redact);
		}
		else
			valueSource = pRootSource;

		StringBuilder typeBuilder = new StringBuilder();
		NodeType type = pValue.getType();
		boolean useType = false;
		if (type.isExplicitType() == true) {
			useType = true;
			typeBuilder.append('[');
			if (type.getType().isPresent()) {
				writeProp(valueSource, typeBuilder, type.getType().get(), false);
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
			writeProp(valueSource, typeBuilder, type.getFactory().get(), false);
		}
		if (type.getFactoryArg().isPresent()) {
			if (useType == false) {
				useType = true;
				typeBuilder.append('[');
			}
			else
				typeBuilder.append(',');
			typeBuilder.append("arg=");
			writeProp(valueSource, typeBuilder, type.getFactoryArg().get(), false);
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
			writeProp(valueSource, metaBuilder, metaPair.getValue(), false);
		}
		if (useMeta == true)
			metaBuilder.append('}');
		String escapedName = pValue.getName().replaceAll("\\\\", "\\\\").replaceAll("\\.", "\\\\.");
		String fullName = pPrefix + escapedName;
		if (pSkipSet.contains(fullName) == false) {

			if ((pFilterTo != null) && (pFilterTo.isEmpty() == false)) {
				boolean match = false;
				for (String ft : pFilterTo)
					if ((fullName.isEmpty() == true) || (fullName.startsWith(ft) == true)) {
						match = true;
						break;
					}
				if (match == false)
					return null;
			}

			StringBuilder sb = null;

			/* Children */

			for (Map.Entry<String, ConfigNode> childPair : pValue.getChildren().entrySet()) {
				String r =
					recursiveDebug(pSkipSet, pFilterTo, pPrefix + escapedName + ".", pRootSource, childPair.getValue());
				if ((r != null) && (r.isEmpty() == false)) {
					if (sb == null) {
						sb = new StringBuilder();
						sb.append(fullName);
						sb.append('=');
						sb.append(typeBuilder.toString());
						sb.append(valueBuilder.toString());
						sb.append(metaBuilder.toString());
						sb.append('\n');
					}
					sb.append(r);
				}
			}

			if ((sb == null)
				&& ((typeBuilder.length() > 0) || (valueBuilder.length() > 0) || (metaBuilder.length() > 0))) {
				sb = new StringBuilder();
				sb.append(fullName);
				sb.append('=');
				sb.append(typeBuilder.toString());
				sb.append(valueBuilder.toString());
				sb.append(metaBuilder.toString());
				sb.append('\n');
			}
			return (sb == null ? null : sb.toString());
		}
		else
			return null;
	}

	private static String writeProp(String pRootSource, StringBuilder pBuilder, ConfigProp p, boolean pRedact) {
		String value = p.getValue().orElse("(NULL)");
		if (pRedact == true)
			pBuilder.append("XXXXXXXXXX");
		else
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
