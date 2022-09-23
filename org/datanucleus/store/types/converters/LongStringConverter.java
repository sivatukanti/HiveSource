// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

public class LongStringConverter implements TypeConverter<Long, String>
{
    @Override
    public Long toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        try {
            return Long.getLong(str);
        }
        catch (NumberFormatException nfe) {
            return null;
        }
    }
    
    @Override
    public String toDatastoreType(final Long val) {
        if (val == null) {
            return null;
        }
        return "" + val;
    }
}
