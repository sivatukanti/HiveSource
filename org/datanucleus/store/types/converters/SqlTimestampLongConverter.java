// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.sql.Timestamp;

public class SqlTimestampLongConverter implements TypeConverter<Timestamp, Long>
{
    @Override
    public Timestamp toMemberType(final Long value) {
        if (value == null) {
            return null;
        }
        return new Timestamp(value);
    }
    
    @Override
    public Long toDatastoreType(final Timestamp ts) {
        return (ts != null) ? Long.valueOf(ts.getTime()) : null;
    }
}
