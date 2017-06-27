package com.diamondq.common.config.builders;

import com.diamondq.common.config.ConfigKey;

import java.util.Properties;

/**
 * A factory for Properties
 */
public class PropertiesFactory {

	private Properties mProps;

	/**
	 * The default constructor
	 */
	public PropertiesFactory() {
		mProps = new Properties();
	}

	/**
	 * The method that is called for all key/values
	 * 
	 * @param pKey
	 * @param pValue
	 */
	@ConfigKey("*")
	public void add(Object pKey, Object pValue) {
		mProps.put(pKey, pValue);
	}

	/**
	 * Builds the final Properties
	 * 
	 * @return the Properties
	 */
	public Properties build() {
		return mProps;
	}

}
