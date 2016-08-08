package com.diamondq.common.config.tests.simple;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.tests.AbstractYamlTest;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTest extends AbstractYamlTest {

	@Test
	public void testSimpleKey() {
		Config config = buildConfig("test1");
		String value = config.bind("testKey", String.class);
		Assert.assertEquals("testValue", value);
	}

	@Test
	public void testNoSimpleKey() {
		Config config = buildConfig("test1");
		String value = config.bind("testMissingKey", String.class);
		Assert.assertNull(value);
	}

}
