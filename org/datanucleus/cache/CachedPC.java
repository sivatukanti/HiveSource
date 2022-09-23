// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.cache;

import org.datanucleus.util.StringUtils;
import java.util.Iterator;
import org.datanucleus.util.ClassUtils;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class CachedPC implements Serializable
{
    private Class cls;
    private Map<Integer, Object> fieldValues;
    private Object version;
    private boolean[] loadedFields;
    
    public CachedPC(final Class cls, final boolean[] loadedFields, final Object vers) {
        this.fieldValues = null;
        this.cls = cls;
        this.loadedFields = new boolean[loadedFields.length];
        for (int i = 0; i < loadedFields.length; ++i) {
            this.loadedFields[i] = loadedFields[i];
        }
        this.version = vers;
    }
    
    public Class getObjectClass() {
        return this.cls;
    }
    
    public void setFieldValue(final Integer fieldNumber, final Object value) {
        if (this.fieldValues == null) {
            this.fieldValues = new HashMap<Integer, Object>();
        }
        this.fieldValues.put(fieldNumber, value);
    }
    
    public Object getFieldValue(final Integer fieldNumber) {
        if (this.fieldValues == null) {
            return null;
        }
        return this.fieldValues.get(fieldNumber);
    }
    
    public void setVersion(final Object ver) {
        this.version = ver;
    }
    
    public Object getVersion() {
        return this.version;
    }
    
    public boolean[] getLoadedFields() {
        return this.loadedFields;
    }
    
    public int[] getLoadedFieldNumbers() {
        return ClassUtils.getFlagsSetTo(this.loadedFields, true);
    }
    
    public void setLoadedField(final int fieldNumber, final boolean loaded) {
        this.loadedFields[fieldNumber] = loaded;
    }
    
    public synchronized CachedPC getCopy() {
        final CachedPC copy = new CachedPC(this.cls, this.loadedFields, this.version);
        if (this.fieldValues != null) {
            copy.fieldValues = new HashMap<Integer, Object>(this.fieldValues.size());
            for (final Map.Entry<Integer, Object> entry : this.fieldValues.entrySet()) {
                final Integer key = entry.getKey();
                Object val = entry.getValue();
                if (val != null && val instanceof CachedPC) {
                    val = ((CachedPC)val).getCopy();
                }
                copy.fieldValues.put(key, val);
            }
        }
        return copy;
    }
    
    @Override
    public String toString() {
        return this.toString(false);
    }
    
    public String toString(final boolean debug) {
        return "CachedPC : cls=" + this.cls.getName() + " version=" + this.version + " loadedFlags=" + StringUtils.booleanArrayToString(this.loadedFields) + (debug ? (" vals=" + StringUtils.mapToString(this.fieldValues)) : "");
    }
    
    public static class CachedId implements Serializable
    {
        String className;
        Object id;
        
        public CachedId(final String className, final Object id) {
            this.className = className;
            this.id = id;
        }
        
        public String getClassName() {
            return this.className;
        }
        
        public Object getId() {
            return this.id;
        }
    }
}
