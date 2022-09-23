// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.Collection;
import java.util.List;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.Iterator;

public interface ImmutableConfiguration
{
    boolean isEmpty();
    
    int size();
    
    boolean containsKey(final String p0);
    
    Object getProperty(final String p0);
    
    Iterator<String> getKeys(final String p0);
    
    Iterator<String> getKeys();
    
    Properties getProperties(final String p0);
    
    boolean getBoolean(final String p0);
    
    boolean getBoolean(final String p0, final boolean p1);
    
    Boolean getBoolean(final String p0, final Boolean p1);
    
    byte getByte(final String p0);
    
    byte getByte(final String p0, final byte p1);
    
    Byte getByte(final String p0, final Byte p1);
    
    double getDouble(final String p0);
    
    double getDouble(final String p0, final double p1);
    
    Double getDouble(final String p0, final Double p1);
    
    float getFloat(final String p0);
    
    float getFloat(final String p0, final float p1);
    
    Float getFloat(final String p0, final Float p1);
    
    int getInt(final String p0);
    
    int getInt(final String p0, final int p1);
    
    Integer getInteger(final String p0, final Integer p1);
    
    long getLong(final String p0);
    
    long getLong(final String p0, final long p1);
    
    Long getLong(final String p0, final Long p1);
    
    short getShort(final String p0);
    
    short getShort(final String p0, final short p1);
    
    Short getShort(final String p0, final Short p1);
    
    BigDecimal getBigDecimal(final String p0);
    
    BigDecimal getBigDecimal(final String p0, final BigDecimal p1);
    
    BigInteger getBigInteger(final String p0);
    
    BigInteger getBigInteger(final String p0, final BigInteger p1);
    
    String getString(final String p0);
    
    String getString(final String p0, final String p1);
    
    String getEncodedString(final String p0, final ConfigurationDecoder p1);
    
    String getEncodedString(final String p0);
    
    String[] getStringArray(final String p0);
    
    List<Object> getList(final String p0);
    
    List<Object> getList(final String p0, final List<?> p1);
    
     <T> T get(final Class<T> p0, final String p1);
    
     <T> T get(final Class<T> p0, final String p1, final T p2);
    
    Object getArray(final Class<?> p0, final String p1);
    
    @Deprecated
    Object getArray(final Class<?> p0, final String p1, final Object p2);
    
     <T> List<T> getList(final Class<T> p0, final String p1);
    
     <T> List<T> getList(final Class<T> p0, final String p1, final List<T> p2);
    
     <T> Collection<T> getCollection(final Class<T> p0, final String p1, final Collection<T> p2);
    
     <T> Collection<T> getCollection(final Class<T> p0, final String p1, final Collection<T> p2, final Collection<T> p3);
    
    ImmutableConfiguration immutableSubset(final String p0);
}
