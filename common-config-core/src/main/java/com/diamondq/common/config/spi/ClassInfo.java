package com.diamondq.common.config.spi;

import com.diamondq.common.config.core.ConfigImpl;

import java.lang.reflect.InvocationTargetException;

public interface ClassInfo<T, O> {

    /**
     * Returns the class representing the final object
     * 
     * @return the final object class
     */
    public Class<O> getFinalClass();

    public Pair<T, BuilderInfo<T, O>> builder(ConfigImpl pConfigImpl)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException;

}
