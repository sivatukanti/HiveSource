// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

public class StringBufferStringConverter implements TypeConverter<StringBuffer, String>
{
    @Override
    public StringBuffer toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        return new StringBuffer(str);
    }
    
    @Override
    public String toDatastoreType(final StringBuffer str) {
        return (str != null) ? str.toString() : null;
    }
}
