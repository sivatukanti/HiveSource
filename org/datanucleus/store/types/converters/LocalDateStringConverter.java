// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import javax.time.calendar.LocalDate;

public class LocalDateStringConverter implements TypeConverter<LocalDate, String>
{
    @Override
    public LocalDate toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        return LocalDate.parse(str);
    }
    
    @Override
    public String toDatastoreType(final LocalDate date) {
        return (date != null) ? date.toString() : null;
    }
}
