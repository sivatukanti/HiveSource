// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.sql.Time;

public class SqlTimeStringConverter implements TypeConverter<Time, String>
{
    @Override
    public Time toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        return Time.valueOf(str);
    }
    
    @Override
    public String toDatastoreType(final Time time) {
        return (time != null) ? time.toString() : null;
    }
}
