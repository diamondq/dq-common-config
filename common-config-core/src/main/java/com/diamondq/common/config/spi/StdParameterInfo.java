package com.diamondq.common.config.spi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StdParameterInfo<T> implements ParameterInfo<T> {

	private final String	mName;

	private final Class<?>	mType;

	private final Method	mMethod;

	public StdParameterInfo(String pName, Class<?> pType, Method pMethod) {
		mName = pName;
		mType = pType;
		mMethod = pMethod;
	}

	/**
	 * @see com.diamondq.common.config.spi.ParameterInfo#getName()
	 */
	@Override
	public String getName() {
		return mName;
	}

	/**
	 * @see com.diamondq.common.config.spi.ParameterInfo#getType()
	 */
	@Override
	public Class<?> getType() {
		return mType;
	}

	/**
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @see com.diamondq.common.config.spi.ParameterInfo#set(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void set(T pBuilder, Object pValue)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		mMethod.invoke(pBuilder, pValue);
	}

}
