package com.diamondq.common.config.spi;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

public class StdNoopBuilderInfo<O> implements BuilderInfo<Object, O> {

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
