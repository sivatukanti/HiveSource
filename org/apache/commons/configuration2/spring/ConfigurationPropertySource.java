// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.spring;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.configuration2.Configuration;
import org.springframework.core.env.EnumerablePropertySource;

public class ConfigurationPropertySource extends EnumerablePropertySource<Configuration>
{
    public ConfigurationPropertySource(final String name, final Configuration source) {
        super(name, (Object)source);
    }
    
    protected ConfigurationPropertySource(final String name) {
        super(name);
    }
    
    public String[] getPropertyNames() {
        final List<String> keys = new ArrayList<String>();
        final Iterator<String> keysIterator = ((Configuration)this.source).getKeys();
        while (keysIterator.hasNext()) {
            keys.add(keysIterator.next());
        }
        return keys.toArray(new String[keys.size()]);
    }
    
    public Object getProperty(final String name) {
        return ((Configuration)this.source).getProperty(name);
    }
}
