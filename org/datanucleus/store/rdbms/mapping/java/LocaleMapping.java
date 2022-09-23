// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.types.converters.LocaleStringConverter;
import java.util.Locale;
import org.datanucleus.store.types.converters.TypeConverter;

public class LocaleMapping extends ObjectAsStringMapping
{
    private static TypeConverter<Locale, String> converter;
    
    @Override
    public Class getJavaType() {
        return Locale.class;
    }
    
    @Override
    public int getDefaultLength(final int index) {
        return 20;
    }
    
    @Override
    protected String objectToString(final Object object) {
        return LocaleMapping.converter.toDatastoreType((Locale)object);
    }
    
    @Override
    protected Object stringToObject(final String datastoreValue) {
        return LocaleMapping.converter.toMemberType(datastoreValue);
    }
    
    static {
        LocaleMapping.converter = new LocaleStringConverter();
    }
}
