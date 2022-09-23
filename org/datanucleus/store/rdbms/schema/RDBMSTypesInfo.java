// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import java.util.Iterator;
import java.util.HashMap;
import org.datanucleus.store.schema.StoreSchemaData;
import java.util.Map;
import org.datanucleus.store.schema.MapStoreSchemaData;

public class RDBMSTypesInfo implements MapStoreSchemaData
{
    Map<String, Object> properties;
    Map<String, StoreSchemaData> jdbcTypes;
    
    public RDBMSTypesInfo() {
        this.properties = new HashMap<String, Object>();
        this.jdbcTypes = new HashMap<String, StoreSchemaData>();
    }
    
    @Override
    public void addChild(final StoreSchemaData type) {
        this.jdbcTypes.put("" + type.getProperty("jdbc_type"), type);
    }
    
    @Override
    public void clearChildren() {
        this.jdbcTypes.clear();
    }
    
    @Override
    public StoreSchemaData getChild(final String key) {
        return this.jdbcTypes.get(key);
    }
    
    @Override
    public Map<String, StoreSchemaData> getChildren() {
        return this.jdbcTypes;
    }
    
    @Override
    public int getNumberOfChildren() {
        return this.jdbcTypes.size();
    }
    
    @Override
    public void addProperty(final String name, final Object value) {
        this.properties.put(name, value);
    }
    
    @Override
    public Object getProperty(final String name) {
        return this.properties.get(name);
    }
    
    @Override
    public StoreSchemaData getParent() {
        return null;
    }
    
    @Override
    public void setParent(final StoreSchemaData parent) {
    }
    
    @Override
    public final boolean equals(final Object obj) {
        return obj == this || (obj instanceof RDBMSTypesInfo && obj == this);
    }
    
    @Override
    public final int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer("RDBMSTypesInfo : ");
        final Iterator iter = this.properties.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry entry = iter.next();
            str.append(entry.getKey() + " = " + entry.getValue());
            if (iter.hasNext()) {
                str.append(", ");
            }
        }
        str.append(", numJDBCTypes=" + this.jdbcTypes.size());
        return str.toString();
    }
}
