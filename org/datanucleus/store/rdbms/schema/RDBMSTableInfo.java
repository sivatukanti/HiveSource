// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import java.util.Iterator;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import org.datanucleus.store.schema.StoreSchemaData;
import java.util.List;
import java.util.Map;
import org.datanucleus.store.schema.ListStoreSchemaData;

public class RDBMSTableInfo implements ListStoreSchemaData
{
    private int hash;
    Map<String, Object> properties;
    List columns;
    Map<String, StoreSchemaData> columnMapByColumnName;
    
    public RDBMSTableInfo() {
        this.hash = 0;
        this.properties = new HashMap<String, Object>();
        this.columns = new ArrayList();
        this.columnMapByColumnName = new HashMap<String, StoreSchemaData>();
    }
    
    public RDBMSTableInfo(final String catalog, final String schema, final String table) {
        this.hash = 0;
        this.properties = new HashMap<String, Object>();
        this.columns = new ArrayList();
        this.columnMapByColumnName = new HashMap<String, StoreSchemaData>();
        this.addProperty("table_cat", catalog);
        this.addProperty("table_schem", schema);
        this.addProperty("table_name", table);
    }
    
    public RDBMSTableInfo(final ResultSet rs) {
        this.hash = 0;
        this.properties = new HashMap<String, Object>();
        this.columns = new ArrayList();
        this.columnMapByColumnName = new HashMap<String, StoreSchemaData>();
        try {
            this.addProperty("table_cat", rs.getString(1));
            this.addProperty("table_schem", rs.getString(2));
            this.addProperty("table_name", rs.getString(3));
            this.addProperty("table_type", rs.getString(4));
            this.addProperty("remarks", rs.getString(5));
            if (rs.getMetaData().getColumnCount() > 5) {
                this.addProperty("type_cat", rs.getString(6));
                this.addProperty("type_schem", rs.getString(7));
                this.addProperty("type_name", rs.getString(8));
                this.addProperty("self_referencing_col_name", rs.getString(9));
                this.addProperty("ref_generation", rs.getString(10));
            }
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException("Exception thrown obtaining schema table information from datastore", sqle);
        }
    }
    
    @Override
    public void addChild(final StoreSchemaData child) {
        final RDBMSColumnInfo col = (RDBMSColumnInfo)child;
        this.columns.add(col);
        this.columnMapByColumnName.put(col.getColumnName(), child);
    }
    
    @Override
    public void clearChildren() {
        this.columns.clear();
        this.columnMapByColumnName.clear();
    }
    
    @Override
    public StoreSchemaData getChild(final int position) {
        return this.columns.get(position);
    }
    
    public StoreSchemaData getChild(final String key) {
        return this.columnMapByColumnName.get(key);
    }
    
    @Override
    public List getChildren() {
        return this.columns;
    }
    
    @Override
    public int getNumberOfChildren() {
        return this.columns.size();
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
        if (!(obj instanceof RDBMSTableInfo)) {
            return false;
        }
        final RDBMSTableInfo other = (RDBMSTableInfo)obj;
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
        final StringBuffer str = new StringBuffer("RDBMSTableInfo : ");
        final Iterator iter = this.properties.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry entry = iter.next();
            str.append(entry.getKey() + " = " + entry.getValue());
            if (iter.hasNext()) {
                str.append(", ");
            }
        }
        str.append(", numColumns=" + this.columns.size());
        return str.toString();
    }
}
