// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import javax.time.calendar.LocalDateTime;

public class LocalDateTimeStringConverter implements TypeConverter<LocalDateTime, String>
{
    @Override
    public LocalDateTime toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        return LocalDateTime.parse(str);
    }
    
    @Override
    public String toDatastoreType(final LocalDateTime date) {
        return (date != null) ? date.toString() : null;
    }
}
