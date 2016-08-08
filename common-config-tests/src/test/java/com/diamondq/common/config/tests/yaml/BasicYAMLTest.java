package com.diamondq.common.config.tests.yaml;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.tests.AbstractYamlTest;

import org.junit.Assert;
import org.junit.Test;

public class BasicYAMLTest extends AbstractYamlTest {

	@Test
	public void testMetaValue() {
		Config config = buildConfig("yaml/meta");
		String value = config.bind("metadata.value.key1", String.class);
		Assert.assertEquals("value", value);
	}

}
