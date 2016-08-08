package com.diamondq.common.config.tests.properties;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.tests.AbstractYamlTest;

import org.junit.Test;

public class InvalidPropertiesTest extends AbstractYamlTest {

	@Test(expected = IllegalArgumentException.class)
	public void testMultiLevelMetaKey() {
		@SuppressWarnings("unused")
		Config config = buildConfig("properties/invalid_multilevelmeta");
	}

}
