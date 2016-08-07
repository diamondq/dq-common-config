package com.diamondq.common.config.spi;

import java.lang.reflect.InvocationTargetException;

public interface ClassInfo<T, O> {

	public Pair<T, BuilderInfo<T, O>> builder()
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

}
