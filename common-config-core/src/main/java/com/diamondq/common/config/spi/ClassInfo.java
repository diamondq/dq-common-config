package com.diamondq.common.config.spi;

import com.diamondq.common.config.core.ConfigImpl;

import java.lang.reflect.InvocationTargetException;

/**
 * Defines information about a given class
 * 
 * @param <T>
 * @param <O> the class
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
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	public Pair<T, BuilderInfo<T, O>> builder(ConfigImpl pConfigImpl)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException;

}
