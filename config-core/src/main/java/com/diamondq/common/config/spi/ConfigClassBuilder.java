package com.diamondq.common.config.spi;

import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a handler that can create a builder to support a given class.
 */
public interface ConfigClassBuilder extends ConfigReconstructable {

	/**
	 * Returns the class information if this builder can be used for this class. If it can't be used, it returns null.
	 * 
	 * @param pClass the class to check
	 * @param pFinalClass the expected final class
	 * @param pType the node type
	 * @param pClassBuilders the list of builders
	 * @param pContext the context
	 * @param <T> the 'state' object
	 * @param <O> the 'final' object
	 * @return the class info
	 */
	public <@NonNull T, @NonNull O> @Nullable ClassInfo<T, O> getClassInfo(Class<?> pClass, Class<O> pFinalClass,
		NodeType pType, List<ConfigClassBuilder> pClassBuilders, @Nullable Map<String, Object> pContext);

	/**
	 * @param pClassInfo the class info
	 * @param pBuilder the builder
	 * @param <T> the 'state' object
	 * @param <O> the 'final' object
	 * @return the builder information
	 */
	public <@NonNull T, @NonNull O> BuilderInfo<T, O> getBuilderInfo(ClassInfo<T, O> pClassInfo, T pBuilder);
}
