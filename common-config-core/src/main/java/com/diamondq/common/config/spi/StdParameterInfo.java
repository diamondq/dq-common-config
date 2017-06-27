package com.diamondq.common.config.spi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.checkerframework.checker.nullness.qual.Nullable;

public class StdParameterInfo<T> implements ParameterInfo<T> {

    private final String        mName;

    private final ParameterType mType;

    private final Class<?>      mClassType1;

    @Nullable
    private final Class<?>      mClassType2;

    private final Method        mMethod;

    public StdParameterInfo(String pName, ParameterType pType, Class<?> pClassType1, @Nullable Class<?> pClassType2, Method pMethod) {
        mName = pName;
        mType = pType;
        mClassType1 = pClassType1;
        mClassType2 = pClassType2;
        mMethod = pMethod;
    }

    /**
     * @see com.diamondq.common.config.spi.ParameterInfo#getName()
     */
    @Override
    public String getName() {
        return mName;
    }

    /**
     * @see com.diamondq.common.config.spi.ParameterInfo#getType()
     */
    @Override
    public com.diamondq.common.config.spi.ParameterInfo.ParameterType getType() {
        return mType;
    }

    /**
     * @see com.diamondq.common.config.spi.ParameterInfo#getClassType1()
     */
    @Override
    public Class<?> getClassType1() {
        return mClassType1;
    }

    /**
     * @see com.diamondq.common.config.spi.ParameterInfo#getClassType2()
     */
    @Override
    public @Nullable Class<?> getClassType2() {
        return mClassType2;
    }

    /**
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @see com.diamondq.common.config.spi.ParameterInfo#set1(java.lang.Object, java.lang.Object)
     */
    @Override
    public void set1(T pBuilder, Object pValue1) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        mMethod.invoke(pBuilder, pValue1);
    }

    /**
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @see com.diamondq.common.config.spi.ParameterInfo#set2(java.lang.Object, java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public void set2(T pBuilder, Object pValue1, Object pValue2)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        mMethod.invoke(pBuilder, pValue1, pValue2);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append('{');
        sb.append("name=");
        sb.append(mName);
        sb.append(",type=");
        sb.append(mType);
        sb.append(",type1=");
        sb.append(mClassType1);
        sb.append(",type2=");
        sb.append(mClassType2);
        sb.append(",method=");
        sb.append(mMethod);
        sb.append('}');
        return sb.toString();
    }
}
