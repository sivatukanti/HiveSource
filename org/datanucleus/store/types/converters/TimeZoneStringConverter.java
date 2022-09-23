// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.util.TimeZone;

public class TimeZoneStringConverter implements TypeConverter<TimeZone, String>
{
    @Override
    public TimeZone toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        return TimeZone.getTimeZone(str.trim());
    }
    
    @Override
    public String toDatastoreType(final TimeZone tz) {
        return (tz != null) ? tz.getID() : null;
    }
}
