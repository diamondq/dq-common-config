package com.diamondq.common.config.model;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The holder for a BootstrapSetupConfig
 */
public class BootstrapSetupConfigHolder {

	/**
	 * The BootstrapSetupConfig
	 */
	public volatile @Nullable BootstrapSetupConfig value;

	/**
	 * Default constructor
	 */
	public BootstrapSetupConfigHolder() {
	}
}
