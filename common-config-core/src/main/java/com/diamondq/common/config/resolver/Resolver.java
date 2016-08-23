package com.diamondq.common.config.resolver;

import com.diamondq.common.config.spi.ConfigNode;
import com.diamondq.common.config.spi.ConfigNodeResolver;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.ConfigProp.Builder;
import com.diamondq.common.config.spi.NodeType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Resolver implements ConfigNodeResolver {

	private Pattern					mPattern;

	private static final NodeType	sNodeType	= NodeType.builder().isExplicitType(true)
		.type(ConfigProp.builder().configSource("").value(Resolver.class.getName()).build()).build();

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

	private ConfigNode internalResolve(ConfigNode pRootNode, String pDiagName, ConfigNode pNode) {

		/* Recursively handle all children */

		Map<String, ConfigNode> replacementChildren = null;
		for (Map.Entry<String, ConfigNode> child : pNode.getChildren().entrySet()) {
			ConfigNode replaceChild = internalResolve(pRootNode, pDiagName + "." + child.getKey(), child.getValue());
			if (replaceChild != null) {
				if (replacementChildren == null)
					replacementChildren = new HashMap<>(pNode.getChildren());
				replacementChildren.put(child.getKey(), replaceChild);
			}
		}

		ConfigNode replacement = null;

		/* Check to see if there is anything that needs resolving in the current node */

		if (pNode.getValue().isPresent() == true) {
			ConfigProp prop = pNode.getValue().get();
			if (prop.getValue().isPresent() == true) {
				String actualValue = prop.getValue().get();
				String replacementValue = resolveStr(actualValue, pRootNode, pDiagName, pNode);
				if (replacementValue != null) {
					Builder builder = ConfigProp.builder().from(prop).value(replacementValue);
					if (prop.getOriginalValue().isPresent() == false)
						builder = builder.originalValue(actualValue);
					ConfigProp replacementProp = builder.build();

					replacement = pNode.withValue(replacementProp);
				}
			}
		}

		if (replacementChildren != null)
			replacement = (replacement == null ? pNode : replacement).withChildren(replacementChildren);

		return replacement;
	}

	protected String resolveStr(String pValue, ConfigNode pRootNode, String pDiagName, ConfigNode pNode) {
		if (pValue != null) {
			Matcher matcher = mPattern.matcher(pValue);
			StringBuilder sb = null;
			while (matcher.find() == true) {
				if (sb == null) {
					sb = new StringBuilder();
					sb.append(pValue.substring(0, matcher.start()));
				}
				String key = matcher.group(1);
				String[] keys = key.split("\\.");
				ConfigNode n = pRootNode;
				for (String k : keys) {
					n = n.getChildren().get(k);
					if (n == null)
						break;
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

				String replacementValue = resolveStr(value, pRootNode, pDiagName, pNode);
				sb.append(replacementValue == null ? value : replacementValue);
			}

			if (sb == null)
				return null;
			String finalValue = sb.toString();
			if (finalValue.equals(pValue) == true)
				return null;

			return finalValue;
		}
		return null;
	}

}
