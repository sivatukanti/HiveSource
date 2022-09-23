// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import javax.time.calendar.LocalTime;

public class LocalTimeStringConverter implements TypeConverter<LocalTime, String>
{
    @Override
    public LocalTime toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        return LocalTime.parse(str).toLocalTime();
    }
    
    @Override
    public String toDatastoreType(final LocalTime date) {
        return (date != null) ? date.toString() : null;
    }
}
