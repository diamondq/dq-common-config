package com.diamondq.common.config.builders;

import com.diamondq.common.config.core.ConfigImpl;
import com.diamondq.common.config.spi.BuilderInfo;
import com.diamondq.common.config.spi.ClassInfo;
import com.diamondq.common.config.spi.Pair;
import com.diamondq.common.config.spi.StdNoopBuilderInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Class information for a class that takes no parameters during the constructor
 * 
 * @param <O>
 */
public class NoParamClassInfo<O> implements ClassInfo<Object, O> {

	private Constructor<?>	mConstructor;

	private final Class<O>	mFinalClass;

	/**
	 * The constructor
	 * 
	 * @param pFinalClass
	 * @param pConstructor
	 */
	public NoParamClassInfo(Class<O> pFinalClass, Constructor<?> pConstructor) {
		mFinalClass = pFinalClass;
		mConstructor = pConstructor;
	}

	/**
	 * @see com.diamondq.common.config.spi.ClassInfo#getFinalClass()
	 */
	@Override
	public Class<O> getFinalClass() {
		return mFinalClass;
	}

	/**
	 * @see com.diamondq.common.config.spi.ClassInfo#builder(com.diamondq.common.config.core.ConfigImpl)
	 */
	@Override
	public Pair<Object, BuilderInfo<Object, O>> builder(ConfigImpl pConfigImpl)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

		/*
		 * This is done with an extra parameter to make both the Eclipse Null checker and the CheckerFramework null
		 * checker happy. Eventually, the CheckerFramework should support a null array in their annotated JDK. Opened
		 * https://github.com/typetools/checker-framework/issues/1365 to track.
		 */
		@NonNull
		Object @Nullable [] params = new Object[0];
		Object builder = mConstructor.newInstance(params);

		BuilderInfo<Object, O> builderInfo = new StdNoopBuilderInfo<O>();

		return new Pair<Object, BuilderInfo<Object, O>>(builder, builderInfo);

	}

}
