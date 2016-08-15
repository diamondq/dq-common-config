package com.diamondq.common.config.spi;

import java.lang.reflect.InvocationTargetException;

public interface ParameterInfo<T> {

    public static enum ParameterType {
        NORMAL, LIST, MAP
    };

    public String getName();

    public ParameterType getType();

    public Class<?> getClassType1();

    public Class<?> getClassType2();

    public void set1(T pBuilder, Object pValue1) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

    public void set2(T pBuilder, Object pValue1, Object pValue2)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
