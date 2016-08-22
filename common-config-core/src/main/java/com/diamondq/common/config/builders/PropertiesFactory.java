package com.diamondq.common.config.builders;

import com.diamondq.common.config.ConfigKey;

import java.util.Properties;

public class PropertiesFactory {

	private Properties mProps;

	public PropertiesFactory() {
		mProps = new Properties();
	}

	@ConfigKey("*")
	public void add(Object pKey, Object pValue) {
		mProps.put(pKey, pValue);
	}

	public Properties build() {
		return mProps;
	}

}
