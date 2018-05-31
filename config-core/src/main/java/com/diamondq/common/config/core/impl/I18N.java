package com.diamondq.common.config.core.impl;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Internationalization helper class
 */
public class I18N {

	/**
	 * Formats a given string (provided by resource key) with arguments into a locale specific value.
	 * 
	 * @param pLocale the locale
	 * @param pFormatKey the key
	 * @param pArgs the arguments
	 * @return the formated string
	 */
	public static String getFormat(Locale pLocale, String pFormatKey, @Nullable Object @Nullable... pArgs) {

		ResourceBundle bundle = ResourceBundle.getBundle("common_config", pLocale);
		String format = bundle.getString(pFormatKey);
		return MessageFormat.format(format, pArgs);

	}
}
