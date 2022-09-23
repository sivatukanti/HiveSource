// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.types.converters.UUIDStringConverter;
import java.util.UUID;
import org.datanucleus.store.types.converters.TypeConverter;

public class UUIDMapping extends ObjectAsStringMapping
{
    private static TypeConverter<UUID, String> converter;
    
    @Override
    public Class getJavaType() {
        return UUID.class;
    }
    
    @Override
    protected String objectToString(final Object object) {
        return UUIDMapping.converter.toDatastoreType((UUID)object);
    }
    
    @Override
    protected Object stringToObject(final String datastoreValue) {
        return UUIDMapping.converter.toMemberType(datastoreValue);
    }
    
    static {
        UUIDMapping.converter = new UUIDStringConverter();
    }
}
