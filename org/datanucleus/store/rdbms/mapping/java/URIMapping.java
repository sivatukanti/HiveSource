// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.types.converters.URIStringConverter;
import java.net.URI;
import org.datanucleus.store.types.converters.TypeConverter;

public class URIMapping extends ObjectAsStringMapping
{
    private static TypeConverter<URI, String> converter;
    
    @Override
    public Class getJavaType() {
        return URI.class;
    }
    
    @Override
    protected String objectToString(final Object object) {
        return URIMapping.converter.toDatastoreType((URI)object);
    }
    
    @Override
    protected Object stringToObject(final String datastoreValue) {
        return URIMapping.converter.toMemberType(datastoreValue);
    }
    
    static {
        URIMapping.converter = new URIStringConverter();
    }
}
