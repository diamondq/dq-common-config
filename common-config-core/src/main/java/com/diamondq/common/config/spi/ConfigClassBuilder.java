package com.diamondq.common.config.spi;

import java.util.List;
import java.util.Map;

public interface ConfigClassBuilder extends ConfigReconstructable {

    /**
     * @param pClass the class to use
     * @param pFinalClass the expected final class
     * @param pType the node type
     * @param pClassBuilders the list of builders
     * @param pContext the context
     * @return the class info
     */
    public <T, O> ClassInfo<T, O> getClassInfo(Class<?> pClass, Class<O> pFinalClass, NodeType pType,
		List<ConfigClassBuilder> pClassBuilders, Map<String, Object> pContext);

    public <T, O> BuilderInfo<T, O> getBuilderInfo(ClassInfo<T, O> pClassInfo, T pBuilder);
}
