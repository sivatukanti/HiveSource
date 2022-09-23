// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.util.TimeZone;
import org.datanucleus.store.types.converters.TimeZoneStringConverter;

public class TimeZoneMapping extends ObjectAsStringMapping
{
    private static TimeZoneStringConverter converter;
    
    @Override
    public Class getJavaType() {
        return TimeZone.class;
    }
    
    @Override
    public int getDefaultLength(final int index) {
        return 30;
    }
    
    @Override
    protected String objectToString(final Object object) {
        return TimeZoneMapping.converter.toDatastoreType((TimeZone)object);
    }
    
    @Override
    protected Object stringToObject(final String datastoreValue) {
        return TimeZoneMapping.converter.toMemberType(datastoreValue);
    }
    
    static {
        TimeZoneMapping.converter = new TimeZoneStringConverter();
    }
}
