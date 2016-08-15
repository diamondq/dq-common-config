package com.diamondq.common.config.builders;

import com.diamondq.common.config.core.I18NRuntimeException;
import com.diamondq.common.config.spi.BuilderInfo;
import com.diamondq.common.config.spi.ClassInfo;
import com.diamondq.common.config.spi.ConfigClassBuilder;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.NodeType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Priority;
import javax.inject.Singleton;

@Singleton
@Priority(200)
public class ListClassBuilder implements ConfigClassBuilder {

    public ListClassBuilder() {

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

        /* Only support Iterables and Lists */

        if ((pClass.isAssignableFrom(Iterable.class) == false) && (pClass.isAssignableFrom(List.class) == false))
            return null;

        if ((pType.getFactory().isPresent() == true) && (pType.getFactory().get().getValue().isPresent() == true)) {

            String factoryClass = pType.getFactory().get().getValue().get();
            try {
                pClass = Class.forName(factoryClass);
            } catch (ClassNotFoundException ex) {
                throw new I18NRuntimeException("bootstrap.factorynotfound", factoryClass, pType.getFactory().get().getConfigSource());
            }

        } else {

            pClass = ListBuilderFactory.class;

        }

        ClassInfo<?, ?> childResult = null;
        for (ConfigClassBuilder ccb : pClassBuilders) {
            childResult = ccb.getClassInfo(pClass, pFinalClass, pType, pClassBuilders);
            if (childResult != null)
                break;
        }

        @SuppressWarnings("unchecked")
        ClassInfo<T, O> result = (ClassInfo<T, O>) childResult;
        return result;

    }

    /**
     * @see com.diamondq.common.config.spi.ConfigClassBuilder#getBuilderInfo(com.diamondq.common.config.spi.ClassInfo,
     *      java.lang.Object)
     */
    @Override
    public <T, O> BuilderInfo<T, O> getBuilderInfo(ClassInfo<T, O> pClassInfo, T pBuilder) {
        throw new UnsupportedOperationException();
    }

}
