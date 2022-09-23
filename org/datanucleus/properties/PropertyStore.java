// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.properties;

import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public abstract class PropertyStore
{
    protected Map<String, Object> properties;
    
    public PropertyStore() {
        this.properties = new HashMap<String, Object>();
    }
    
    protected void setPropertyInternal(final String name, final Object value) {
        this.properties.put(name.toLowerCase(Locale.ENGLISH), value);
    }
    
    public Object getProperty(final String name) {
        if (this.properties.containsKey(name.toLowerCase(Locale.ENGLISH))) {
            return this.properties.get(name.toLowerCase(Locale.ENGLISH));
        }
        return null;
    }
    
    public boolean hasProperty(final String name) {
        return this.properties.containsKey(name.toLowerCase(Locale.ENGLISH));
    }
    
    public boolean hasPropertyNotNull(final String name) {
        return this.getProperty(name) != null;
    }
    
    public int getIntProperty(final String name) {
        final Object obj = this.getProperty(name);
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Number) {
            return ((Number)obj).intValue();
        }
        if (obj instanceof String) {
            final Integer intVal = Integer.valueOf((String)obj);
            this.setPropertyInternal(name, intVal);
            return intVal;
        }
        throw new PropertyTypeInvalidException(name, "int");
    }
    
    public boolean getBooleanProperty(final String name) {
        return this.getBooleanProperty(name, false);
    }
    
    public boolean getBooleanProperty(final String name, final boolean resultIfNotSet) {
        final Object obj = this.getProperty(name);
        if (obj == null) {
            return resultIfNotSet;
        }
        if (obj instanceof Boolean) {
            return (boolean)obj;
        }
        if (obj instanceof String) {
            final Boolean boolVal = Boolean.valueOf((String)obj);
            this.setPropertyInternal(name, boolVal);
            return boolVal;
        }
        throw new PropertyTypeInvalidException(name, "boolean");
    }
    
    public Boolean getBooleanObjectProperty(final String name) {
        final Object obj = this.getProperty(name);
        if (obj == null) {
            return null;
        }
        if (obj instanceof Boolean) {
            return (Boolean)obj;
        }
        if (obj instanceof String) {
            final Boolean boolVal = Boolean.valueOf((String)obj);
            this.setPropertyInternal(name, boolVal);
            return boolVal;
        }
        throw new PropertyTypeInvalidException(name, "Boolean");
    }
    
    public String getStringProperty(final String name) {
        final Object obj = this.getProperty(name);
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String)obj;
        }
        throw new PropertyTypeInvalidException(name, "String");
    }
}
