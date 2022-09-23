// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import java.util.Iterator;
import org.datanucleus.store.schema.StoreSchemaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.datanucleus.store.schema.ListStoreSchemaData;

public class RDBMSTableIndexInfo implements ListStoreSchemaData
{
    private int hash;
    Map properties;
    List indices;
    
    public RDBMSTableIndexInfo() {
        this.hash = 0;
        this.properties = new HashMap();
        this.indices = new ArrayList();
    }
    
    public RDBMSTableIndexInfo(final String catalog, final String schema, final String table) {
        this.hash = 0;
        this.properties = new HashMap();
        this.indices = new ArrayList();
        this.addProperty("table_cat", catalog);
        this.addProperty("table_schem", schema);
        this.addProperty("table_name", table);
    }
    
    @Override
    public void addChild(final StoreSchemaData child) {
        this.indices.add(child);
    }
    
    @Override
    public void clearChildren() {
        this.indices.clear();
    }
    
    @Override
    public StoreSchemaData getChild(final int position) {
        return this.indices.get(position);
    }
    
    @Override
    public List getChildren() {
        return this.indices;
    }
    
    @Override
    public int getNumberOfChildren() {
        return this.indices.size();
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
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RDBMSTableIndexInfo)) {
            return false;
        }
        final RDBMSTableIndexInfo other = (RDBMSTableIndexInfo)obj;
        final String cat1 = (String)this.getProperty("table_cat");
        final String sch1 = (String)this.getProperty("table_schem");
        final String name1 = (String)this.getProperty("table_name");
        final String cat2 = (String)other.getProperty("table_cat");
        final String sch2 = (String)other.getProperty("table_schem");
        final String name2 = (String)other.getProperty("table_name");
        if (cat1 == null) {
            if (cat2 != null) {
                return false;
            }
        }
        else if (!cat1.equals(cat2)) {
            return false;
        }
        if (sch1 == null) {
            if (sch2 != null) {
                return false;
            }
        }
        else if (!sch1.equals(sch2)) {
            return false;
        }
        if (name1.equals(name2)) {
            return true;
        }
        return false;
    }
    
    @Override
    public final int hashCode() {
        if (this.hash == 0) {
            final String cat = (String)this.getProperty("table_cat");
            final String sch = (String)this.getProperty("table_schem");
            final String name = (String)this.getProperty("table_name");
            this.hash = (((cat == null) ? 0 : cat.hashCode()) ^ ((sch == null) ? 0 : sch.hashCode()) ^ name.hashCode());
        }
        return this.hash;
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer("RDBMSTableIndexInfo : ");
        final Iterator iter = this.properties.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry entry = iter.next();
            str.append(entry.getKey() + " = " + entry.getValue());
            if (iter.hasNext()) {
                str.append(", ");
            }
        }
        str.append(", numIndices=" + this.indices.size());
        return str.toString();
    }
}
