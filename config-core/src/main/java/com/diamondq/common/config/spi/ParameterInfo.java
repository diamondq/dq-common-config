package com.diamondq.common.config.spi;

import java.lang.reflect.InvocationTargetException;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Information about a given parameter within a builder
 * 
 * @param <T> the builder
 */
public interface ParameterInfo<T> {

	/**
	 * The type of parameter
	 */
	public static enum ParameterType {
		/**
		 * A regular parameter
		 */
		NORMAL,
		/**
		 * A list of parameters
		 */
		LIST,
		/**
		 * A map of key/value parameters
		 */
		MAP
	};

	/**
	 * Returns the name of the parameter
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Returns the type of the parameter
	 * 
	 * @return the type
	 */
	public ParameterType getType();

	/**
	 * Returns the 'class' underlying the parameter
	 * 
	 * @return the class
	 */
	public Class<?> getClassType1();

	/**
	 * Returns the other 'class' (optionally) underlying the parameter. Not all parameters have a second class type.
	 * 
	 * @return the class or null
	 */
	public @Nullable Class<?> getClassType2();

	/**
	 * Stores the object within the builder for this parameter
	 * 
	 * @param pBuilder the builder
	 * @param pValue1 the object
	 * @throws IllegalAccessException exception
	 * @throws IllegalArgumentException exception
	 * @throws InvocationTargetException exception
	 */
	public void set1(T pBuilder, @Nullable Object pValue1)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

	/**
	 * Stores the second object within the builder for this parameter
	 * 
	 * @param pBuilder the builder
	 * @param pValue1 the first object
	 * @param pValue2 the second object
	 * @throws IllegalAccessException exception
	 * @throws IllegalArgumentException exception
	 * @throws InvocationTargetException exception
	 */
	public void set2(T pBuilder, Object pValue1, @Nullable Object pValue2)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
