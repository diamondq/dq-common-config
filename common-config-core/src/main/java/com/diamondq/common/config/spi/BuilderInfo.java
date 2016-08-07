package com.diamondq.common.config.spi;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface BuilderInfo<T, O> {

	public List<ParameterInfo<T>> getParameters();

	public O build(T pBuilder) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
