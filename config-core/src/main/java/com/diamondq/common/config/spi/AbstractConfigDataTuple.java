package com.diamondq.common.config.spi;

import java.io.InputStream;
import java.util.Optional;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

/**
 * Immutable model for ConfigDataTuple
 */
@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
public abstract class AbstractConfigDataTuple {

	/**
	 * The data stream representing this source
	 * 
	 * @return the stream
	 */
	public abstract InputStream getStream();

	/**
	 * The (optional) media type of this source
	 * 
	 * @return the media type
	 */
	public abstract Optional<String> getMediaType();

	/**
	 * The representable 'file name' for this source
	 * 
	 * @return the file name
	 */
	public abstract String getName();

	/**
	 * The ConfigSource this came from
	 * 
	 * @return the source
	 */
	public abstract ConfigSource getSource();
}
