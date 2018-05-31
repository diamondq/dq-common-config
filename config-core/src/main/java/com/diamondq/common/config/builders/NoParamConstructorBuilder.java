package com.diamondq.common.config.builders;

import com.diamondq.common.config.spi.BuilderInfo;
import com.diamondq.common.config.spi.ClassInfo;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.NodeType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The builder for classes that take no parameter constructors
 */
@ApplicationScoped
@Priority(100)
public class NoParamConstructorBuilder implements ConfigClassBuilder {

	/**
	 * The default constructor
	 */
	public NoParamConstructorBuilder() {

	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigReconstructable#getReconstructionNodeType()
	 */
	@Override
	public NodeType getReconstructionNodeType() {
		return NodeType.builder().isExplicitType(true)
			.type(ConfigProp.builder().configSource("").value(getClass().getName()).build()).build();
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigReconstructable#getReconstructionParams()
	 */
	@Override
	public Map<String, String> getReconstructionParams() {
		return Collections.emptyMap();
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigClassBuilder#getClassInfo(java.lang.Class, java.lang.Class,
	 *      com.diamondq.common.config.spi.NodeType, java.util.List, java.util.Map)
	 */
	@Override
	public <@NonNull T, @NonNull O> @Nullable ClassInfo<T, O> getClassInfo(Class<?> pClass, Class<O> pFinalClass,
		NodeType pType, List<ConfigClassBuilder> pClassBuilders, @Nullable Map<String, Object> pContext) {

		boolean hasFactoryArg = false;
		if ((pType.getFactoryArg().isPresent() == true) && (pType.getFactoryArg().get().getValue().isPresent() == true))
			hasFactoryArg = true;

		/* This only supports no-argument constructors */

		if (hasFactoryArg == true)
			return null;

		/* Only support non-private, concrete classes */

		int classModifiers = pClass.getModifiers();
		if ((pClass.isInterface() == true) || (Modifier.isAbstract(classModifiers) == true)
			|| (Modifier.isPrivate(classModifiers) == true))
			return null;

		@Nullable
		Constructor<?> constructor = null;
		for (Constructor<?> c : pClass.getConstructors()) {
			Class<?>[] parameterTypes = c.getParameterTypes();
			if (parameterTypes.length == 0) {
				int modifiers = c.getModifiers();
				if (Modifier.isPublic(modifiers) == true) {
					constructor = c;
					break;
				}
			}
		}

		if (constructor == null)
			return null;

		@SuppressWarnings("unchecked")
		ClassInfo<T, O> result = (ClassInfo<T, O>) new NoParamClassInfo<O>(pFinalClass, constructor);
		return result;
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigClassBuilder#getBuilderInfo(com.diamondq.common.config.spi.ClassInfo,
	 *      java.lang.Object)
	 */
	@Override
	public <@NonNull T, @NonNull O> BuilderInfo<T, O> getBuilderInfo(ClassInfo<T, O> pClassInfo, T pBuilder) {
		throw new UnsupportedOperationException();
	}

}
