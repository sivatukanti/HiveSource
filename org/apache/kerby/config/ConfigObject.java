// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.config;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class ConfigObject
{
    private ValueType valueType;
    private Object value;
    
    public ConfigObject(final String value) {
        this.value = value;
        this.valueType = ValueType.PROPERTY;
    }
    
    public ConfigObject(final String[] values) {
        final List<String> valuesList = new ArrayList<String>();
        for (final String v : values) {
            valuesList.add(v);
        }
        this.value = valuesList;
        this.valueType = ValueType.LIST;
    }
    
    public ConfigObject(final List<String> values) {
        if (values != null) {
            this.value = new ArrayList(values);
        }
        else {
            this.value = new ArrayList();
        }
        this.valueType = ValueType.LIST;
    }
    
    public ConfigObject(final Config value) {
        this.value = value;
        this.valueType = ValueType.CONFIG;
    }
    
    public String getPropertyValue() {
        String result = null;
        if (this.valueType == ValueType.PROPERTY) {
            result = (String)this.value;
        }
        return result;
    }
    
    public List<String> getListValues() {
        List<String> results = null;
        if (this.valueType == ValueType.LIST && this.value instanceof List) {
            results = (List<String>)this.value;
        }
        return results;
    }
    
    public Config getConfigValue() {
        Config result = null;
        if (this.valueType == ValueType.CONFIG) {
            result = (Config)this.value;
        }
        return result;
    }
    
    protected enum ValueType
    {
        PROPERTY, 
        LIST, 
        CONFIG;
    }
}
