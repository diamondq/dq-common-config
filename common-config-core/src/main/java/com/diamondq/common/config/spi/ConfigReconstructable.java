package com.diamondq.common.config.spi;

import java.util.Map;

/**
 * An interface indicating that this 'object' can be reconstructed with a given 'type' and 'params'.
 */
public interface ConfigReconstructable {

	/**
	 * The params necessary to reconstruct this object
	 * 
	 * @return the params
	 */
	public Map<String, String> getReconstructionParams();

	/**
	 * The type of object
	 * 
	 * @return the type
	 */
	public NodeType getReconstructionNodeType();

}
