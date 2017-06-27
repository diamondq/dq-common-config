package com.diamondq.common.config.spi;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface BuilderInfo<@NonNull T, @NonNull O> {

	public List<ParameterInfo<T>> getParameters();

	public O build(T pBuilder) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
