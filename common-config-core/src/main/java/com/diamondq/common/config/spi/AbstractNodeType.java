package com.diamondq.common.config.spi;

import java.util.Optional;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
public abstract class AbstractNodeType {

	/**
	 * Returns true if the type provided is explicitly provided. ie. if false, then we're just assuming that it's a
	 * String.
	 * 
	 * @return true or false
	 */
	@Value.Default
	public boolean isExplicitType() {
		return false;
	}

	public abstract Optional<ConfigProp> getType();

	public abstract Optional<ConfigProp> getFactory();

	public abstract Optional<ConfigProp> getFactoryArg();

}
