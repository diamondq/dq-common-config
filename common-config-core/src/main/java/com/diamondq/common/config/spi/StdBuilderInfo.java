package com.diamondq.common.config.spi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class StdBuilderInfo<O> implements BuilderInfo<Object, O> {

	private final Method						mBuildMethod;

	private final List<ParameterInfo<Object>>	mParameters;

	public StdBuilderInfo(Method pBuildMethod, List<ParameterInfo<Object>> pParameters) {
		mBuildMethod = pBuildMethod;
		mParameters = pParameters;
	}

	/**
	 * @see com.diamondq.common.config.spi.BuilderInfo#getParameters()
	 */
	@Override
	public List<ParameterInfo<Object>> getParameters() {
		return mParameters;
	}

	/**
	 * @see com.diamondq.common.config.spi.BuilderInfo#build(java.lang.Object)
	 */
	@Override
	public O build(Object pBuilder) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		@SuppressWarnings("unchecked")
		O result = (O) mBuildMethod.invoke(pBuilder);
		return result;
	}

}
