// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.conf;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

public class ReconfigurationUtil
{
    public static Collection<PropertyChange> getChangedProperties(final Configuration newConf, final Configuration oldConf) {
        final Map<String, PropertyChange> changes = new HashMap<String, PropertyChange>();
        for (final Map.Entry<String, String> oldEntry : oldConf) {
            final String prop = oldEntry.getKey();
            final String oldVal = oldEntry.getValue();
            final String newVal = newConf.getRaw(prop);
            if (newVal == null || !newVal.equals(oldVal)) {
                changes.put(prop, new PropertyChange(prop, newVal, oldVal));
            }
        }
        for (final Map.Entry<String, String> newEntry : newConf) {
            final String prop = newEntry.getKey();
            final String newVal2 = newEntry.getValue();
            if (oldConf.get(prop) == null) {
                changes.put(prop, new PropertyChange(prop, newVal2, null));
            }
        }
        return changes.values();
    }
    
    public Collection<PropertyChange> parseChangedProperties(final Configuration newConf, final Configuration oldConf) {
        return getChangedProperties(newConf, oldConf);
    }
    
    public static class PropertyChange
    {
        public String prop;
        public String oldVal;
        public String newVal;
        
        public PropertyChange(final String prop, final String newVal, final String oldVal) {
            this.prop = prop;
            this.newVal = newVal;
            this.oldVal = oldVal;
        }
    }
}
