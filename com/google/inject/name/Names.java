// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.name;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Iterator;
import java.lang.annotation.Annotation;
import com.google.inject.Key;
import java.util.Map;
import com.google.inject.Binder;

public class Names
{
    private Names() {
    }
    
    public static Named named(final String name) {
        return new NamedImpl(name);
    }
    
    public static void bindProperties(Binder binder, final Map<String, String> properties) {
        binder = binder.skipSources(Names.class);
        for (final Map.Entry<String, String> entry : properties.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            binder.bind((Key<String>)Key.get((Class<T>)String.class, new NamedImpl(key))).toInstance(value);
        }
    }
    
    public static void bindProperties(Binder binder, final Properties properties) {
        binder = binder.skipSources(Names.class);
        final Enumeration<?> e = properties.propertyNames();
        while (e.hasMoreElements()) {
            final String propertyName = (String)e.nextElement();
            final String value = properties.getProperty(propertyName);
            binder.bind((Key<String>)Key.get((Class<T>)String.class, new NamedImpl(propertyName))).toInstance(value);
        }
    }
}
