// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.net.URI;

public class URIStringConverter implements TypeConverter<URI, String>
{
    @Override
    public URI toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        return URI.create(str.trim());
    }
    
    @Override
    public String toDatastoreType(final URI uri) {
        return (uri != null) ? uri.toString() : null;
    }
}
