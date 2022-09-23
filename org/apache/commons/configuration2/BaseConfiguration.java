// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.Collection;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseConfiguration extends AbstractConfiguration implements Cloneable
{
    private Map<String, Object> store;
    
    public BaseConfiguration() {
        this.store = new LinkedHashMap<String, Object>();
    }
    
    @Override
    protected void addPropertyDirect(final String key, final Object value) {
        final Object previousValue = this.getPropertyInternal(key);
        if (previousValue == null) {
            this.store.put(key, value);
        }
        else if (previousValue instanceof List) {
            final List<Object> valueList = (List<Object>)previousValue;
            valueList.add(value);
        }
        else {
            final List<Object> list = new ArrayList<Object>();
            list.add(previousValue);
            list.add(value);
            this.store.put(key, list);
        }
    }
    
    @Override
    protected Object getPropertyInternal(final String key) {
        return this.store.get(key);
    }
    
    @Override
    protected boolean isEmptyInternal() {
        return this.store.isEmpty();
    }
    
    @Override
    protected boolean containsKeyInternal(final String key) {
        return this.store.containsKey(key);
    }
    
    @Override
    protected void clearPropertyDirect(final String key) {
        this.store.remove(key);
    }
    
    @Override
    protected void clearInternal() {
        this.store.clear();
    }
    
    @Override
    protected int sizeInternal() {
        return this.store.size();
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        return this.store.keySet().iterator();
    }
    
    public Object clone() {
        try {
            final BaseConfiguration copy = (BaseConfiguration)super.clone();
            this.cloneStore(copy);
            copy.cloneInterpolator(this);
            return copy;
        }
        catch (CloneNotSupportedException cex) {
            throw new ConfigurationRuntimeException(cex);
        }
    }
    
    private void cloneStore(final BaseConfiguration copy) throws CloneNotSupportedException {
        final Map<String, Object> clonedStore = (Map<String, Object>)ConfigurationUtils.clone(this.store);
        copy.store = clonedStore;
        for (final Map.Entry<String, Object> e : this.store.entrySet()) {
            if (e.getValue() instanceof Collection) {
                final Collection<String> strList = e.getValue();
                copy.store.put(e.getKey(), new ArrayList(strList));
            }
        }
    }
}
