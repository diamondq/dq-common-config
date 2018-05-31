package com.diamondq.common.config;

import java.util.Locale;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The interface for generating a Config from nothing (aka bootstrap)
 */
public interface Bootstrap {

	/**
	 * The current version of this Bootstrap interface (used for better backwards compatibility)
	 */
	public static final int CURRENT_INTERFACE_VERSION = 0x00010000;

	/**
	 * Returns the implementation version of this interface. Implementations should always implement this method as just
	 * returning the constant {@link #CURRENT_INTERFACE_VERSION}.
	 * 
	 * @return the version
	 */
	public int getCurrentBootstrapInterfaceVersion();

	/**
	 * Sets the locale
	 * 
	 * @param pLocale the locale
	 */
	public void setLocale(Locale pLocale);

	/**
	 * @param pDebugFilterTo a set of key prefixes to restrict any debug dumping of the config to. If it's null or
	 *            empty, then all entries are included. This is mostly used during the 'first pass' bootstrap (before
	 *            the CDI system) to keep the debugging to a minimum.
	 * @return the config
	 */
	public Config bootstrapConfig(@Nullable Set<String> pDebugFilterTo);

}
