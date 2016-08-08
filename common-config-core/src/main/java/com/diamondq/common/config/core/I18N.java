package com.diamondq.common.config.core;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {

	public static String getFormat(Locale pLocale, String pFormatKey, Object... pArgs) {

		ResourceBundle bundle = ResourceBundle.getBundle("common_config", pLocale);
		String format = bundle.getString(pFormatKey);
		return MessageFormat.format(format, pArgs);

	}
}
