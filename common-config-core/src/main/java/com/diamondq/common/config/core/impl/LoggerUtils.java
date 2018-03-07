package com.diamondq.common.config.core.impl;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.ext.XLogger;

/**
 * Helper class for logging
 */
public class LoggerUtils {

	/**
	 * Helper method to make sure that the value passed is NonNull and the result is NonNull. Unfortunately, the Eclipse
	 * External Annotations doesn't support a @PolyNull.
	 * 
	 * @param <T> the value type
	 * @param pLogger the logger
	 * @param pValue the value
	 * @return the value
	 */
	public static <@NonNull T> T nonNullExit(XLogger pLogger, T pValue) {
		pLogger.exit(pValue);
		return pValue;
	}
}
