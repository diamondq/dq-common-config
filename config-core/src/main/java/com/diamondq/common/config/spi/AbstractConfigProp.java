package com.diamondq.common.config.spi;

import java.util.Optional;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

/**
 * The config prop represents a single piece of metadata about a config node (such as the value, default value, type,
 * etc.) It contains the value and the source where the value came from.
 */
@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
public abstract class AbstractConfigProp {

	/**
	 * The value of the property.
	 * 
	 * @return the value
	 */
	public abstract Optional<String> getValue();

	/**
	 * The original value of the property (before any resolvers made changes)
	 * 
	 * @return the original value
	 */
	public abstract Optional<String> getOriginalValue();

	/**
	 * The source of the property. This is generally the name property from the ConfigSource that generated this.
	 * 
	 * @return the source
	 */
	public abstract String getConfigSource();

}
