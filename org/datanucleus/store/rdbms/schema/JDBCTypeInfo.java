// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import java.util.Iterator;
import org.datanucleus.store.schema.StoreSchemaData;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.store.schema.MapStoreSchemaData;

public class JDBCTypeInfo implements MapStoreSchemaData
{
    private int hash;
    Map properties;
    Map sqlTypes;
    
    public JDBCTypeInfo(final short type) {
        this.hash = 0;
        this.properties = new HashMap();
        this.sqlTypes = new HashMap();
        this.addProperty("jdbc_type", type);
    }
    
    @Override
    public void setParent(final StoreSchemaData parent) {
    }
    
    @Override
    public StoreSchemaData getParent() {
        return null;
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
    public void addChild(final StoreSchemaData child) {
        final SQLTypeInfo sqlType = (SQLTypeInfo)child;
        this.sqlTypes.put(sqlType.getTypeName(), sqlType);
        if (this.sqlTypes.size() == 1) {
            this.sqlTypes.put("DEFAULT", sqlType);
        }
    }
    
    @Override
    public void clearChildren() {
        this.sqlTypes.clear();
    }
    
    @Override
    public StoreSchemaData getChild(final String key) {
        return this.sqlTypes.get(key);
    }
    
    @Override
    public Map getChildren() {
        return this.sqlTypes;
    }
    
    @Override
    public int getNumberOfChildren() {
        return this.sqlTypes.size();
    }
    
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof JDBCTypeInfo)) {
            return false;
        }
        final JDBCTypeInfo other = (JDBCTypeInfo)obj;
        final short jdbcType1 = (short)this.getProperty("jdbc_type");
        final short jdbcType2 = (short)other.getProperty("jdbc_type");
        return jdbcType1 == jdbcType2;
    }
    
    @Override
    public final int hashCode() {
        if (this.hash == 0) {
            final short jdbcType1 = (short)this.getProperty("jdbc_type");
            this.hash = jdbcType1;
        }
        return this.hash;
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer("JDBCTypeInfo : ");
        final Iterator iter = this.properties.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry entry = iter.next();
            str.append(entry.getKey() + " = " + entry.getValue());
            if (iter.hasNext()) {
                str.append(", ");
            }
        }
        str.append(", numSQLTypes=" + this.sqlTypes.size());
        return str.toString();
    }
}
