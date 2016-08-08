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

	public String getSimpleName() {
		StringBuilder sb = new StringBuilder();
		if ((getType().isPresent() == true) && (getType().get().getValue().isPresent() == true))
			sb.append("t=").append(getType().get().getValue().get());
		if ((getFactory().isPresent() == true) && (getFactory().get().getValue().isPresent() == true)) {
			if (sb.length() > 0)
				sb.append(',');
			sb.append("f=").append(getFactory().get().getValue().get());
		}
		if ((getFactoryArg().isPresent() == true) && (getFactoryArg().get().getValue().isPresent() == true)) {
			if (sb.length() > 0)
				sb.append(',');
			sb.append("fa=").append(getFactoryArg().get().getValue().get());
		}
		return sb.toString();
	}
}
