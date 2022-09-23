// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.properties;

public class BooleanPropertyValidator implements PersistencePropertyValidator
{
    @Override
    public boolean validate(final String name, final Object value) {
        return validateValueIsBoolean(value);
    }
    
    public static boolean validateValueIsBoolean(final Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return true;
        }
        if (value instanceof String) {
            final String val = ((String)value).trim();
            if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false")) {
                return true;
            }
        }
        return false;
    }
}
