// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.config;

import java.util.List;
import java.util.Set;

public interface Config
{
    String getResource();
    
    Set<String> getNames();
    
    String getString(final String p0);
    
    String getString(final ConfigKey p0, final boolean p1);
    
    String getString(final String p0, final String p1);
    
    void setString(final String p0, final String p1);
    
    void setString(final ConfigKey p0, final String p1);
    
    String getTrimmed(final String p0);
    
    String getTrimmed(final ConfigKey p0);
    
    Boolean getBoolean(final String p0);
    
    Boolean getBoolean(final ConfigKey p0, final boolean p1);
    
    Boolean getBoolean(final String p0, final Boolean p1);
    
    void setBoolean(final String p0, final Boolean p1);
    
    void setBoolean(final ConfigKey p0, final Boolean p1);
    
    Integer getInt(final String p0);
    
    Integer getInt(final ConfigKey p0, final boolean p1);
    
    Integer getInt(final String p0, final Integer p1);
    
    void setInt(final String p0, final Integer p1);
    
    void setInt(final ConfigKey p0, final Integer p1);
    
    Long getLong(final String p0);
    
    Long getLong(final ConfigKey p0, final boolean p1);
    
    Long getLong(final String p0, final Long p1);
    
    void setLong(final String p0, final Long p1);
    
    void setLong(final ConfigKey p0, final Long p1);
    
    Float getFloat(final String p0);
    
    Float getFloat(final ConfigKey p0, final boolean p1);
    
    Float getFloat(final String p0, final Float p1);
    
    void setFloat(final String p0, final Float p1);
    
    void setFloat(final ConfigKey p0, final Float p1);
    
    List<String> getList(final String p0);
    
    List<String> getList(final String p0, final String[] p1);
    
    List<String> getList(final ConfigKey p0);
    
    Config getConfig(final String p0);
    
    Config getConfig(final ConfigKey p0);
    
    Class<?> getClass(final String p0) throws ClassNotFoundException;
    
    Class<?> getClass(final String p0, final Class<?> p1) throws ClassNotFoundException;
    
    Class<?> getClass(final ConfigKey p0, final boolean p1) throws ClassNotFoundException;
    
     <T> T getInstance(final String p0) throws ClassNotFoundException;
    
     <T> T getInstance(final ConfigKey p0) throws ClassNotFoundException;
    
     <T> T getInstance(final String p0, final Class<T> p1) throws ClassNotFoundException;
}
