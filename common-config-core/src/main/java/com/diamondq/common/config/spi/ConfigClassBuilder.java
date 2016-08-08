package com.diamondq.common.config.spi;

import java.util.List;

public interface ConfigClassBuilder extends ConfigReconstructable {

	public <T, O> ClassInfo<T, O> getClassInfo(Class<?> pClass, NodeType pType,
		List<ConfigClassBuilder> pClassBuilders);

	public <T, O> BuilderInfo<T, O> getBuilderInfo(ClassInfo<T, O> pClassInfo, T pBuilder);
}
