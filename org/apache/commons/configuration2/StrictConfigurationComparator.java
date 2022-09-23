// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.Iterator;

public class StrictConfigurationComparator implements ConfigurationComparator
{
    @Override
    public boolean compare(final Configuration a, final Configuration b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        Iterator<String> keys = a.getKeys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final Object value = a.getProperty(key);
            if (!value.equals(b.getProperty(key))) {
                return false;
            }
        }
        keys = b.getKeys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final Object value = b.getProperty(key);
            if (!value.equals(a.getProperty(key))) {
                return false;
            }
        }
        return true;
    }
}
