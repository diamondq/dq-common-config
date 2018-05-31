package com.diamondq.common.config.tests.properties;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.tests.AbstractYamlTest;

import org.junit.Assert;
import org.junit.Test;

/**
 * Basic Properties Tests
 */
public class BasicPropertiesTest extends AbstractYamlTest {

	/**
	 * Simple key
	 */
	@Test
	public void testSimpleKey() {
		Config config = buildConfig("properties/test1");
		String value = config.bind("testKey", String.class);
		Assert.assertEquals("testValue", value);
	}

	/**
	 * Simple meta value
	 */
	@Test
	public void testMetaValue() {
		Config config = buildConfig("properties/meta");
		String value = config.bind("metadata.value.key1", String.class);
		Assert.assertEquals("value", value);
	}

}
