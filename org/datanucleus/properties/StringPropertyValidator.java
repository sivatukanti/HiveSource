// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.properties;

public class StringPropertyValidator implements PersistencePropertyValidator
{
    @Override
    public boolean validate(final String name, final Object value) {
        return value == null || value instanceof String;
    }
}
