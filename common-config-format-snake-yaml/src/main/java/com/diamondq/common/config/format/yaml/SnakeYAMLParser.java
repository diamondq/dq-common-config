package com.diamondq.common.config.format.yaml;

import com.diamondq.common.config.spi.ConfigDataTuple;
import com.diamondq.common.config.spi.ConfigNode;
import com.diamondq.common.config.spi.ConfigParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

public class SnakeYAMLParser extends AbstractYAMLConfigParser implements ConfigParser {

	public SnakeYAMLParser() {

	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigParser#parse(com.diamondq.common.config.spi.ConfigDataTuple)
	 */
	@Override
	public List<ConfigNode> parse(ConfigDataTuple pData) {
		Yaml yaml = new Yaml(new SafeConstructor());
		InputStream stream = pData.getStream();
		Iterable<Object> docs = yaml.loadAll(stream);
		List<ConfigNode> results = new ArrayList<>();
		for (Object o : docs) {
			ConfigNode rootNode = map(pData.getName(), "", o);
			results.add(rootNode);
		}
		return results;
	}

}
