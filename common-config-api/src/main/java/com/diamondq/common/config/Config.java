package com.diamondq.common.config;

import java.util.Locale;

public interface Config {

	public void setThreadLocale(Locale pLocale);

	/**
	 * Returns an instance of the given class with all the data bound to the correct variables
	 * 
	 * @param pPrefix the configuration prefix
	 * @param pClass the class
	 * @return the result
	 */
	public <T> T bind(String pPrefix, Class<T> pClass);

}
