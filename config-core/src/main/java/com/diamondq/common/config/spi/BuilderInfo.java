package com.diamondq.common.config.spi;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Interface for a BuilderInfo
 * 
 * @param <T> the 'state' object for the builder
 * @param <O> the 'final' built object
 */
public interface BuilderInfo<T, O> {

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
	 * @throws IllegalAccessException exception
	 * @throws IllegalArgumentException exception
	 * @throws InvocationTargetException exception
	 */
	public O build(T pBuilder) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
