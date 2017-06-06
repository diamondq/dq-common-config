package com.diamondq.common.config.core;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.spi.BuilderInfo;
import com.diamondq.common.config.spi.ClassInfo;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigNode;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.NodeType;
import com.diamondq.common.config.spi.Pair;
import com.diamondq.common.config.spi.ParameterInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigImpl implements Config {

	private static final Logger								sLogger					=
		LoggerFactory.getLogger(ConfigImpl.class);

	private final ConfigNode								mConfigNode;

	private final Map<String, ClassInfo<Object, Object>>	mClassToConstructorMap	= new ConcurrentHashMap<>();

	private final List<ConfigClassBuilder>					mClassBuilders;

	private final ThreadLocal<Locale>						mThreadLocale			= new ThreadLocal<Locale>() {
																						@Override
																						protected Locale initialValue() {
																							return Locale.getDefault();
																						}
																					};

	public ConfigImpl(ConfigNode pConfigNode, List<ConfigClassBuilder> pClassBuilders) {

		mConfigNode = pConfigNode;
		mClassBuilders = pClassBuilders;
	}

	/**
	 * @see com.diamondq.common.config.Config#setThreadLocale(java.util.Locale)
	 */
	@Override
	public void setThreadLocale(Locale pLocale) {
		mThreadLocale.set(pLocale);
	}

	/**
	 * @see com.diamondq.common.config.Config#bind(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> T bind(String pPrefix, Class<T> pClass) {
		return bind(pPrefix, pClass, null);
	}

	/**
	 * @see com.diamondq.common.config.Config#bind(java.lang.String, java.lang.Class, java.util.Map)
	 */
	@Override
	public <T> T bind(String pPrefix, Class<T> pClass, Map<String, Object> pContext) {

		sLogger.trace("Config binding {} to {}...", pPrefix, pClass);

		String[] prefixKeys = pPrefix.split("\\.");
		ConfigNode node = findNode(prefixKeys);
		if (node == null)
			return null;

		return internalBind(node, pClass, pContext);
	}

	@SuppressWarnings("unchecked")
	private <T> T internalBind(ConfigNode pNode, Class<T> pClass, Map<String, Object> pContext) {

		DebugUtils.trace("", pNode);

		Class<?> buildClass = null;
		NodeType type = pNode.getType();
		Class<T> finalClass = pClass;

		/* If there is a factory, then the factory provides the buildClass */

		if ((type.getFactory().isPresent() == true) && (type.getFactory().get().getValue().isPresent() == true))
			buildClass = resolveType(type.getFactory().get().getValue().get());

		if ((buildClass == null) && (type.getType().isPresent() == true)
			&& (type.getType().get().getValue().isPresent() == true))
			buildClass = resolveType(type.getType().get().getValue().get());

		/* If the final class is an Object, then hopefully there is type information that we can use */

		if (finalClass == Object.class) {
			if ((type.getType().isPresent() == true) && (type.getType().get().getValue().isPresent() == true))
				finalClass = (Class<T>) resolveType(type.getType().get().getValue().get());
			else if (type.isExplicitType() == false)
				finalClass = (Class<T>) String.class;
			else
				throw new IllegalArgumentException();
		}

		/* If there still isn't a build class, then use the final class */

		if (buildClass == null)
			buildClass = finalClass;

		/* If we're dealing with a primitive, then just resolve the value */

		if (isPrimitive(buildClass) == true)
			return resolveValue(pNode, pClass);

		T result = internalBind2(pNode, buildClass, finalClass, pContext);
		return result;
	}

	private <T> T internalBind2(ConfigNode pNode, Class<?> pClass, Class<T> pFinalClass, Map<String, Object> pContext) {
		try {

			NodeType type = pNode.getType();

			String classNameKey =
				new StringBuilder().append(pClass.getName()).append('/').append(type.getSimpleName()).toString();
			@SuppressWarnings("unchecked")
			ClassInfo<Object, T> classInfo = (ClassInfo<Object, T>) mClassToConstructorMap.get(classNameKey);
			if (classInfo == null)
				classInfo = lookupClassInfo(pClass, pFinalClass, type, classNameKey, pContext);

			/* Create a builder */

			Pair<Object, BuilderInfo<Object, T>> builderPair = classInfo.builder(this);
			Object builder = builderPair._1;
			BuilderInfo<Object, T> builderInfo = builderPair._2;

			List<ParameterInfo<Object>> parameters = builderInfo.getParameters();
			Map<String, ConfigNode> children = pNode.getChildren();
			for (ParameterInfo<Object> p : parameters) {
				String origName = p.getName();
				if ("*".equals(origName)) {

					/* Sort the keys */

					SortedSet<String> sortedKeys = new TreeSet<>();
					sortedKeys.addAll(children.keySet());

					/* If there are two types, then treat it like a Map */

					Class<?> typeClass = p.getClassType1();
					Class<?> mapValueClass = p.getClassType2();
					if (mapValueClass == null) {
						for (String k : sortedKeys) {
							Object result = isPrimitive(typeClass) ? resolveValue(children.get(k), typeClass)
								: internalBind(children.get(k), typeClass, pContext);
							sLogger.trace("P: Name: {} Type: {} = {}", origName, typeClass, result);
							p.set1(builder, result);
						}
					}
					else {
						for (String k : sortedKeys) {
							Object result = isPrimitive(mapValueClass) ? resolveValue(children.get(k), mapValueClass)
								: internalBind(children.get(k), mapValueClass, pContext);
							sLogger.trace("P: Name: {} Type: {} = {}", k, mapValueClass, result);
							p.set2(builder, k, result);
						}
					}
				}
				else {
					Set<String> names = generateAlternateNames(origName);
					boolean match = false;
					for (String name : names) {
						if (children.containsKey(name)) {

							switch (p.getType()) {
							case NORMAL: {
								Class<?> typeClass = p.getClassType1();
								Object result = isPrimitive(typeClass) ? resolveValue(children.get(name), typeClass)
									: internalBind(children.get(name), typeClass, pContext);
								sLogger.trace("P: Name: {} -> {} Type: {} = {}", origName, name, typeClass, result);
								p.set1(builder, result);
								match = true;
								break;
							}
							case LIST: {
								Class<?> valueClass = p.getClassType1();
								boolean isPrimitive = isPrimitive(valueClass);
								ConfigNode listChildren = children.get(name);
								if ((listChildren.getType().getFactory().isPresent() == true)
									&& (listChildren.getType().getFactory().get().getValue().isPresent() == true)) {

									/* There is a factory, so resolve that first to get the initial set of values */

									@SuppressWarnings("rawtypes")
									List initialList = internalBind(listChildren, List.class, pContext);
									for (Object o : initialList)
										p.set1(builder, o);
								}
								for (Map.Entry<String, ConfigNode> childPair : listChildren.getChildren().entrySet()) {
									Object listResult = isPrimitive ? resolveValue(childPair.getValue(), valueClass)
										: internalBind(childPair.getValue(), valueClass, pContext);
									sLogger.trace("P: Name: {} -> {} Type: {} = {}", origName, name, valueClass,
										listResult);
									p.set1(builder, listResult);
								}
								match = true;
								break;
							}
							case MAP: {
								Class<?> keyClass = p.getClassType1();
								Class<?> mapValueClass = p.getClassType2();
								ConfigNode mapChildren = children.get(name);
								boolean isPrimitive = isPrimitive(mapValueClass);
								for (Map.Entry<String, ConfigNode> childPair : mapChildren.getChildren().entrySet()) {
									Object key = convertType(childPair.getKey(), keyClass);
									Object value = isPrimitive ? resolveValue(childPair.getValue(), mapValueClass)
										: internalBind(childPair.getValue(), mapValueClass, pContext);
									p.set2(builder, key, value);
								}
								match = true;
								break;
							}
							default:
								throw new UnsupportedOperationException();
							}
							if (match == true)
								break;
						}

						/* See if the context has the child */

						else if ((pContext != null) && (pContext.containsKey(name))) {

							switch (p.getType()) {
							case NORMAL: {
								Class<?> typeClass = p.getClassType1();
								Object result = convertType(pContext.get(name), typeClass);
								sLogger.trace("P: Name: {} -> {} Type: {} = {}", origName, name, typeClass, result);
								p.set1(builder, result);
								match = true;
								break;
							}
							default:
								throw new UnsupportedOperationException();
							}
							if (match == true)
								break;
						}
					}
					if (match == false)
						sLogger.trace("P: Name: {} Type: {} = null", origName, p.getClassType1());
				}
			}

			/* Now, finally perform the build() constructor */

			T result = builderInfo.build(builder);
			return result;
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException
			| InstantiationException ex) {
			throw new RuntimeException(ex);
		}
	}

	private <T> T resolveValue(ConfigNode pNode, Class<T> pType) {

		NodeType type = pNode.getType();

		if (pNode.getChildren().isEmpty() == false) {

			/*
			 * If there are children, but we're attempting to generate a value, then we need to flatten the children. We
			 * also assume that the parent has no meaningful value. We'll also assume that it's a one dimensional list
			 * of elements. Thus, the key is ignored, and the value must not have any children.
			 */

			StringBuilder flattenedValue = new StringBuilder();
			boolean first = true;
			for (ConfigNode child : pNode.getChildren().values()) {
				if (child.getChildren().isEmpty() == false)
					throw new IllegalArgumentException();

				String v = resolveValue(child, String.class);
				if (v != null) {
					if (first == true)
						first = false;
					else
						flattenedValue.append(',');
					flattenedValue.append(v);
				}
			}

			return convertType(flattenedValue.toString(), pType);
		}

		Optional<ConfigProp> value = pNode.getValue();
		if ((value.isPresent() == false) || (value.get().getValue().isPresent() == false))
			return null;

		String str = value.get().getValue().get();

		/* First, convert the type into the node type */
		Object nodeValue;

		if (type.getFactory().isPresent()) {
			Class<?> factoryClass = resolveType(type.getFactory().get().getValue().get());
			if (type.getFactoryArg().isPresent()) {
				try {
					Method method = factoryClass.getMethod("create", String.class, String.class);
					nodeValue = method.invoke(null, type.getFactoryArg().get().getValue().get(), str);
				}
				catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException ex) {
					throw new RuntimeException(ex);
				}
			}
			else {
				try {
					Method method = factoryClass.getMethod("create", String.class);
					nodeValue = method.invoke(null, str);
				}
				catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
		else if ((type.getType().isPresent() == true) && (type.getType().get().getValue().isPresent() == true)) {
			Class<?> typeClass = resolveType(type.getType().get().getValue().get());
			nodeValue = convertType(str, typeClass);
		}
		else if (type.isExplicitType() == false)
			nodeValue = str;
		else
			throw new IllegalStateException();

		/* Now convert the node value into the requested value */

		return convertType(nodeValue, pType);
	}

	private Class<?> resolveType(String pType) {
		try {
			return Class.forName(pType);
		}
		catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T convertType(Object pValue, Class<T> pTypeClass) {
		if (pTypeClass.isInstance(pValue))
			return (T) pValue;
		String str = (pValue instanceof String ? (String) pValue : pValue.toString());
		if (pTypeClass.isAssignableFrom(String.class))
			return (T) str;
		if ((pTypeClass.isAssignableFrom(Long.class)) || (pTypeClass.isAssignableFrom(long.class)))
			return (T) Long.valueOf(str);
		if ((pTypeClass.isAssignableFrom(Integer.class)) || (pTypeClass.isAssignableFrom(int.class)))
			return (T) Integer.valueOf(str);
		if ((pTypeClass.isAssignableFrom(Float.class)) || (pTypeClass.isAssignableFrom(float.class)))
			return (T) Float.valueOf(str);
		if ((pTypeClass.isAssignableFrom(Double.class)) || (pTypeClass.isAssignableFrom(double.class)))
			return (T) Double.valueOf(str);
		if ((pTypeClass.isAssignableFrom(Boolean.class)) || (pTypeClass.isAssignableFrom(boolean.class)))
			return (T) Boolean.valueOf(str);
		if ((pTypeClass.isAssignableFrom(Short.class)) || (pTypeClass.isAssignableFrom(short.class)))
			return (T) Short.valueOf(str);
		throw new IllegalArgumentException();
	}

	private ConfigNode findNode(String[] pPrefixKeys) {
		ConfigNode n = mConfigNode;
		for (String k : pPrefixKeys) {
			n = n.getChildren().get(k);
			if (n == null)
				return null;
		}
		return n;
	}

	private Set<String> generateAlternateNames(String pName) {
		Set<String> result = new HashSet<>();
		result.add(pName);

		/* camelCase to camel-case */

		StringBuilder camelCase = new StringBuilder();
		char[] chars = pName.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (Character.isUpperCase(c) == true) {
				if (i > 0) {
					camelCase.append('-');
				}
				c = Character.toLowerCase(c);
			}
			camelCase.append(c);
		}
		result.add(camelCase.toString());
		return result;
	}

	private boolean isPrimitive(Class<?> pType) {
		if (pType == Object.class)
			return false;
		if (pType.isPrimitive() == true)
			return true;
		if ((pType.isAssignableFrom(Integer.class)) || (pType.isAssignableFrom(Short.class))
			|| (pType.isAssignableFrom(Boolean.class)) || (pType.isAssignableFrom(Long.class))
			|| (pType.isAssignableFrom(Float.class)) || (pType.isAssignableFrom(Double.class))
			|| (pType.isAssignableFrom(String.class)))
			return true;
		return false;
	}

	/**
	 * This is responsible to analyze a class and figure out the constructor, parameters, and builder methods.
	 * 
	 * @param pClass the class
	 * @param pFinalClass the final class
	 * @param pType the type
	 * @param pClassNameKey the class name key
	 * @param pContext the context
	 * @return the class info
	 */
	private <T> ClassInfo<Object, T> lookupClassInfo(Class<?> pClass, Class<T> pFinalClass, NodeType pType,
		String pClassNameKey, Map<String, Object> pContext) {

		ClassInfo<Object, T> result = null;
		for (ConfigClassBuilder ccb : mClassBuilders) {
			result = ccb.getClassInfo(pClass, pFinalClass, pType, mClassBuilders, pContext);
			if (result != null)
				break;
		}
		if (result == null)
			throw new IllegalStateException(
				String.format("Configuration support for the %s class is not supported.", pClass.getName()));
		@SuppressWarnings("unchecked")
		ClassInfo<Object, Object> putResult = (ClassInfo<Object, Object>) result;
		mClassToConstructorMap.put(pClassNameKey, putResult);
		return result;
	}
}
