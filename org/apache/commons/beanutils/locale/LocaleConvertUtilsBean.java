// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.beanutils.locale;

import java.util.Collection;
import java.util.Set;
import org.apache.commons.beanutils.locale.converters.SqlTimestampLocaleConverter;
import java.sql.Timestamp;
import org.apache.commons.beanutils.locale.converters.SqlTimeLocaleConverter;
import java.sql.Time;
import org.apache.commons.beanutils.locale.converters.SqlDateLocaleConverter;
import java.sql.Date;
import org.apache.commons.beanutils.locale.converters.StringLocaleConverter;
import org.apache.commons.beanutils.locale.converters.ShortLocaleConverter;
import org.apache.commons.beanutils.locale.converters.LongLocaleConverter;
import org.apache.commons.beanutils.locale.converters.IntegerLocaleConverter;
import org.apache.commons.beanutils.locale.converters.FloatLocaleConverter;
import org.apache.commons.beanutils.locale.converters.DoubleLocaleConverter;
import org.apache.commons.beanutils.locale.converters.ByteLocaleConverter;
import org.apache.commons.beanutils.locale.converters.BigIntegerLocaleConverter;
import java.math.BigInteger;
import org.apache.commons.beanutils.locale.converters.BigDecimalLocaleConverter;
import java.math.BigDecimal;
import java.lang.reflect.Array;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import java.util.Locale;

public class LocaleConvertUtilsBean
{
    private Locale defaultLocale;
    private boolean applyLocalized;
    private final Log log;
    private final FastHashMap mapConverters;
    
    public static LocaleConvertUtilsBean getInstance() {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getLocaleConvertUtils();
    }
    
    public LocaleConvertUtilsBean() {
        this.defaultLocale = Locale.getDefault();
        this.applyLocalized = false;
        this.log = LogFactory.getLog(LocaleConvertUtils.class);
        (this.mapConverters = new DelegateFastHashMap((Map)BeanUtils.createCache())).setFast(false);
        this.deregister();
        this.mapConverters.setFast(true);
    }
    
    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }
    
    public void setDefaultLocale(final Locale locale) {
        if (locale == null) {
            this.defaultLocale = Locale.getDefault();
        }
        else {
            this.defaultLocale = locale;
        }
    }
    
    public boolean getApplyLocalized() {
        return this.applyLocalized;
    }
    
    public void setApplyLocalized(final boolean newApplyLocalized) {
        this.applyLocalized = newApplyLocalized;
    }
    
    public String convert(final Object value) {
        return this.convert(value, this.defaultLocale, null);
    }
    
    public String convert(final Object value, final String pattern) {
        return this.convert(value, this.defaultLocale, pattern);
    }
    
    public String convert(final Object value, final Locale locale, final String pattern) {
        final LocaleConverter converter = this.lookup(String.class, locale);
        return converter.convert(String.class, value, pattern);
    }
    
    public Object convert(final String value, final Class<?> clazz) {
        return this.convert(value, clazz, this.defaultLocale, null);
    }
    
    public Object convert(final String value, final Class<?> clazz, final String pattern) {
        return this.convert(value, clazz, this.defaultLocale, pattern);
    }
    
    public Object convert(final String value, final Class<?> clazz, final Locale locale, final String pattern) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Convert string " + value + " to class " + clazz.getName() + " using " + locale + " locale and " + pattern + " pattern");
        }
        Class<?> targetClass = clazz;
        LocaleConverter converter = this.lookup(clazz, locale);
        if (converter == null) {
            converter = this.lookup(String.class, locale);
            targetClass = String.class;
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace("  Using converter " + converter);
        }
        return converter.convert(targetClass, value, pattern);
    }
    
    public Object convert(final String[] values, final Class<?> clazz, final String pattern) {
        return this.convert(values, clazz, this.getDefaultLocale(), pattern);
    }
    
    public Object convert(final String[] values, final Class<?> clazz) {
        return this.convert(values, clazz, this.getDefaultLocale(), null);
    }
    
    public Object convert(final String[] values, final Class<?> clazz, final Locale locale, final String pattern) {
        Class<?> type = clazz;
        if (clazz.isArray()) {
            type = clazz.getComponentType();
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Convert String[" + values.length + "] to class " + type.getName() + "[] using " + locale + " locale and " + pattern + " pattern");
        }
        final Object array = Array.newInstance(type, values.length);
        for (int i = 0; i < values.length; ++i) {
            Array.set(array, i, this.convert(values[i], type, locale, pattern));
        }
        return array;
    }
    
    public void register(final LocaleConverter converter, final Class<?> clazz, final Locale locale) {
        this.lookup(locale).put(clazz, converter);
    }
    
    public void deregister() {
        final FastHashMap defaultConverter = this.lookup(this.defaultLocale);
        this.mapConverters.setFast(false);
        this.mapConverters.clear();
        this.mapConverters.put(this.defaultLocale, defaultConverter);
        this.mapConverters.setFast(true);
    }
    
    public void deregister(final Locale locale) {
        this.mapConverters.remove(locale);
    }
    
    public void deregister(final Class<?> clazz, final Locale locale) {
        this.lookup(locale).remove(clazz);
    }
    
    public LocaleConverter lookup(final Class<?> clazz, final Locale locale) {
        final LocaleConverter converter = (LocaleConverter)this.lookup(locale).get(clazz);
        if (this.log.isTraceEnabled()) {
            this.log.trace("LocaleConverter:" + converter);
        }
        return converter;
    }
    
    @Deprecated
    protected FastHashMap lookup(final Locale locale) {
        FastHashMap localeConverters;
        if (locale == null) {
            localeConverters = (FastHashMap)this.mapConverters.get(this.defaultLocale);
        }
        else {
            localeConverters = (FastHashMap)this.mapConverters.get(locale);
            if (localeConverters == null) {
                localeConverters = this.create(locale);
                this.mapConverters.put(locale, localeConverters);
            }
        }
        return localeConverters;
    }
    
    @Deprecated
    protected FastHashMap create(final Locale locale) {
        final FastHashMap converter = new DelegateFastHashMap((Map)BeanUtils.createCache());
        converter.setFast(false);
        converter.put(BigDecimal.class, new BigDecimalLocaleConverter(locale, this.applyLocalized));
        converter.put(BigInteger.class, new BigIntegerLocaleConverter(locale, this.applyLocalized));
        converter.put(Byte.class, new ByteLocaleConverter(locale, this.applyLocalized));
        converter.put(Byte.TYPE, new ByteLocaleConverter(locale, this.applyLocalized));
        converter.put(Double.class, new DoubleLocaleConverter(locale, this.applyLocalized));
        converter.put(Double.TYPE, new DoubleLocaleConverter(locale, this.applyLocalized));
        converter.put(Float.class, new FloatLocaleConverter(locale, this.applyLocalized));
        converter.put(Float.TYPE, new FloatLocaleConverter(locale, this.applyLocalized));
        converter.put(Integer.class, new IntegerLocaleConverter(locale, this.applyLocalized));
        converter.put(Integer.TYPE, new IntegerLocaleConverter(locale, this.applyLocalized));
        converter.put(Long.class, new LongLocaleConverter(locale, this.applyLocalized));
        converter.put(Long.TYPE, new LongLocaleConverter(locale, this.applyLocalized));
        converter.put(Short.class, new ShortLocaleConverter(locale, this.applyLocalized));
        converter.put(Short.TYPE, new ShortLocaleConverter(locale, this.applyLocalized));
        converter.put(String.class, new StringLocaleConverter(locale, this.applyLocalized));
        converter.put(Date.class, new SqlDateLocaleConverter(locale, "yyyy-MM-dd"));
        converter.put(Time.class, new SqlTimeLocaleConverter(locale, "HH:mm:ss"));
        converter.put(Timestamp.class, new SqlTimestampLocaleConverter(locale, "yyyy-MM-dd HH:mm:ss.S"));
        converter.setFast(true);
        return converter;
    }
    
    private static class DelegateFastHashMap extends FastHashMap
    {
        private final Map<Object, Object> map;
        
        private DelegateFastHashMap(final Map<Object, Object> map) {
            this.map = map;
        }
        
        @Override
        public void clear() {
            this.map.clear();
        }
        
        @Override
        public boolean containsKey(final Object key) {
            return this.map.containsKey(key);
        }
        
        @Override
        public boolean containsValue(final Object value) {
            return this.map.containsValue(value);
        }
        
        @Override
        public Set<Map.Entry<Object, Object>> entrySet() {
            return this.map.entrySet();
        }
        
        @Override
        public boolean equals(final Object o) {
            return this.map.equals(o);
        }
        
        @Override
        public Object get(final Object key) {
            return this.map.get(key);
        }
        
        @Override
        public int hashCode() {
            return this.map.hashCode();
        }
        
        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }
        
        @Override
        public Set<Object> keySet() {
            return this.map.keySet();
        }
        
        @Override
        public Object put(final Object key, final Object value) {
            return this.map.put(key, value);
        }
        
        @Override
        public void putAll(final Map m) {
            this.map.putAll(m);
        }
        
        @Override
        public Object remove(final Object key) {
            return this.map.remove(key);
        }
        
        @Override
        public int size() {
            return this.map.size();
        }
        
        @Override
        public Collection<Object> values() {
            return this.map.values();
        }
        
        @Override
        public boolean getFast() {
            return BeanUtils.getCacheFast(this.map);
        }
        
        @Override
        public void setFast(final boolean fast) {
            BeanUtils.setCacheFast(this.map, fast);
        }
    }
}
