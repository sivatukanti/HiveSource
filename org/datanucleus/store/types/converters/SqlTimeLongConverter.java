// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.sql.Time;

public class SqlTimeLongConverter implements TypeConverter<Time, Long>
{
    @Override
    public Time toMemberType(final Long value) {
        if (value == null) {
            return null;
        }
        return new Time(value);
    }
    
    @Override
    public Long toDatastoreType(final Time time) {
        return (time != null) ? Long.valueOf(time.getTime()) : null;
    }
}
