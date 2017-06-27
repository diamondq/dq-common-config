package com.diamondq.common.config.tests;

import com.diamondq.common.config.format.yaml.SnakeYAMLParser;
import com.diamondq.common.config.spi.ConfigParser;

import java.util.List;

/**
 * Abstract tests for YAML
 */
public class AbstractYamlTest extends AbstractConfigTest {

	/**
	 * @see com.diamondq.common.config.tests.AbstractConfigTest#getParsers()
	 */
	@Override
	protected List<ConfigParser> getParsers() {
		List<ConfigParser> list = super.getParsers();
		list.add(new SnakeYAMLParser());
		return list;
	}
}
