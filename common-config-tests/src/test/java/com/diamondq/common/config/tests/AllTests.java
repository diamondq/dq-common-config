package com.diamondq.common.config.tests;

import com.diamondq.common.config.tests.properties.AllPropertiesTests;
import com.diamondq.common.config.tests.simple.AllSimpleTests;
import com.diamondq.common.config.tests.yaml.AllYAMLTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * All tests
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({AllPropertiesTests.class, AllSimpleTests.class, AllYAMLTests.class})
public class AllTests {

}
