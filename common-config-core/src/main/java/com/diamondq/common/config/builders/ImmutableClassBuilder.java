package com.diamondq.common.config.builders;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.ConfigKey;
import com.diamondq.common.config.core.ConfigImpl;
import com.diamondq.common.config.spi.BuilderInfo;
import com.diamondq.common.config.spi.ClassInfo;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.NodeType;
import com.diamondq.common.config.spi.ParameterInfo;
import com.diamondq.common.config.spi.StdBuilderInfo;
import com.diamondq.common.config.spi.StdParameterInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ImmutableClassBuilder implements ConfigClassBuilder {

	private static final Set<String> sIgnorableBuilderMethods;

	static {
		Set<String> builder = new HashSet<>();
		builder.add("build");
		builder.add("from");
		builder.add("wait");
		builder.add("equals");
		builder.add("toString");
		builder.add("hashCode");
		builder.add("getClass");
		builder.add("notify");
		builder.add("notifyAll");
		sIgnorableBuilderMethods = Collections.unmodifiableSet(builder);
	}

	private final Map<Class<?>, BuilderInfo<?, ?>> mCachedBuilderMap = new ConcurrentHashMap<>();

	public ImmutableClassBuilder() {
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
	 * @see com.diamondq.common.config.spi.ConfigClassBuilder#getClassInfo(java.lang.Class,
	 *      com.diamondq.common.config.spi.NodeType, java.util.List)
	 */
	@Override
	public <T, O> ClassInfo<T, O> getClassInfo(Class<?> pClass, NodeType pType,
		List<ConfigClassBuilder> pClassBuilders) {

		boolean hasFactoryArg = false;
		if ((pType.getFactoryArg().isPresent() == true) && (pType.getFactoryArg().get().getValue().isPresent() == true))
			hasFactoryArg = true;

		Method builderMethod = null;
		int constructorArgPos = -1;
		int configArgPos = -1;
		method: for (Method m : pClass.getMethods()) {

			/* Look for an Immutable's builder method or a builder that takes a ConfigImpl or a Config */

			if (m.getName().equals("builder")) {
				int modifiers = m.getModifiers();
				if ((Modifier.isStatic(modifiers) == true) && (Modifier.isPublic(modifiers) == true)) {
					if (hasFactoryArg == true) {
						Class<?>[] parameterTypes = m.getParameterTypes();
						if (parameterTypes.length > 0)
							for (int paramOffset = 0; paramOffset < parameterTypes.length; paramOffset++) {
								Class<?> pt = parameterTypes[paramOffset];
								if ((pt.isAssignableFrom(ConfigImpl.class) == true)
									|| (pt.isAssignableFrom(Config.class) == true)) {
									if (configArgPos == -1)
										configArgPos = paramOffset;
									else
										continue method;
								}
								else if (pt.isAssignableFrom(String.class) == true) {
									if (constructorArgPos == -1)
										constructorArgPos = paramOffset;
									else
										continue method;
								}
								else
									continue method;
							}
					}
					else {

						/* Make sure that any parameter is a Config/ConfigImpl */

						Class<?>[] parameterTypes = m.getParameterTypes();
						if (parameterTypes.length > 0)
							for (int paramOffset = 0; paramOffset < parameterTypes.length; paramOffset++) {
								Class<?> pt = parameterTypes[paramOffset];
								if ((pt.isAssignableFrom(ConfigImpl.class) == true)
									|| (pt.isAssignableFrom(Config.class) == true)) {
									if (configArgPos == -1)
										configArgPos = paramOffset;
									else
										continue method;
								}
							}
					}
					builderMethod = m;
					break;
				}
			}
		}

		/* If there is no builder method, then this ClassBuilder doesn't support this class */

		if (builderMethod == null)
			return null;

		Object constructorArg;
		if (hasFactoryArg == true)
			constructorArg = pType.getFactoryArg().get().getValue().get();
		else
			constructorArg = null;

		@SuppressWarnings("unchecked")
		ClassInfo<T, O> result = (ClassInfo<T, O>) new ImmutableClassInfo<O>(this, builderMethod, constructorArg,
			constructorArgPos, configArgPos);
		return result;
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigClassBuilder#getBuilderInfo(com.diamondq.common.config.spi.ClassInfo,
	 *      java.lang.Object)
	 */
	@Override
	public <T, O> BuilderInfo<T, O> getBuilderInfo(ClassInfo<T, O> pClassInfo, T pBuilder) {

		Class<?> builderClass = pBuilder.getClass();

		/* See if we have a cached copy of the builder info */

		BuilderInfo<?, ?> builderInfo = mCachedBuilderMap.get(builderClass);
		if (builderInfo != null) {
			@SuppressWarnings("unchecked")
			BuilderInfo<T, O> result = (BuilderInfo<T, O>) builderInfo;
			return result;
		}

		/* Now start traversing through the details for the builder to get all the information */

		Method buildMethod = null;
		List<ParameterInfo<Object>> paramList = new ArrayList<>();
		for (Method m : builderClass.getMethods()) {
			String name = m.getName();

			if (name.equals("build")) {
				buildMethod = m;
				continue;
			}

			/* Ignore build(), from() methods */

			if (sIgnorableBuilderMethods.contains(name) == true)
				continue;

			/* Make sure it only takes one parameter, and that parameter is not an Optional */

			Class<?>[] types = m.getParameterTypes();
			if (types.length != 1)
				continue;
			if (types[0] != Object.class) {
				if (types[0].isAssignableFrom(Optional.class) == true)
					continue;
			}

			/* Ignore any method that takes an Iterable or an Array */

			if (types[0].isArray() == true)
				continue;

			/* If it starts with 'add', and there's a matching Iterable with the subname, then skip it */

			if (name.startsWith("add") == true) {
				String subName = name.substring(3, 4).toLowerCase().concat(name.substring(4)).concat("s");
				try {
					builderClass.getMethod(subName, Iterable.class);
					continue;
				}
				catch (NoSuchMethodException | SecurityException ex) {
				}
			}

			/* If it starts with 'addAll', and there's a matching Iterable with the subname, then skip it */

			if (name.startsWith("addAll") == true) {
				String subName = name.substring(6, 7).toLowerCase().concat(name.substring(7));
				try {
					builderClass.getMethod(subName, Iterable.class);
					continue;
				}
				catch (NoSuchMethodException | SecurityException ex) {
				}
			}

			/* Check if there is a ConfigKey annotation override */

			ConfigKey annotation = m.getAnnotation(ConfigKey.class);
			if (annotation != null)
				name = annotation.value();

			/* This represents a parameter */

			ParameterInfo<Object> info = new StdParameterInfo<>(name, types[0], m);
			paramList.add(info);
		}

		/* If there is no build method, then this isn't supported by this builder */

		if (buildMethod == null)
			return null;

		List<ParameterInfo<Object>> immutableParamList = Collections.unmodifiableList(paramList);
		@SuppressWarnings("unchecked")
		BuilderInfo<T, O> result = (BuilderInfo<T, O>) new StdBuilderInfo<O>(buildMethod, immutableParamList);
		return result;
	}

}