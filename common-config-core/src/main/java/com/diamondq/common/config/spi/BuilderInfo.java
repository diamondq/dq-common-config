package com.diamondq.common.config.spi;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Interface for a BuilderInfo
 * 
 * @param <T> the 'state' object for the builder
 * @param <O> the 'final' built object
 */
public interface BuilderInfo<@NonNull T, @NonNull O> {

	/**
	 * Get's the list of parameters
	 * 
	 * @return the parameters
	 */
	public List<ParameterInfo<T>> getParameters();

	/**
	 * Builds the final object
	 * 
	 * @param pBuilder the 'state' object
	 * @return the 'final' object
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public O build(T pBuilder) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
