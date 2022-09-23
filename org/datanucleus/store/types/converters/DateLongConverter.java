// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.util.Date;

public class DateLongConverter implements TypeConverter<Date, Long>
{
    @Override
    public Date toMemberType(final Long value) {
        if (value == null) {
            return null;
        }
        return new Date(value);
    }
    
    @Override
    public Long toDatastoreType(final Date date) {
        return (date != null) ? Long.valueOf(date.getTime()) : null;
    }
}
