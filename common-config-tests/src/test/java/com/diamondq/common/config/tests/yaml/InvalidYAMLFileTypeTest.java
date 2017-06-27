package com.diamondq.common.config.tests.yaml;

import static org.hamcrest.CoreMatchers.startsWith;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.core.WrappedBootstrapSource;
import com.diamondq.common.config.model.BootstrapSetupConfigHolder;
import com.diamondq.common.config.spi.BootstrapConfigSourceFactory;
import com.diamondq.common.config.spi.ConfigSource;
import com.diamondq.common.config.spi.ConfigSourceFactory;
import com.diamondq.common.config.spi.ConfigSourceFactoryFactory;
import com.diamondq.common.config.tests.AbstractYamlTest;

import java.util.Collection;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Invalid test
 */
public class InvalidYAMLFileTypeTest extends AbstractYamlTest {

	/**
	 * Holder of the expected exception
	 */
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
		ConfigSourceFactory factory = pFactory.getClassPathConfigSourceFactory();
		ConfigSource source = factory.create("yaml/unknown.unknown", null);
		sources.add(new WrappedBootstrapSource(source));
		return sources;
	}

	/**
	 * Tests when the file isn't the correct type
	 */
	@Test()
	public void testUnknownFile() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(startsWith("No available parser to parse"));
		@SuppressWarnings("unused")
		Config config = buildConfig("properties/unknown");
	}

}
