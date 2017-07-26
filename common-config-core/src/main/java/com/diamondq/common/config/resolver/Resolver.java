package com.diamondq.common.config.resolver;

import com.diamondq.common.config.spi.ConfigNode;
import com.diamondq.common.config.spi.ConfigNodeResolver;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.ConfigProp.Builder;
import com.diamondq.common.config.spi.NodeType;
import com.diamondq.common.config.spi.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The generic resolver
 */
@ApplicationScoped
public class Resolver implements ConfigNodeResolver {

	private Pattern					mPattern;

	private static final NodeType	sNodeType	= NodeType.builder().isExplicitType(true)
		.type(ConfigProp.builder().configSource("").value(Resolver.class.getName()).build()).build();

	/**
	 * Default constructor
	 */
	public Resolver() {
		mPattern = Pattern.compile("\\$\\{([^:}]+)(:[^}]*)?\\}");
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigReconstructable#getReconstructionParams()
	 */
	@Override
	public Map<String, String> getReconstructionParams() {
		return Collections.emptyMap();
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigReconstructable#getReconstructionNodeType()
	 */
	@Override
	public NodeType getReconstructionNodeType() {
		return sNodeType;
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigNodeResolver#resolve(com.diamondq.common.config.spi.ConfigNode)
	 */
	@Override
	public ConfigNode resolve(ConfigNode pNode) {
		ConfigNode result = internalResolve(pNode, "", pNode);
		if (result == null)
			return pNode;
		return result;
	}

	private @Nullable ConfigNode internalResolve(ConfigNode pRootNode, String pDiagName, ConfigNode pNode) {

		/* Recursively handle all children */

		Map<String, ConfigNode> replacementChildren = null;
		for (Map.Entry<String, ConfigNode> child : pNode.getChildren().entrySet()) {
			String childKey = child.getKey();
			ConfigNode replaceChild = internalResolve(pRootNode, pDiagName + "." + childKey, child.getValue());
			if (replaceChild != null) {
				if (replacementChildren == null)
					replacementChildren = new HashMap<>(pNode.getChildren());
				replacementChildren.put(childKey, replaceChild);
			}
		}

		ConfigNode replacement = null;

		/* Check to see if there is anything that needs resolving in the current node */

		if (pNode.getValue().isPresent() == true) {
			ConfigProp prop = pNode.getValue().get();
			if (prop.getValue().isPresent() == true) {
				String actualValue = prop.getValue().get();
				Pair<String, Boolean> replacementValue = resolveStr(actualValue, pRootNode, pDiagName, pNode);
				if (replacementValue != null) {
					Builder builder = ConfigProp.builder().from(prop).value(replacementValue._1);
					if (prop.getOriginalValue().isPresent() == false)
						builder = builder.originalValue(actualValue);
					ConfigProp replacementProp = builder.build();

					replacement = pNode.withValue(replacementProp);
					if (replacementValue._2 == true) {
						SortedMap<String, ConfigProp> existingMeta = replacement.getMetaData();
						Map<String, ConfigProp> newMeta = new HashMap<>(existingMeta);
						newMeta.put("redact",
							ConfigProp.builder().configSource(prop.getConfigSource()).value("true").build());
						replacement = replacement.withMetaData(newMeta);
					}
				}
			}
		}

		if (replacementChildren != null)
			replacement = (replacement == null ? pNode : replacement).withChildren(replacementChildren);

		return replacement;
	}

	protected @Nullable Pair<String, Boolean> resolveStr(@Nullable String pValue, ConfigNode pRootNode,
		String pDiagName, ConfigNode pNode) {
		if (pValue != null) {
			Matcher matcher = mPattern.matcher(pValue);
			StringBuilder sb = null;
			int startingPoint = 0;
			boolean redact = false;
			while (matcher.find() == true) {
				if (sb == null)
					sb = new StringBuilder();
				sb.append(pValue.substring(startingPoint, matcher.start()));
				startingPoint = matcher.end();
				String key = matcher.group(1);
				String[] keys = key.split("\\.");
				ConfigNode n = pRootNode;
				for (String k : keys) {
					n = n.getChildren().get(k);
					if (n == null)
						break;
					ConfigProp redactProp = n.getMetaData().get("redact");
					if ((redactProp != null) && (redactProp.getValue().isPresent() == true)
						&& ("true".equalsIgnoreCase(redactProp.getValue().get())))
						redact = true;
				}
				String value = null;
				if (n != null) {
					if ((n.getValue().isPresent() == true) && (n.getValue().get().getValue().isPresent() == true)) {
						value = n.getValue().get().getValue().get();
					}
				}

				if (value == null) {

					/* See if there is a default */

					if (matcher.groupCount() > 1) {
						String defaultValue = matcher.group(2);
						if (defaultValue != null)
							value = defaultValue;
					}
				}

				if (value == null)
					throw new RuntimeException("Unresolvable placeholder " + pValue + " at " + pDiagName);

				Pair<String, Boolean> replacementValue = resolveStr(value, pRootNode, pDiagName, pNode);
				if ((replacementValue != null) && (replacementValue._2 == true))
					redact = true;
				sb.append(replacementValue == null ? value : replacementValue._1);
			}

			if (sb == null)
				return null;

			if (startingPoint > 0)
				sb.append(pValue.substring(startingPoint));
			String finalValue = sb.toString();
			if (finalValue.equals(pValue) == true)
				return null;

			return new Pair<String, Boolean>(finalValue, redact);
		}
		return null;
	}

}
