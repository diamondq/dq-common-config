package com.diamondq.common.config.spi;

/**
 * Responsible for resolving placeholders within a ConfigNode
 */
public interface ConfigNodeResolver extends ConfigReconstructable {

	public ConfigNode resolve(ConfigNode pNode);
	
}
