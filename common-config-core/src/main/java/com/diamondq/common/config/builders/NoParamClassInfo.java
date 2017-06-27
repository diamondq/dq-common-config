package com.diamondq.common.config.builders;

import com.diamondq.common.config.core.ConfigImpl;
import com.diamondq.common.config.spi.BuilderInfo;
import com.diamondq.common.config.spi.ClassInfo;
import com.diamondq.common.config.spi.Pair;
import com.diamondq.common.config.spi.StdNoopBuilderInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Class information for a class that takes no parameters during the constructor
 * 
 * @param <O>
 */
public class NoParamClassInfo<@NonNull O> implements ClassInfo<Object, O> {

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
	@SuppressWarnings("nullness")
	public Pair<@NonNull Object, @NonNull BuilderInfo<@NonNull Object, @NonNull O>> builder(ConfigImpl pConfigImpl)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

		/*
		 * After discussion with the CheckerFramework folks (see
		 * https://github.com/typetools/checker-framework/issues/1365), the best solution is to just suppress the
		 * warning, as the -AresolveReflection doesn't work since the info about the Constructor isn't easily
		 * accessible.
		 */
		Object builder = mConstructor.newInstance((Object[]) null);

		BuilderInfo<@NonNull Object, @NonNull O> builderInfo = new StdNoopBuilderInfo<@NonNull O>();

		return new Pair<@NonNull Object, @NonNull BuilderInfo<@NonNull Object, @NonNull O>>(builder, builderInfo);

	}

}
