package com.diamondq.common.config.builders;

import com.diamondq.common.config.core.ConfigImpl;
import com.diamondq.common.config.spi.BuilderInfo;
import com.diamondq.common.config.spi.ClassInfo;
import com.diamondq.common.config.spi.Pair;
import com.diamondq.common.config.spi.StdNoopBuilderInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nonnull;

public class NoParamClassInfo<O> implements ClassInfo<Object, O> {

    private Constructor<?> mConstructor;
    private final Class<O> mFinalClass;

    public NoParamClassInfo(Class<O> pFinalClass, Constructor<?> pConstructor) {
        mFinalClass = pFinalClass;
        mConstructor = pConstructor;
    }

    /**
     * @see com.diamondq.common.config.spi.ClassInfo#getFinalClass()
     */
    @Override
    public Class<O> getFinalClass() {
        return mFinalClass;
    }

    /**
     * @see com.diamondq.common.config.spi.ClassInfo#builder(com.diamondq.common.config.core.ConfigImpl)
     */
    @Override
    public Pair<Object, BuilderInfo<Object, O>> builder(ConfigImpl pConfigImpl)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

        @SuppressWarnings("nullness")
        @Nonnull
        Object builder = mConstructor.newInstance((Object[]) null);

        BuilderInfo<Object, O> builderInfo = new StdNoopBuilderInfo<O>();

        return new Pair<Object, BuilderInfo<Object, O>>(builder, builderInfo);

    }

}
