// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.Properties;

public final class DoubleProperties extends Properties
{
    private final Properties read;
    private final Properties write;
    
    public DoubleProperties(final Properties read, final Properties write) {
        this.read = read;
        this.write = write;
    }
    
    public Object put(final Object key, final Object value) {
        return this.write.put(key, value);
    }
    
    public String getProperty(final String s) {
        return this.read.getProperty(s, this.write.getProperty(s));
    }
    
    public String getProperty(final String s, final String defaultValue) {
        return this.read.getProperty(s, this.write.getProperty(s, defaultValue));
    }
    
    public Enumeration propertyNames() {
        final HashSet<Object> c = new HashSet<Object>();
        addAllNames(this.write, c);
        addAllNames(this.read, c);
        return Collections.enumeration(c);
    }
    
    private static void addAllNames(final Properties properties, final HashSet set) {
        if (properties != null) {
            final Enumeration<?> propertyNames = properties.propertyNames();
            while (propertyNames.hasMoreElements()) {
                set.add(propertyNames.nextElement());
            }
        }
    }
}
