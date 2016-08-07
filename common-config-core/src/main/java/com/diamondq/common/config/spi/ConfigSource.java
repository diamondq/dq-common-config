package com.diamondq.common.config.spi;

import java.util.List;
import java.util.Map;

/**
 * Represents a source of configuration information
 */
public interface ConfigSource {

	/**
	 * Returns the set of parameters used to construct this configuration source. NOTE: That the environment and
	 * profiles are not included.
	 * 
	 * @return a Map of key/value parameters.
	 */
	public Map<String, String> getReconstructionDetails();

	/**
	 * Returns a name that, while not guaranteed unique, should be able to generally refer to this specific
	 * ConfigSource.
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Returns the configuration provided by this source.
	 * 
	 * @param pEnvironment
	 * @param pProfiles
	 * @return the configuration data
	 */
	public List<ConfigDataTuple> getConfiguration(String pEnvironment, List<String> pProfiles);

}
