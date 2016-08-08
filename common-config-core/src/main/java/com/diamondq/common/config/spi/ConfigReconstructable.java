package com.diamondq.common.config.spi;

import java.util.Map;

public interface ConfigReconstructable {

	public Map<String, String> getReconstructionParams();

	public NodeType getReconstructionNodeType();

}
