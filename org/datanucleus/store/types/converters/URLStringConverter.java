// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.net.MalformedURLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.net.URL;

public class URLStringConverter implements TypeConverter<URL, String>
{
    @Override
    public URL toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        URL url = null;
        try {
            url = new URL(str.trim());
        }
        catch (MalformedURLException mue) {
            throw new NucleusDataStoreException(URLStringConverter.LOCALISER.msg("016002", str, URL.class.getName()), mue);
        }
        return url;
    }
    
    @Override
    public String toDatastoreType(final URL url) {
        return (url != null) ? url.toString() : null;
    }
}
