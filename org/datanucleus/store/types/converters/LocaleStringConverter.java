// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import org.datanucleus.util.I18nUtils;
import java.util.Locale;

public class LocaleStringConverter implements TypeConverter<Locale, String>
{
    @Override
    public Locale toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        return I18nUtils.getLocaleFromString(str);
    }
    
    @Override
    public String toDatastoreType(final Locale loc) {
        return (loc != null) ? loc.toString() : null;
    }
}
