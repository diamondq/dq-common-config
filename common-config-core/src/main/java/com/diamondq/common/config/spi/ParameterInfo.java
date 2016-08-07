package com.diamondq.common.config.spi;

import java.lang.reflect.InvocationTargetException;

public interface ParameterInfo<T> {

	public String getName();

	public Class<?> getType();

	public void set(T pBuilder, Object pValue)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
