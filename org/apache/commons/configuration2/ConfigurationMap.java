// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.Iterator;
import java.util.AbstractSet;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap;

public class ConfigurationMap extends AbstractMap<Object, Object>
{
    private final Configuration configuration;
    
    public ConfigurationMap(final Configuration configuration) {
        this.configuration = configuration;
    }
    
    public Configuration getConfiguration() {
        return this.configuration;
    }
    
    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        return new ConfigurationSet(this.configuration);
    }
    
    @Override
    public Object put(final Object key, final Object value) {
        final String strKey = String.valueOf(key);
        final Object old = this.configuration.getProperty(strKey);
        this.configuration.setProperty(strKey, value);
        return old;
    }
    
    @Override
    public Object get(final Object key) {
        return this.configuration.getProperty(String.valueOf(key));
    }
    
    static class ConfigurationSet extends AbstractSet<Map.Entry<Object, Object>>
    {
        private final Configuration configuration;
        
        ConfigurationSet(final Configuration configuration) {
            this.configuration = configuration;
        }
        
        @Override
        public int size() {
            int count = 0;
            final Iterator<String> iterator = this.configuration.getKeys();
            while (iterator.hasNext()) {
                iterator.next();
                ++count;
            }
            return count;
        }
        
        @Override
        public Iterator<Map.Entry<Object, Object>> iterator() {
            return new ConfigurationSetIterator();
        }
        
        private final class Entry implements Map.Entry<Object, Object>
        {
            private final Object key;
            
            private Entry(final Object key) {
                this.key = key;
            }
            
            @Override
            public Object getKey() {
                return this.key;
            }
            
            @Override
            public Object getValue() {
                return ConfigurationSet.this.configuration.getProperty((String)this.key);
            }
            
            @Override
            public Object setValue(final Object value) {
                final Object old = this.getValue();
                ConfigurationSet.this.configuration.setProperty((String)this.key, value);
                return old;
            }
        }
        
        private final class ConfigurationSetIterator implements Iterator<Map.Entry<Object, Object>>
        {
            private final Iterator<String> keys;
            
            private ConfigurationSetIterator() {
                this.keys = ConfigurationSet.this.configuration.getKeys();
            }
            
            @Override
            public boolean hasNext() {
                return this.keys.hasNext();
            }
            
            @Override
            public Map.Entry<Object, Object> next() {
                return new Entry((Object)this.keys.next());
            }
            
            @Override
            public void remove() {
                this.keys.remove();
            }
        }
    }
}
