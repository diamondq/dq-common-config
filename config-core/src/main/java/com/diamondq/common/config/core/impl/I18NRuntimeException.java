package com.diamondq.common.config.core.impl;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A Runtime Exception that holds on to a future localizable key
 */
public class I18NRuntimeException extends RuntimeException {

	private static final long					serialVersionUID	= 7324214310087208692L;

	private final String						mFormatKey;

	private final @Nullable Object @Nullable []	mArgs;

	/**
	 * The constructor
	 * 
	 * @param pFormatKey the format key
	 * @param pArgs the optional arguments
	 */
	public I18NRuntimeException(String pFormatKey, @Nullable Object @Nullable... pArgs) {
		super();
		mFormatKey = pFormatKey;
		mArgs = pArgs;
	}

	/**
	 * The key
	 * 
	 * @return the key
	 */
	public String getFormatKey() {
		return mFormatKey;
	}

	/**
	 * The arguments (if any)
	 * 
	 * @return the arguments
	 */
	public @Nullable Object @Nullable [] getArgs() {
		return mArgs;
	}
}
