// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.types.converters.URLStringConverter;
import java.net.URL;
import org.datanucleus.store.types.converters.TypeConverter;

public class URLMapping extends ObjectAsStringMapping
{
    private static TypeConverter<URL, String> converter;
    
    @Override
    public Class getJavaType() {
        return URL.class;
    }
    
    @Override
    protected String objectToString(final Object object) {
        return URLMapping.converter.toDatastoreType((URL)object);
    }
    
    @Override
    protected Object stringToObject(final String datastoreValue) {
        return URLMapping.converter.toMemberType(datastoreValue);
    }
    
    static {
        URLMapping.converter = new URLStringConverter();
    }
}
