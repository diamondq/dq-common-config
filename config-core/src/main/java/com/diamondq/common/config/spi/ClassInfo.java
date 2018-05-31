package com.diamondq.common.config.spi;

import com.diamondq.common.config.Config;

import java.lang.reflect.InvocationTargetException;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Defines information about a given class
 *
 * @param <T> the 'state' object
 * @param <O> the 'final' object
 */
public interface ClassInfo<T, O> {

	/**
	 * Returns the class representing the final object
	 *
	 * @return the final object class
	 */
	public Class<O> getFinalClass();

	/**
	 * Returns a Pair with a 'holder' class and the BuilderInfo to build the final object
	 *
	 * @param pConfigImpl the config to use
	 * @return the pair
	 * @throws IllegalAccessException exceptions from reflection
	 * @throws IllegalArgumentException exceptions from reflection
	 * @throws InvocationTargetException exceptions from reflection
	 * @throws InstantiationException exceptions from reflection
	 */
	public Pair<T, @NonNull BuilderInfo<T, O>> builder(Config pConfigImpl)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException;

}
