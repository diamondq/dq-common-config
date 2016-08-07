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

	private final Map<Class<?>, ClassInfo<Object, Object>>	mClassToConstructorMap	= new ConcurrentHashMap<>();

	private final List<ConfigClassBuilder>					mClassBuilders;

	public ConfigImpl(ConfigNode pConfigNode, List<ConfigClassBuilder> pClassBuilders) {

		mConfigNode = pConfigNode;
		mClassBuilders = pClassBuilders;
	}

	/**
	 * @see com.diamondq.common.config.Config#bind(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> T bind(String pPrefix, Class<T> pClass) {

		sLogger.trace("Config binding {} to {}...", pPrefix, pClass);

		String[] prefixKeys = pPrefix.split("\\.");
		ConfigNode node = findNode(prefixKeys);
		if (node == null)
			return null;
		
		DebugUtils.trace("", node);
		
		return internalBind(node, pClass);
	}

	private <T> T internalBind(ConfigNode pNode, Class<T> pClass) {

		Class<?> buildClass;
		NodeType type = pNode.getType();

		if (pClass == Object.class) {

			/* We'll use the type of the node to determine the class */

			if (type.getFactory().isPresent() == true) {
				if ((type.getType().isPresent() == true) && (type.getType().get().getValue().isPresent() == true))
					buildClass = resolveType(type.getType().get().getValue().get());
				else
					buildClass = pClass;

				if (type.getFactoryArg().isPresent() == true)
					buildClass = resolveType(type.getFactory().get().getValue().get());
			}
			else if (type.getType().isPresent() == true) {
				buildClass = resolveType(type.getType().get().getValue().get());
			}
			else
				throw new IllegalArgumentException();
		}
		else
			buildClass = pClass;
		if (isPrimitive(buildClass) == true)
			return resolveValue(pNode, pClass);

		@SuppressWarnings("unchecked")
		T result = (T) internalBind2(pNode, buildClass);
		return result;
	}

	private <T> T internalBind2(ConfigNode pNode, Class<T> pClass) {
		try {

			NodeType type = pNode.getType();

			@SuppressWarnings("unchecked")
			ClassInfo<Object, T> classInfo = (ClassInfo<Object, T>) mClassToConstructorMap.get(pClass);
			if (classInfo == null)
				classInfo = lookupClassInfo(pClass, type);

			/* Create a builder */

			Pair<Object, BuilderInfo<Object, T>> builderPair = classInfo.builder();
			Object builder = builderPair._1;
			BuilderInfo<Object, T> builderInfo = builderPair._2;

			List<ParameterInfo<Object>> parameters = builderInfo.getParameters();
			Map<String, ConfigNode> children = pNode.getChildren();
			for (ParameterInfo<Object> p : parameters) {
				String origName = p.getName();
				Class<?> typeClass = p.getType();
				if ("*".equals(origName)) {

					/* Sort the keys */

					SortedSet<String> sortedKeys = new TreeSet<>();
					sortedKeys.addAll(children.keySet());

					if (isPrimitive(typeClass) == true) {
						for (String k : sortedKeys) {
							Object result = resolveValue(children.get(k), typeClass);
							sLogger.trace("P: Name: {} Type: {} = {}", origName, typeClass, result);
							p.set(builder, result);
						}
					}
					else {
						for (String k : sortedKeys) {
							Object result = internalBind(children.get(k), typeClass);
							sLogger.trace("P: Name: {} Type: {} = {}", origName, typeClass, result);
							p.set(builder, result);
						}
					}
				}
				else {
					Set<String> names = generateAlternateNames(origName);
					if (isPrimitive(typeClass) == true) {
						boolean match = false;
						for (String name : names) {
							if (children.containsKey(name)) {
								Object result = resolveValue(children.get(name), typeClass);
								sLogger.trace("P: Name: {} -> {} Type: {} = {}", names, name, typeClass, result);
								p.set(builder, result);
								match = true;
								break;
							}
						}
						if (match == false)
							sLogger.trace("P: Name: {} Type: {} = null", names, typeClass);
					}
					else {

						boolean match = false;
						for (String name : names) {
							if (children.containsKey(name)) {

								Object result = internalBind(children.get(name), typeClass);
								sLogger.trace("P: Name: {} -> {} Type: {} = {}", names, name, typeClass, result);
								p.set(builder, result);
								match = true;
								break;

							}
						}
						if (match == false)
							sLogger.trace("P: Name: {} Type: {} = null", names, typeClass);
					}
				}
			}

			/* Now, finally perform the build() constructor */

			T result = builderInfo.build(builder);
			return result;
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException ex) {
			throw new RuntimeException(ex);
		}
	}

	private <T> T resolveValue(ConfigNode pNode, Class<T> pType) {
		Optional<ConfigProp> value = pNode.getValue();
		if ((value.isPresent() == false) || (value.get().getValue().isPresent() == false))
			return null;

		String str = value.get().getValue().get();
		NodeType type = pNode.getType();

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
		if (pTypeClass.isAssignableFrom(Long.class))
			return (T) Long.valueOf(str);
		if (pTypeClass.isAssignableFrom(Integer.class))
			return (T) Integer.valueOf(str);
		if (pTypeClass.isAssignableFrom(Float.class))
			return (T) Float.valueOf(str);
		if (pTypeClass.isAssignableFrom(Double.class))
			return (T) Double.valueOf(str);
		if (pTypeClass.isAssignableFrom(Boolean.class))
			return (T) Boolean.valueOf(str);
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
	 * @return the class info
	 */
	private <T> ClassInfo<Object, T> lookupClassInfo(Class<T> pClass, NodeType pType) {
		if ((pClass.isAssignableFrom(Iterable.class) == true) || (pClass.isAssignableFrom(List.class) == true)) {
			@SuppressWarnings("unchecked")
			Class<T> reassignClass = (Class<T>) ListBuilderFactory.class;
			pClass = reassignClass;
		}

		ClassInfo<Object, T> result = null;
		for (ConfigClassBuilder ccb : mClassBuilders) {
			result = ccb.getClassInfo(pClass, pType);
			if (result != null)
				break;
		}
		if (result == null)
			throw new IllegalStateException(
				String.format("Configuration support for the %s class is not supported.", pClass.getName()));
		@SuppressWarnings("unchecked")
		ClassInfo<Object, Object> putResult = (ClassInfo<Object, Object>) result;
		mClassToConstructorMap.put(pClass, putResult);
		return result;
	}
}
