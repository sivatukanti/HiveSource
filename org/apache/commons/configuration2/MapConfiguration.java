// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Properties;
import java.util.Map;

public class MapConfiguration extends AbstractConfiguration implements Cloneable
{
    protected Map<String, Object> map;
    private boolean trimmingDisabled;
    
    public MapConfiguration(final Map<String, ?> map) {
        this.map = (Map<String, Object>)map;
    }
    
    public MapConfiguration(final Properties props) {
        this.map = convertPropertiesToMap(props);
    }
    
    public Map<String, Object> getMap() {
        return this.map;
    }
    
    public boolean isTrimmingDisabled() {
        return this.trimmingDisabled;
    }
    
    public void setTrimmingDisabled(final boolean trimmingDisabled) {
        this.trimmingDisabled = trimmingDisabled;
    }
    
    @Override
    protected Object getPropertyInternal(final String key) {
        final Object value = this.map.get(key);
        if (value instanceof String) {
            final Collection<String> list = this.getListDelimiterHandler().split((String)value, !this.isTrimmingDisabled());
            return (list.size() > 1) ? list : list.iterator().next();
        }
        return value;
    }
    
    @Override
    protected void addPropertyDirect(final String key, final Object value) {
        final Object previousValue = this.getProperty(key);
        if (previousValue == null) {
            this.map.put(key, value);
        }
        else if (previousValue instanceof List) {
            ((List)previousValue).add(value);
        }
        else {
            final List<Object> list = new ArrayList<Object>();
            list.add(previousValue);
            list.add(value);
            this.map.put(key, list);
        }
    }
    
    @Override
    protected boolean isEmptyInternal() {
        return this.map.isEmpty();
    }
    
    @Override
    protected boolean containsKeyInternal(final String key) {
        return this.map.containsKey(key);
    }
    
    @Override
    protected void clearPropertyDirect(final String key) {
        this.map.remove(key);
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        return this.map.keySet().iterator();
    }
    
    @Override
    protected int sizeInternal() {
        return this.map.size();
    }
    
    public Object clone() {
        try {
            final MapConfiguration copy = (MapConfiguration)super.clone();
            final Map<String, Object> clonedMap = (Map<String, Object>)ConfigurationUtils.clone(this.map);
            copy.map = clonedMap;
            copy.cloneInterpolator(this);
            return copy;
        }
        catch (CloneNotSupportedException cex) {
            throw new ConfigurationRuntimeException(cex);
        }
    }
    
    private static Map<String, Object> convertPropertiesToMap(final Properties props) {
        final Map map = props;
        return (Map<String, Object>)map;
    }
}
