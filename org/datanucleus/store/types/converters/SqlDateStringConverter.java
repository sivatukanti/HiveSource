// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.sql.Date;

public class SqlDateStringConverter implements TypeConverter<Date, String>
{
    @Override
    public Date toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        return Date.valueOf(str);
    }
    
    @Override
    public String toDatastoreType(final Date date) {
        return (date != null) ? date.toString() : null;
    }
}
