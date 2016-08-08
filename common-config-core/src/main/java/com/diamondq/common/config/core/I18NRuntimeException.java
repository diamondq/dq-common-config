package com.diamondq.common.config.core;

public class I18NRuntimeException extends RuntimeException {

	private static final long	serialVersionUID	= 7324214310087208692L;

	private final String		mFormatKey;

	private final Object[]		mArgs;

	public I18NRuntimeException(String pFormatKey, Object... pArgs) {
		super();
		mFormatKey = pFormatKey;
		mArgs = pArgs;
	}

	public String getFormatKey() {
		return mFormatKey;
	}

	public Object[] getArgs() {
		return mArgs;
	}
}
