package com.diamondq.common.config.builders;

import com.diamondq.common.config.spi.BuilderInfo;
import com.diamondq.common.config.spi.ClassInfo;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ImmutableClassInfo<O> implements ClassInfo<Object, O> {

	private final Method				mConstructor;

	private final boolean				mWithConstructorArg;

	private final Object				mConstructorArg;

	private final ConfigClassBuilder	mClassBuilder;

	public ImmutableClassInfo(ConfigClassBuilder pClassBuilder, Method pConstructor, Object pConstructorArg,
		boolean pWithConstructorArg) {
		mClassBuilder = pClassBuilder;
		mConstructor = pConstructor;
		mWithConstructorArg = pWithConstructorArg;
		mConstructorArg = pConstructorArg;
	}

	/**
	 * @see com.diamondq.common.config.spi.ClassInfo#builder()
	 */
	@Override
	public Pair<Object, BuilderInfo<Object, O>> builder()
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object builder;
		if (mWithConstructorArg == true)
			builder = mConstructor.invoke(null, mConstructorArg);
		else
			builder = mConstructor.invoke(null);

		if (builder == null)
			throw new IllegalStateException();
		
		BuilderInfo<Object, O> builderInfo = mClassBuilder.getBuilderInfo(this, builder);

		return new Pair<Object, BuilderInfo<Object, O>>(builder, builderInfo);
	}

}
