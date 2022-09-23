// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.convert;

import org.apache.commons.lang3.ClassUtils;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collection;
import java.lang.reflect.Array;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;

public class DefaultConversionHandler implements ConversionHandler
{
    public static final DefaultConversionHandler INSTANCE;
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final AbstractListDelimiterHandler EXTRACTOR;
    private static final ConfigurationInterpolator NULL_INTERPOLATOR;
    private volatile String dateFormat;
    
    public String getDateFormat() {
        final String fmt = this.dateFormat;
        return (fmt != null) ? fmt : "yyyy-MM-dd HH:mm:ss";
    }
    
    public void setDateFormat(final String dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    @Override
    public <T> T to(final Object src, final Class<T> targetCls, final ConfigurationInterpolator ci) {
        final ConfigurationInterpolator interpolator = fetchInterpolator(ci);
        return (T)this.convert(interpolator.interpolate(src), (Class<Object>)targetCls, interpolator);
    }
    
    @Override
    public Object toArray(final Object src, final Class<?> elemClass, final ConfigurationInterpolator ci) {
        if (src == null) {
            return null;
        }
        if (this.isEmptyElement(src)) {
            return Array.newInstance(elemClass, 0);
        }
        final ConfigurationInterpolator interpolator = fetchInterpolator(ci);
        return elemClass.isPrimitive() ? this.toPrimitiveArray(src, elemClass, interpolator) : this.toObjectArray(src, elemClass, interpolator);
    }
    
    @Override
    public <T> void toCollection(final Object src, final Class<T> elemClass, final ConfigurationInterpolator ci, final Collection<T> dest) {
        if (dest == null) {
            throw new IllegalArgumentException("Target collection must not be null!");
        }
        if (src != null && !this.isEmptyElement(src)) {
            final ConfigurationInterpolator interpolator = fetchInterpolator(ci);
            this.convertToCollection(src, (Class<Object>)elemClass, interpolator, (Collection<Object>)dest);
        }
    }
    
    protected boolean isComplexObject(final Object src) {
        return src instanceof Iterator || src instanceof Iterable || (src != null && src.getClass().isArray());
    }
    
    protected boolean isEmptyElement(final Object src) {
        return src instanceof CharSequence && ((CharSequence)src).length() == 0;
    }
    
    protected <T> T convert(final Object src, final Class<T> targetCls, final ConfigurationInterpolator ci) {
        final Object conversionSrc = this.isComplexObject(src) ? this.extractConversionValue(src, targetCls, ci) : src;
        return (T)this.convertValue(ci.interpolate(conversionSrc), (Class<Object>)targetCls, ci);
    }
    
    protected Collection<?> extractValues(final Object source, final int limit) {
        return DefaultConversionHandler.EXTRACTOR.flatten(source, limit);
    }
    
    protected Collection<?> extractValues(final Object source) {
        return this.extractValues(source, Integer.MAX_VALUE);
    }
    
    protected Object extractConversionValue(final Object container, final Class<?> targetCls, final ConfigurationInterpolator ci) {
        final Collection<?> values = this.extractValues(container, 1);
        return values.isEmpty() ? null : ci.interpolate(values.iterator().next());
    }
    
    protected <T> T convertValue(final Object src, final Class<T> targetCls, final ConfigurationInterpolator ci) {
        if (src == null) {
            return null;
        }
        final T result = (T)PropertyConverter.to(targetCls, src, this);
        return result;
    }
    
    private <T> T[] toObjectArray(final Object src, final Class<T> elemClass, final ConfigurationInterpolator ci) {
        final Collection<T> convertedCol = new LinkedList<T>();
        this.convertToCollection(src, elemClass, ci, convertedCol);
        final T[] result = (T[])Array.newInstance(elemClass, convertedCol.size());
        return convertedCol.toArray(result);
    }
    
    private Object toPrimitiveArray(final Object src, final Class<?> elemClass, final ConfigurationInterpolator ci) {
        if (src.getClass().isArray()) {
            if (src.getClass().getComponentType().equals(elemClass)) {
                return src;
            }
            if (src.getClass().getComponentType().equals(ClassUtils.primitiveToWrapper(elemClass))) {
                final int length = Array.getLength(src);
                final Object array = Array.newInstance(elemClass, length);
                for (int i = 0; i < length; ++i) {
                    Array.set(array, i, Array.get(src, i));
                }
                return array;
            }
        }
        final Collection<?> values = this.extractValues(src);
        final Class<?> targetClass = ClassUtils.primitiveToWrapper(elemClass);
        final Object array2 = Array.newInstance(elemClass, values.size());
        int idx = 0;
        for (final Object value : values) {
            Array.set(array2, idx++, this.convertValue(ci.interpolate(value), targetClass, ci));
        }
        return array2;
    }
    
    private <T> void convertToCollection(final Object src, final Class<T> elemClass, final ConfigurationInterpolator ci, final Collection<T> dest) {
        for (final Object o : this.extractValues(ci.interpolate(src))) {
            dest.add(this.convert(o, elemClass, ci));
        }
    }
    
    private static ConfigurationInterpolator fetchInterpolator(final ConfigurationInterpolator ci) {
        return (ci != null) ? ci : DefaultConversionHandler.NULL_INTERPOLATOR;
    }
    
    static {
        INSTANCE = new DefaultConversionHandler();
        EXTRACTOR = (AbstractListDelimiterHandler)DisabledListDelimiterHandler.INSTANCE;
        NULL_INTERPOLATOR = new ConfigurationInterpolator() {
            @Override
            public Object interpolate(final Object value) {
                return value;
            }
        };
    }
}
