// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.types.converters.CurrencyStringConverter;
import java.util.Currency;
import org.datanucleus.store.types.converters.TypeConverter;

public class CurrencyMapping extends ObjectAsStringMapping
{
    private static TypeConverter<Currency, String> converter;
    
    @Override
    public Class getJavaType() {
        return Currency.class;
    }
    
    @Override
    public int getDefaultLength(final int index) {
        return 3;
    }
    
    @Override
    protected String objectToString(final Object object) {
        return CurrencyMapping.converter.toDatastoreType((Currency)object);
    }
    
    @Override
    protected Object stringToObject(final String datastoreValue) {
        return CurrencyMapping.converter.toMemberType(datastoreValue);
    }
    
    static {
        CurrencyMapping.converter = new CurrencyStringConverter();
    }
}
