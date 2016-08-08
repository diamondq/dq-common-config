package com.diamondq.common.config.tests.yaml;

import static org.hamcrest.CoreMatchers.startsWith;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.core.WrappedBootstrapSource;
import com.diamondq.common.config.model.BootstrapSetupConfigHolder;
import com.diamondq.common.config.spi.BootstrapConfigSourceFactory;
import com.diamondq.common.config.spi.ConfigSourceFactoryFactory;
import com.diamondq.common.config.tests.AbstractYamlTest;

import java.util.Collection;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class InvalidYAMLFileTypeTest extends AbstractYamlTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * @see com.diamondq.common.config.tests.AbstractConfigTest#getBootstrapSources(com.diamondq.common.config.spi.ConfigSourceFactoryFactory,
	 *      java.util.Collection, com.diamondq.common.config.model.BootstrapSetupConfigHolder, java.lang.String)
	 */
	@Override
	protected List<BootstrapConfigSourceFactory> getBootstrapSources(ConfigSourceFactoryFactory pFactory,
		Collection<String> pExtensions, BootstrapSetupConfigHolder pHolder, String pAppId) {
		List<BootstrapConfigSourceFactory> sources = super.getBootstrapSources(pFactory, pExtensions, pHolder, pAppId);
		sources.add(new WrappedBootstrapSource(
			pFactory.getClassPathConfigSourceFactory().create("yaml/unknown.unknown", null)));
		return sources;
	}

	@Test()
	public void testUnknownFile() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(startsWith("No available parser to parse"));
		@SuppressWarnings("unused")
		Config config = buildConfig("properties/unknown");
	}

}
