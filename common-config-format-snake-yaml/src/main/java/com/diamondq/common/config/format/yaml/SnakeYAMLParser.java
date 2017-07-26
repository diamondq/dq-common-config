package com.diamondq.common.config.format.yaml;

import com.diamondq.common.config.spi.ConfigDataTuple;
import com.diamondq.common.config.spi.ConfigNode;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.NodeType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

/**
 * This parser is capable of reading YAML files by using the Snake YAML library
 */
@ApplicationScoped
public class SnakeYAMLParser extends AbstractYAMLConfigParser implements ConfigParser {

	private static final NodeType sNodeType = NodeType.builder().isExplicitType(true)
		.type(ConfigProp.builder().configSource("").value(SnakeYAMLParser.class.getName()).build()).build();

	/**
	 * The default constructor
	 */
	public SnakeYAMLParser() {

	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigParser#getReconstructionNodeType()
	 */
	@Override
	public NodeType getReconstructionNodeType() {
		return sNodeType;
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigParser#getReconstructionParams()
	 */
	@Override
	public Map<String, String> getReconstructionParams() {
		return Collections.emptyMap();
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigParser#parse(com.diamondq.common.config.spi.ConfigDataTuple)
	 */
	@Override
	public List<ConfigNode> parse(ConfigDataTuple pData) throws IOException {
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
