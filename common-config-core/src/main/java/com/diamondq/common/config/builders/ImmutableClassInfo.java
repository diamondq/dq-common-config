package com.diamondq.common.config.builders;

import com.diamondq.common.config.core.ConfigImpl;
import com.diamondq.common.config.spi.BuilderInfo;
import com.diamondq.common.config.spi.ClassInfo;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;

public class ImmutableClassInfo<O> implements ClassInfo<Object, O> {

	private final Method				mConstructor;

	private final Object				mConstructorArg;

	private final int					mConstructorArgPos;

	private final int					mConfigArgPos;

	private final int					mParamCount;

	private final ConfigClassBuilder	mClassBuilder;

	public ImmutableClassInfo(ConfigClassBuilder pClassBuilder, Method pConstructor, Object pConstructorArg,
		int pConstructorArgPos, int pConfigArgPos) {
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
	 * @see com.diamondq.common.config.spi.ClassInfo#builder(com.diamondq.common.config.core.ConfigImpl)
	 */
	@Override
	public Pair<Object, BuilderInfo<Object, O>> builder(ConfigImpl pConfigImpl)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Object[] params = (mParamCount == 0 ? null : new Object[mParamCount]);
		if (mConfigArgPos != -1)
			params[mConfigArgPos] = pConfigImpl;
		if (mConstructorArgPos != -1)
			params[mConstructorArgPos] = mConstructorArg;

		@SuppressWarnings("nullness")
		@Nonnull
		Object builder = mConstructor.invoke(null, params);

		BuilderInfo<Object, O> builderInfo = mClassBuilder.getBuilderInfo(this, builder);

		return new Pair<Object, BuilderInfo<Object, O>>(builder, builderInfo);
	}

}
