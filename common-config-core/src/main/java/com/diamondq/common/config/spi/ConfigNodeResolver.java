package com.diamondq.common.config.spi;

/**
 * Responsible for resolving placeholders within a ConfigNode
 */
public interface ConfigNodeResolver {

	public ConfigNode resolve(ConfigNode pNode);
	
}
