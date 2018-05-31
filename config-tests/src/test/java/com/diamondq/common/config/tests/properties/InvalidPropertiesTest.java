package com.diamondq.common.config.tests.properties;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.tests.AbstractYamlTest;

import org.junit.Test;

/**
 * Invalid Properties tests
 */
public class InvalidPropertiesTest extends AbstractYamlTest {

	/**
	 * Multi-level meta data
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testMultiLevelMetaKey() {
		@SuppressWarnings("unused")
		Config config = buildConfig("properties/invalid_multilevelmeta");
	}

}
