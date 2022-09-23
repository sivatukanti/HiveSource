// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.util.HashMap;
import java.sql.ResultSet;
import java.util.Map;
import org.datanucleus.store.schema.StoreSchemaData;

public class PrimaryKeyInfo implements StoreSchemaData
{
    Map properties;
    private int hash;
    
    public PrimaryKeyInfo(final ResultSet rs) {
        this.properties = new HashMap();
        this.hash = 0;
        try {
            this.addProperty("table_cat", rs.getString(1));
            this.addProperty("table_schem", rs.getString(2));
            this.addProperty("table_name", rs.getString(3));
            this.addProperty("column_name", rs.getString(4));
            this.addProperty("key_seq", rs.getShort(5));
            this.addProperty("pk_name", rs.getString(6));
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException("Can't read JDBC metadata from result set", e).setFatal();
        }
    }
    
    @Override
    public void addProperty(final String name, final Object value) {
        if (name != null && value != null) {
            this.properties.put(name, value);
        }
    }
    
    @Override
    public Object getProperty(final String name) {
        return this.properties.get(name);
    }
    
    @Override
    public final boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PrimaryKeyInfo)) {
            return false;
        }
        final PrimaryKeyInfo other = (PrimaryKeyInfo)obj;
        final String tableCat1 = (String)this.getProperty("table_cat");
        final String tableSch1 = (String)this.getProperty("table_schema");
        final String tableName1 = (String)this.getProperty("table_name");
        final String columnName1 = (String)this.getProperty("column_name");
        final String pkName1 = (String)this.getProperty("pk_name");
        final String tableCat2 = (String)other.getProperty("table_cat");
        final String tableSch2 = (String)other.getProperty("table_schema");
        final String tableName2 = (String)other.getProperty("table_name");
        final String columnName2 = (String)other.getProperty("column_name");
        final String pkName2 = (String)other.getProperty("pk_name");
        if (tableCat1 == null) {
            if (tableCat2 != null) {
                return false;
            }
        }
        else if (!tableCat1.equals(tableCat2)) {
            return false;
        }
        if (tableSch1 == null) {
            if (tableSch2 != null) {
                return false;
            }
        }
        else if (!tableSch1.equals(tableSch2)) {
            return false;
        }
        if (tableName1.equals(tableName2) && columnName1.equals(columnName2)) {
            if (tableCat1 == null) {
                if (tableCat2 != null) {
                    return false;
                }
            }
            else if (!tableCat1.equals(tableCat2)) {
                return false;
            }
            if (tableSch1 == null) {
                if (tableSch2 != null) {
                    return false;
                }
            }
            else if (!tableSch1.equals(tableSch2)) {
                return false;
            }
            if (tableName1.equals(tableName2) && columnName1.equals(columnName2) && ((pkName1 != null) ? pkName1.equals(pkName2) : (pkName2 == null))) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public final int hashCode() {
        if (this.hash == 0) {
            final String tableCat = (String)this.getProperty("table_cat");
            final String tableSch = (String)this.getProperty("table_schema");
            final String tableName = (String)this.getProperty("table_name");
            final String columnName = (String)this.getProperty("column_name");
            this.hash = (((tableCat == null) ? 0 : tableCat.hashCode()) ^ ((tableSch == null) ? 0 : tableSch.hashCode()) ^ tableName.hashCode() ^ columnName.hashCode());
        }
        return this.hash;
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer();
        str.append(this.getClass().getName() + "\n");
        str.append("  tableCat    = " + this.getProperty("table_cat") + "\n");
        str.append("  tableSchem  = " + this.getProperty("table_schema") + "\n");
        str.append("  tableName   = " + this.getProperty("table_name") + "\n");
        str.append("  columnName  = " + this.getProperty("column_name") + "\n");
        str.append("  keySeq      = " + this.getProperty("key_seq") + "\n");
        str.append("  pkName      = " + this.getProperty("pk_name") + "\n");
        return str.toString();
    }
}
