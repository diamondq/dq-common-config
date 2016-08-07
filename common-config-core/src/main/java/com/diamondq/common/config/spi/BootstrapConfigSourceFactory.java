package com.diamondq.common.config.spi;

import java.util.List;

public interface BootstrapConfigSourceFactory {

	public int getBootstrapPriority();

	public ConfigSource create(String pEnvironment, List<String> pProfiles);
}
