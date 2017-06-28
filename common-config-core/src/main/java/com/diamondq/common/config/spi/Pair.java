package com.diamondq.common.config.spi;

/**
 * A single Pair class to make it easy to return two things from a method
 * 
 * @param <T1> the type of the first param
 * @param <T2> the type of the second param
 */
public class Pair<T1, T2> {

	/**
	 * The first param (naming convention is based on javatuples)
	 */
	public T1	_1;

	/**
	 * The second param (naming convention is based on javatuples)
	 */
	public T2	_2;

	/**
	 * Constructor
	 * 
	 * @param pA first param
	 * @param pB second param
	 */
	public Pair(T1 pA, T2 pB) {
		_1 = pA;
		_2 = pB;
	}

}
