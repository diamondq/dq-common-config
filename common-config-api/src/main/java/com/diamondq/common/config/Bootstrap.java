package com.diamondq.common.config;

import java.util.Locale;

public interface Bootstrap {

	public void setLocale(Locale pLocale);

	public Config bootstrapConfig();

}
