package com.diamondq.common.config.spi;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This represents a parser (ie. YAML, JSON, etc.) that is capable of translating an InputStream from a ConfigSource
 * into a Map of ConfigNodes.
 */
public interface ConfigParser extends ConfigReconstructable {

	/**
	 * Parses a ConfigDataTuple into a list of ConfigNode's
	 * 
	 * @param pData the ConfigDataTuple
	 * @return the list of ConfigNode's
	 * @throws IOException an exception
	 */
	public List<@NonNull ConfigNode> parse(ConfigDataTuple pData) throws IOException;

	/**
	 * Indicates whether this parser can be used to parse this content.
	 * 
	 * @param pMediaType the media type of the content (if available)
	 * @param pFileName the file name (if available)
	 * @return true if this parser can be used or false if it cannot
	 */
	public boolean canParse(Optional<String> pMediaType, @Nullable String pFileName);

	/**
	 * Returns the set of file extensions that may correspond to this parser
	 * 
	 * @return the file extensions
	 */
	public Collection<@NonNull String> getFileExtensions();

}
