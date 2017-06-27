package com.diamondq.common.config.spi;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A standard Builder that just returns the Builder
 * 
 * @param <O>
 */
public class StdNoopBuilderInfo<@NonNull O> implements BuilderInfo<Object, O> {

	/**
	 * Default constructor
	 */
	public StdNoopBuilderInfo() {
	}

	/**
	 * @see com.diamondq.common.config.spi.BuilderInfo#getParameters()
	 */
	@Override
	public List<ParameterInfo<Object>> getParameters() {
		return Collections.emptyList();
	}

	/**
	 * @see com.diamondq.common.config.spi.BuilderInfo#build(java.lang.Object)
	 */
	@Override
	public O build(Object pBuilder) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		@SuppressWarnings("unchecked")
		O result = (O) pBuilder;
		return result;
	}

}
