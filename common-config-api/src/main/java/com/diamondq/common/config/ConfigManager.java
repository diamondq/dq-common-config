package com.diamondq.common.config;

import java.util.Locale;

/**
 * This is a singleton that is used to find the appropriate Config
 */
public interface ConfigManager {

	/**
	 * Sets the locale
	 *
	 * @param pLocale the locale
	 */
	public void setLocale(Locale pLocale);

	/**
	 * Gets the Config given all the defaults
	 *
	 * @return the Config
	 */
	public Config getConfig();

}
