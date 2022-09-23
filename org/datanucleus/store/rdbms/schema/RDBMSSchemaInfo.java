// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import java.util.Iterator;
import org.datanucleus.exceptions.NucleusException;
import java.util.HashMap;
import org.datanucleus.store.schema.StoreSchemaData;
import java.util.Map;
import org.datanucleus.store.schema.MapStoreSchemaData;

public class RDBMSSchemaInfo implements MapStoreSchemaData
{
    private int hash;
    Map properties;
    Map<String, StoreSchemaData> tables;
    
    public RDBMSSchemaInfo(final String catalog, final String schema) {
        this.hash = 0;
        this.properties = new HashMap();
        this.tables = new HashMap<String, StoreSchemaData>();
        this.addProperty("catalog", catalog);
        this.addProperty("schema", schema);
    }
    
    @Override
    public void addChild(final StoreSchemaData data) {
        final RDBMSTableInfo table = (RDBMSTableInfo)data;
        final String tableKey = (String)data.getProperty("table_key");
        if (tableKey == null) {
            throw new NucleusException("Attempt to add RDBMSTableInfo to RDBMSSchemaInfo with null table key! tableName=" + data.getProperty("table_name"));
        }
        this.tables.put(tableKey, table);
    }
    
    @Override
    public void clearChildren() {
        this.tables.clear();
    }
    
    @Override
    public StoreSchemaData getChild(final String key) {
        return this.tables.get(key);
    }
    
    @Override
    public Map<String, StoreSchemaData> getChildren() {
        return this.tables;
    }
    
    @Override
    public int getNumberOfChildren() {
        return this.tables.size();
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
        if (!(obj instanceof RDBMSSchemaInfo)) {
            return false;
        }
        final RDBMSSchemaInfo other = (RDBMSSchemaInfo)obj;
        final String cat1 = (String)this.getProperty("table_cat");
        final String sch1 = (String)this.getProperty("table_schem");
        final String cat2 = (String)other.getProperty("table_cat");
        final String sch2 = (String)other.getProperty("table_schem");
        if (cat1 == null) {
            if (cat2 != null) {
                return false;
            }
        }
        else if (!cat1.equals(cat2)) {
            return false;
        }
        if ((sch1 != null) ? sch1.equals(sch2) : (sch2 == null)) {
            return true;
        }
        return false;
    }
    
    @Override
    public final int hashCode() {
        if (this.hash == 0) {
            final String cat = (String)this.getProperty("table_cat");
            final String sch = (String)this.getProperty("table_schem");
            this.hash = (((cat == null) ? 0 : cat.hashCode()) ^ ((sch == null) ? 0 : sch.hashCode()));
        }
        return this.hash;
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer("RDBMSSchemaInfo : ");
        final Iterator iter = this.properties.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry entry = iter.next();
            str.append(entry.getKey() + " = " + entry.getValue());
            if (iter.hasNext()) {
                str.append(", ");
            }
        }
        str.append(", numTables=" + this.tables.size());
        return str.toString();
    }
}
