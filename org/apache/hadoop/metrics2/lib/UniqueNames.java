// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import com.google.common.collect.Maps;
import java.util.Map;
import com.google.common.base.Joiner;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class UniqueNames
{
    static final Joiner joiner;
    final Map<String, Count> map;
    
    public UniqueNames() {
        this.map = (Map<String, Count>)Maps.newHashMap();
    }
    
    public synchronized String uniqueName(final String name) {
        Count c = this.map.get(name);
        if (c == null) {
            c = new Count(name, 0);
            this.map.put(name, c);
            return name;
        }
        if (!c.baseName.equals(name)) {
            c = new Count(name, 0);
        }
        String newName;
        Count c2;
        do {
            newName = UniqueNames.joiner.join(name, ++c.value, new Object[0]);
            c2 = this.map.get(newName);
        } while (c2 != null);
        this.map.put(newName, c);
        return newName;
    }
    
    static {
        joiner = Joiner.on('-');
    }
    
    static class Count
    {
        final String baseName;
        int value;
        
        Count(final String name, final int value) {
            this.baseName = name;
            this.value = value;
        }
    }
}
