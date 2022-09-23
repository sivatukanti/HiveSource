// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import javax.time.calendar.LocalTime;

public class LocalTimeLongConverter implements TypeConverter<LocalTime, Long>
{
    @Override
    public LocalTime toMemberType(final Long val) {
        if (val == null) {
            return null;
        }
        return LocalTime.ofNanoOfDay((long)val).toLocalTime();
    }
    
    @Override
    public Long toDatastoreType(final LocalTime date) {
        return (date != null) ? Long.valueOf(date.toNanoOfDay()) : null;
    }
}
