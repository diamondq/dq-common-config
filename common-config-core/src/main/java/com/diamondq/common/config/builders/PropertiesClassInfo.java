package com.diamondq.common.config.builders;

import com.diamondq.common.config.core.ConfigImpl;
import com.diamondq.common.config.spi.BuilderInfo;
import com.diamondq.common.config.spi.ClassInfo;
import com.diamondq.common.config.spi.Pair;
import com.diamondq.common.config.spi.ParameterInfo;
import com.diamondq.common.config.spi.ParameterInfo.ParameterType;
import com.diamondq.common.config.spi.StdBuilderInfo;
import com.diamondq.common.config.spi.StdParameterInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Class information for the Properties builder
 * 
 * @param <O>
 */
public class PropertiesClassInfo<@NonNull O> implements ClassInfo<Object, O> {

	private final Class<O>								mFinalClass;

	private final static List<ParameterInfo<Object>>	sParameters;

	private final static Method							sBuildMethod;

	static {
		try {
			sBuildMethod = PropertiesFactory.class.getMethod("build");

			List<ParameterInfo<Object>> list = new ArrayList<>();

			list.add(new StdParameterInfo<>("*", ParameterType.MAP, String.class, String.class,
				PropertiesFactory.class.getMethod("add", Object.class, Object.class)));

			sParameters = Collections.unmodifiableList(list);
		}
		catch (NoSuchMethodException | SecurityException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Default constructor
	 * 
	 * @param pFinalClass
	 */
	public PropertiesClassInfo(Class<O> pFinalClass) {
		mFinalClass = pFinalClass;
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

		@SuppressWarnings("nullness")
		Object builder = new PropertiesFactory();

		BuilderInfo<Object, O> builderInfo = new StdBuilderInfo<>(sBuildMethod, sParameters);

		return new Pair<Object, BuilderInfo<Object, O>>(builder, builderInfo);
	}

}
