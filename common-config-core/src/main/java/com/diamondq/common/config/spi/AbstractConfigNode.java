package com.diamondq.common.config.spi;

import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
public abstract class AbstractConfigNode {

	/**
	 * The name of the node. Must be a simple name (no 'period' / . character)
	 * 
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Returns the type of the node. Must be a fully qualified Java name (ie. java.lang.String). If the type is not
	 * known, it will default to java.lang.String. A node that is just a 'parent', will have a type of
	 * 'com.diamondq.common.config.model.Node'
	 * 
	 * @return the type
	 */
	public abstract NodeType getType();

	/**
	 * Returns the value of the node
	 * 
	 * @return the optional value
	 */
	public abstract Optional<ConfigProp> getValue();

	/**
	 * Returns the set of meta data
	 * 
	 * @return the meta data
	 */
	@Value.NaturalOrder
	public abstract SortedMap<String, ConfigProp> getMetaData();

	/**
	 * Returns the set of children
	 * 
	 * @return the children
	 */
	@Value.NaturalOrder
	public abstract SortedMap<String, ConfigNode> getChildren();

	/**
	 * This is a temporary due to the bug in withChildren (see https://github.com/immutables/immutables/issues/419) This
	 * is NOT as fast as the real method, and should be replaced once the bug is fixed.
	 * 
	 * @param pChildren the children
	 * @return the new node
	 */
	public final ConfigNode withChildrenEx(Map<String, ? extends ConfigNode> pChildren) {
		if (pChildren == getChildren())
			return (ConfigNode) this;
		return ConfigNode.builder().from(this).children(pChildren).build();
	}

}
