package com.diamondq.common.config.format.yaml;

import com.diamondq.common.config.format.AbstractStdConfigParser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Generically, the YAML format has 3 types of entries, a Map, a List and a Value. Lists are translated into Maps where
 * each zero-based offset is the key. Maps are flattened using the period (.) as the separator.
 *
 * <pre>
 * bootstrap:
 *    profiles:
 *    	- dev
 *    	- test
 * </pre>
 *
 * Translates to
 *
 * <pre>
 * bootstrap.profiles.0 = dev
 * bootstrap.profiles.1 = test
 * </pre>
 *
 * Additional meta data (such as the type, help, etc.) is associated based on the type of entry. <br>
 * <br>
 * A Map is associated by placing a child map with the key of <code>_dqconfig_meta_</code>. Each child is associated in
 * the meta data.
 *
 * <pre>
 * bootstrap:
 * 	block:
 *		_dqconfig_meta_:
 *			factory: com.diamondq.common.config.ExampleFactory
 * 			arg: testValue
 * 			otherMeta: metaValue
 * 		otherArg: simple
 * </pre>
 *
 * Translates to
 *
 * <pre>
 * bootstrap.block=[factory=com.diamondq.common.config.ExampleFactory, arg=testValue] {otherMeta=metaValue}
 * bootstrap.block.otherArg=simple
 * </pre>
 *
 * A List is associated by placing a child map as the first element with a key of <code>_dqconfig_list_</code>.
 *
 * <pre>
 * bootstrap:
 * 	list:
 * 		- _dqconfig_list_:
 *			otherMeta: metaValue
 *		- firstValue
 * </pre>
 *
 * Translates to
 *
 * <pre>
 *bootstrap.list={otherMeta=metaValue}
 *bootstrap.list.0=firstValue
 * </pre>
 *
 * A Value is associated by placing a separate sibling key with a key of <code>_dqconfig_meta_YYY</code> where YYY is
 * the key name to associate.
 *
 * <pre>
 * bootstrap:
 * 	environment: test
 * 	_dqconfig_meta_environment:
 * 		type: java.lang.String
 * 		otherMeta: metaValue
 * </pre>
 *
 * Translates to
 *
 * <pre>
 * bootstrap.environment=[java.lang.String]{otherMeta=metaValue}test
 * </pre>
 *
 * The specialized meta entry <code>factory</code>, <code>arg</code> and <code>type</code> are interpreted as TYPE
 * information, not meta data.
 */
public abstract class AbstractYAMLConfigParser extends AbstractStdConfigParser {

	protected static final Set<String> sFileExtensions;

	static {
		Set<String> r = new HashSet<>();
		r.add("yaml");
		r.add("yml");
		sFileExtensions = Collections.unmodifiableSet(r);
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigParser#canParse(java.util.Optional, java.lang.String)
	 */
	@Override
	public boolean canParse(Optional<String> pMediaType, @Nullable String pFileName) {
		if (pFileName == null)
			return false;
		int offset = pFileName.lastIndexOf('.');
		String suffix = pFileName.substring(offset + 1);
		if (sFileExtensions.contains(suffix))
			return true;
		return false;
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigParser#getFileExtensions()
	 */
	@Override
	public Collection<String> getFileExtensions() {
		return sFileExtensions;
	}
}
