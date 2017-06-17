package com.diamondq.common.config;

import java.util.Locale;
import java.util.Set;

public interface Bootstrap {

	public void setLocale(Locale pLocale);

	/**
	 * @param pDebugFilterTo a set of key prefixes to restrict any debug dumping of the config to. If it's null or
	 *            empty, then all entries are included. This is mostly used during the 'first pass' bootstrap (before
	 *            the CDI system) to keep the debugging to a minimum.
	 * @return the config
	 */
	public Config bootstrapConfig(Set<String> pDebugFilterTo);

}
