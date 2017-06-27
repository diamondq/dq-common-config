package com.diamondq.common.config.tests.simple;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.tests.AbstractYamlTest;

import org.junit.Assert;
import org.junit.Test;

/**
 * Simple tests
 */
public class SimpleTest extends AbstractYamlTest {

	/**
	 * Simple key/value
	 */
	@Test
	public void testSimpleKey() {
		Config config = buildConfig("test1");
		String value = config.bind("testKey", String.class);
		Assert.assertEquals("testValue", value);
	}

	/**
	 * Missing key
	 */
	@Test
	public void testNoSimpleKey() {
		Config config = buildConfig("test1");
		String value = config.bind("testMissingKey", String.class);
		Assert.assertNull(value);
	}

}
