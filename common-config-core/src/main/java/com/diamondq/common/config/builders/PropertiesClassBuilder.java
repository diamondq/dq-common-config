package com.diamondq.common.config.builders;

import com.diamondq.common.config.spi.BuilderInfo;
import com.diamondq.common.config.spi.ClassInfo;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.NodeType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Priority;
import javax.inject.Singleton;

@Singleton
@Priority(10)
public class PropertiesClassBuilder implements ConfigClassBuilder {

    public PropertiesClassBuilder() {

    }

    /**
     * @see com.diamondq.common.config.spi.ConfigReconstructable#getReconstructionNodeType()
     */
    @Override
    public NodeType getReconstructionNodeType() {
        return NodeType.builder().isExplicitType(true).type(ConfigProp.builder().configSource("").value(getClass().getName()).build()).build();
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
     *      com.diamondq.common.config.spi.NodeType, java.util.List)
     */
    @Override
    public <T, O> ClassInfo<T, O> getClassInfo(Class<?> pClass, Class<O> pFinalClass, NodeType pType,
        List<ConfigClassBuilder> pClassBuilders) {

        boolean hasFactoryArg = false;
        if ((pType.getFactoryArg().isPresent() == true) && (pType.getFactoryArg().get().getValue().isPresent() == true))
            hasFactoryArg = true;

        /* This only supports no-argument constructors */

        if (hasFactoryArg == true)
            return null;

        if (pClass.isAssignableFrom(Properties.class) == false)
        	return null;
        
        @SuppressWarnings("unchecked")
		ClassInfo<T, O> result = (ClassInfo<T, O>) new PropertiesClassInfo<O>(pFinalClass);
        return result;
    }

    /**
     * @see com.diamondq.common.config.spi.ConfigClassBuilder#getBuilderInfo(com.diamondq.common.config.spi.ClassInfo,
     *      java.lang.Object)
     */
    @Override
    public <T, O> BuilderInfo<T, O> getBuilderInfo(ClassInfo<T, O> pClassInfo, T pBuilder) {
        // TODO Auto-generated method stub
        return null;
    }

}
