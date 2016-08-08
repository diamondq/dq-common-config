package com.diamondq.common.config.spi;

import com.diamondq.common.config.core.ConfigImpl;

import java.lang.reflect.InvocationTargetException;

public interface ClassInfo<T, O> {

	public Pair<T, BuilderInfo<T, O>> builder(ConfigImpl pConfigImpl)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException;

}
