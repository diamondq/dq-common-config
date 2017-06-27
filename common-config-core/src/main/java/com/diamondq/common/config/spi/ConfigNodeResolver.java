package com.diamondq.common.config.spi;

/**
 * Responsible for resolving placeholders within a ConfigNode
 */
public interface ConfigNodeResolver extends ConfigReconstructable {

	/**
	 * Resolves all placeholders within the node
	 * 
	 * @param pNode the node to resolve
	 * @return the resolved node
	 */
	public ConfigNode resolve(ConfigNode pNode);

}
