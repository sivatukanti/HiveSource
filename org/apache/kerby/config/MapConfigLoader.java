// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.config;

import java.util.Iterator;
import java.util.Map;

public class MapConfigLoader extends ConfigLoader
{
    @Override
    protected void loadConfig(final ConfigImpl config, final Resource resource) {
        final Map<String, Object> mapConfig = (Map<String, Object>)resource.getResource();
        final Iterator<Map.Entry<String, Object>> iter = mapConfig.entrySet().iterator();
        if (iter.hasNext()) {
            final Map.Entry entry = iter.next();
            if (entry.getValue() instanceof String) {
                this.loadStringMap(config, mapConfig);
            }
            else {
                this.loadObjectMap(config, mapConfig);
            }
        }
    }
    
    private void loadStringMap(final ConfigImpl config, final Map<String, Object> stringMap) {
        for (final Map.Entry<String, Object> entry : stringMap.entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
    }
    
    private void loadObjectMap(final ConfigImpl config, final Map<String, Object> objectMap) {
        for (final Map.Entry<String, Object> entry : objectMap.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (!(value instanceof Map)) {
                throw new RuntimeException("Unable to resolve config:" + key);
            }
            final ConfigImpl subConfig = new ConfigImpl(key);
            this.loadSubmap(subConfig, (Map<String, Object>)value);
            config.add(subConfig);
        }
    }
    
    private void loadSubmap(final ConfigImpl config, final Map<String, Object> map) {
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (value instanceof String) {
                config.set(key, (String)value);
            }
            if (value instanceof Map) {
                final ConfigImpl subConfig = new ConfigImpl(key);
                this.loadSubmap(subConfig, (Map<String, Object>)value);
                config.add(subConfig);
            }
        }
    }
}
