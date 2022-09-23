// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.properties;

public class IntegerPropertyValidator implements PersistencePropertyValidator
{
    @Override
    public boolean validate(final String name, final Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Integer) {
            return true;
        }
        if (value instanceof String) {
            final String val = ((String)value).trim();
            try {
                Integer.valueOf(val);
                return true;
            }
            catch (NumberFormatException nfe) {
                return false;
            }
        }
        return false;
    }
}
