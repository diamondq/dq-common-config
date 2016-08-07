package com.diamondq.common.config.core;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.core.std.CoreFactoryFactory;
import com.diamondq.common.config.format.yaml.SnakeYAMLParser;
import com.diamondq.common.config.model.BootstrapSetupConfig;
import com.diamondq.common.config.spi.ConfigParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

public class BootstrapTest {

	@Test
	public void test() {
		String env = "";
		List<String> profiles = new ArrayList<>();
		profiles.add("dev");

		List<ConfigParser> parsers = new ArrayList<>(StandardSetup.getStandardParsers());
		parsers.add(new SnakeYAMLParser());
		List<String> extensions =
			parsers.stream().flatMap(p -> p.getFileExtensions().stream()).collect(Collectors.toList());
		BootstrapSetupConfig build = BootstrapSetupConfig.builder().environment(env).profiles(profiles)
			.addAllClassBuilders(StandardSetup.getStandardClassBuilders())
			.addAllBootstrapSources(StandardSetup.getStandardBootstrapSources(new CoreFactoryFactory(), extensions))
			.addAllParsers(StandardSetup.getStandardParsers()).addParser(new SnakeYAMLParser()).build();
		BootstrapConfigImpl impl = new BootstrapConfigImpl(build);
		Config myConfig = impl.bootstrapConfig();
		String value = myConfig.bind("testKey", String.class);
		Assert.assertEquals("testValue", value);
	}

}
