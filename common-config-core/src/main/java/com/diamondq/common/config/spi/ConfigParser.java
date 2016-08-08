package com.diamondq.common.config.spi;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This represents a parser (ie. YAML, JSON, etc.) that is capable of translating an InputStream from a ConfigSource
 * into a Map of ConfigNodes.
 */
public interface ConfigParser extends ConfigReconstructable {

	public List<ConfigNode> parse(ConfigDataTuple pData) throws IOException;

	public boolean canParse(Optional<String> pMediaType, String pFileName);

	public Collection<String> getFileExtensions();

}
