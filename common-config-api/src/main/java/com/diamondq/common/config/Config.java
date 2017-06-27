package com.diamondq.common.config;

import java.util.Locale;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents the configuration. Can be queried to bind the configuration data to a given object.
 */
public interface Config {

	/**
	 * The current version of this Config interface (used for better backwards compatibility)
	 */
	public static final int CURRENT_INTERFACE_VERSION = 0x00010000;

	/**
	 * Returns the implementation version of this interface. Implementations should always implement this method as just
	 * returning the constant {@link #CURRENT_INTERFACE_VERSION}.
	 * 
	 * @return the version
	 */
	public int getCurrentConfigInterfaceVersion();

	/**
	 * Sets the locale of the current thread (used for error messages)
	 * 
	 * @param pLocale the locale
	 */
	public void setThreadLocale(Locale pLocale);

	/**
	 * Returns an instance of the given class with all the data bound to the correct variables
	 * 
	 * @param pPrefix the configuration prefix
	 * @param pClass the class
	 * @param <T> the class type
	 * @return the result
	 */
	public <@NonNull T> @Nullable T bind(String pPrefix, Class<T> pClass);

	/**
	 * Returns an instance of the given class with all the data bound to the correct variables
	 * 
	 * @param pPrefix the configuration prefix
	 * @param pClass the class
	 * @param pContext the context to be used to lookup injection parameters
	 * @param <T> the class type
	 * @return the result
	 */
	public <@NonNull T> @Nullable T bind(String pPrefix, Class<T> pClass, @Nullable Map<String, Object> pContext);
}
