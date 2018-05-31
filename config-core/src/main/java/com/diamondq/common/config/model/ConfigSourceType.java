package com.diamondq.common.config.model;

/**
 * Enumeration for the standard type of config sources
 */
public enum ConfigSourceType {

	/**
	 * Config file stored in the CLASSPATH
	 */
	CLASSPATH,
	/**
	 * Config information based on the Environment Variables
	 */
	ENV,
	/**
	 * Config file stored on the file system
	 */
	FILE,
	/**
	 * Config information in memory in a Map Key/Value
	 */
	INMEMORY,
	/**
	 * Config information based on System Properties
	 */
	SYSPROPS,
	/**
	 * Config information based on Docker secrets
	 */
	DOCKERSECRETS
}
