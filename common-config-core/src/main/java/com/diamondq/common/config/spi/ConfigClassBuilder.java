package com.diamondq.common.config.spi;

public interface ConfigClassBuilder {

	public <T, O> ClassInfo<T, O> getClassInfo(Class<O> pClass, NodeType pType);

	public <T, O> BuilderInfo<T, O> getBuilderInfo(ClassInfo<T, O> pClassInfo, T pBuilder);
}
