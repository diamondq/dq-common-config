package com.diamondq.common.config.builders;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.spi.BuilderInfo;
import com.diamondq.common.config.spi.ClassInfo;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Information about an Immutable class
 *
 * @param <O> the class
 */
public class ImmutableClassInfo<@NonNull O> implements ClassInfo<Object, O> {

	private final Method				mConstructor;

	@Nullable
	private final Object				mConstructorArg;

	private final int					mConstructorArgPos;

	private final int					mConfigArgPos;

	private final int					mParamCount;

	private final ConfigClassBuilder	mClassBuilder;

	private final Class<O>				mFinalClass;

	/**
	 * Constructor
	 *
	 * @param pFinalClass the final class (non builder) to build
	 * @param pClassBuilder the config class builder
	 * @param pConstructor the constructor of the final class
	 * @param pConstructorArg the argument to pass
	 * @param pConstructorArgPos the position of the argument
	 * @param pConfigArgPos the arg pos
	 */
	public ImmutableClassInfo(Class<O> pFinalClass, ConfigClassBuilder pClassBuilder, Method pConstructor,
		@Nullable Object pConstructorArg, int pConstructorArgPos, int pConfigArgPos) {
		mFinalClass = pFinalClass;
		mClassBuilder = pClassBuilder;
		mConstructor = pConstructor;
		mConstructorArgPos = pConstructorArgPos;
		mConfigArgPos = pConfigArgPos;
		if (mConstructorArgPos == -1) {
			if (mConfigArgPos == -1)
				mParamCount = 0;
			else
				mParamCount = 1;
		}
		else {
			if (mConfigArgPos == -1)
				mParamCount = 1;
			else
				mParamCount = 2;
		}
		mConstructorArg = pConstructorArg;
	}

	/**
	 * @see com.diamondq.common.config.spi.ClassInfo#getFinalClass()
	 */
	@Override
	public Class<O> getFinalClass() {
		return mFinalClass;
	}

	/**
	 * @see com.diamondq.common.config.spi.ClassInfo#builder(com.diamondq.common.config.Config)
	 */
	@Override
	public Pair<Object, BuilderInfo<Object, O>> builder(Config pConfig)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Object builder;
		if (mParamCount == 0)
			builder = mConstructor.invoke(null, (Object[]) null);
		else {
			Object[] params = new Object[mParamCount];
			if (mConfigArgPos != -1)
				params[mConfigArgPos] = pConfig;
			if (mConstructorArgPos != -1)
				params[mConstructorArgPos] = mConstructorArg;

			builder = mConstructor.invoke(null, params);
		}
		if (builder == null)
			throw new IllegalArgumentException();

		BuilderInfo<Object, O> builderInfo = mClassBuilder.getBuilderInfo(this, builder);

		return new Pair<Object, BuilderInfo<Object, O>>(builder, builderInfo);
	}

}
