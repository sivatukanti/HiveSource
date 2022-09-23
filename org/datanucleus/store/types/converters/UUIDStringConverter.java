// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import java.util.UUID;

public class UUIDStringConverter implements TypeConverter<UUID, String>
{
    @Override
    public UUID toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        return UUID.fromString(str);
    }
    
    @Override
    public String toDatastoreType(final UUID uuid) {
        return (uuid != null) ? uuid.toString() : null;
    }
}
