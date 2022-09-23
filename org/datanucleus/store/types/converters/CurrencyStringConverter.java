// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.util.Currency;

public class CurrencyStringConverter implements TypeConverter<Currency, String>
{
    @Override
    public Currency toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        return Currency.getInstance(str);
    }
    
    @Override
    public String toDatastoreType(final Currency curr) {
        return (curr != null) ? curr.toString() : null;
    }
}
