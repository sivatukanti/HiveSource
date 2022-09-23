// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

public class IntegerStringConverter implements TypeConverter<Integer, String>
{
    @Override
    public Integer toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        try {
            return Integer.getInteger(str);
        }
        catch (NumberFormatException nfe) {
            return null;
        }
    }
    
    @Override
    public String toDatastoreType(final Integer val) {
        if (val == null) {
            return null;
        }
        return "" + val;
    }
}
