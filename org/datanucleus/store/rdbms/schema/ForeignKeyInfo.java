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

public class ForeignKeyInfo implements StoreSchemaData
{
    Map properties;
    private int hash;
    
    public ForeignKeyInfo(final ResultSet rs) {
        this.properties = new HashMap();
        this.hash = 0;
        try {
            this.addProperty("pk_table_cat", rs.getString(1));
            this.addProperty("pk_table_schem", rs.getString(2));
            this.addProperty("pk_table_name", rs.getString(3));
            this.addProperty("pk_column_name", rs.getString(4));
            this.addProperty("fk_table_cat", rs.getString(5));
            this.addProperty("fk_table_schem", rs.getString(6));
            this.addProperty("fk_table_name", rs.getString(7));
            this.addProperty("fk_column_name", rs.getString(8));
            this.addProperty("key_seq", rs.getShort(9));
            this.addProperty("update_rule", rs.getShort(10));
            this.addProperty("delete_rule", rs.getShort(11));
            this.addProperty("fk_name", rs.getString(12));
            this.addProperty("pk_name", rs.getString(13));
            this.addProperty("deferrability", rs.getShort(14));
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
        if (!(obj instanceof ForeignKeyInfo)) {
            return false;
        }
        final ForeignKeyInfo other = (ForeignKeyInfo)obj;
        final String pkTableCat1 = (String)this.getProperty("pk_table_cat");
        final String pkTableSch1 = (String)this.getProperty("pk_table_schema");
        final String pkTableName1 = (String)this.getProperty("pk_table_name");
        final String pkColumnName1 = (String)this.getProperty("pk_column_name");
        final String fkTableCat1 = (String)this.getProperty("fk_table_cat");
        final String fkTableSch1 = (String)this.getProperty("fk_table_schema");
        final String fkTableName1 = (String)this.getProperty("fk_table_name");
        final String fkColumnName1 = (String)this.getProperty("fk_column_name");
        final String pkName1 = (String)this.getProperty("pk_name");
        final String fkName1 = (String)this.getProperty("fk_name");
        final String pkTableCat2 = (String)other.getProperty("pk_table_cat");
        final String pkTableSch2 = (String)other.getProperty("pk_table_schema");
        final String pkTableName2 = (String)other.getProperty("pk_table_name");
        final String pkColumnName2 = (String)other.getProperty("pk_column_name");
        final String fkTableCat2 = (String)other.getProperty("fk_table_cat");
        final String fkTableSch2 = (String)other.getProperty("fk_table_schema");
        final String fkTableName2 = (String)other.getProperty("fk_table_name");
        final String fkColumnName2 = (String)other.getProperty("fk_column_name");
        final String pkName2 = (String)other.getProperty("pk_name");
        final String fkName2 = (String)other.getProperty("fk_name");
        if (pkTableCat1 == null) {
            if (pkTableCat2 != null) {
                return false;
            }
        }
        else if (!pkTableCat1.equals(pkTableCat2)) {
            return false;
        }
        if (pkTableSch1 == null) {
            if (pkTableSch2 != null) {
                return false;
            }
        }
        else if (!pkTableSch1.equals(pkTableSch2)) {
            return false;
        }
        if (pkTableName1.equals(pkTableName2) && pkColumnName1.equals(pkColumnName2)) {
            if (fkTableCat1 == null) {
                if (fkTableCat2 != null) {
                    return false;
                }
            }
            else if (!fkTableCat1.equals(fkTableCat2)) {
                return false;
            }
            if (fkTableSch1 == null) {
                if (fkTableSch2 != null) {
                    return false;
                }
            }
            else if (!fkTableSch1.equals(fkTableSch2)) {
                return false;
            }
            if (fkTableName1.equals(fkTableName2) && fkColumnName1.equals(fkColumnName2)) {
                if (fkName1 == null) {
                    if (fkName2 != null) {
                        return false;
                    }
                }
                else if (!fkName1.equals(fkName2)) {
                    return false;
                }
                if ((pkName1 != null) ? pkName1.equals(pkName2) : (pkName2 == null)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public final int hashCode() {
        if (this.hash == 0) {
            final String pkTableCat = (String)this.getProperty("pk_table_cat");
            final String pkTableSch = (String)this.getProperty("pk_table_schema");
            final String pkTableName = (String)this.getProperty("pk_table_name");
            final String pkColumnName = (String)this.getProperty("pk_column_name");
            final String fkTableCat = (String)this.getProperty("fk_table_cat");
            final String fkTableSch = (String)this.getProperty("fk_table_schema");
            final String fkTableName = (String)this.getProperty("fk_table_name");
            final String fkColumnName = (String)this.getProperty("fk_column_name");
            this.hash = (((pkTableCat == null) ? 0 : pkTableCat.hashCode()) ^ ((pkTableSch == null) ? 0 : pkTableSch.hashCode()) ^ pkTableName.hashCode() ^ pkColumnName.hashCode() ^ ((fkTableCat == null) ? 0 : fkTableCat.hashCode()) ^ ((fkTableSch == null) ? 0 : fkTableSch.hashCode()) ^ fkTableName.hashCode() ^ fkColumnName.hashCode());
        }
        return this.hash;
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer();
        str.append(this.getClass().getName() + "\n");
        str.append("  pkTableCat    = " + this.getProperty("pk_table_cat") + "\n");
        str.append("  pkTableSchem  = " + this.getProperty("pk_table_schema") + "\n");
        str.append("  pkTableName   = " + this.getProperty("pk_table_name") + "\n");
        str.append("  pkColumnName  = " + this.getProperty("pk_column_name") + "\n");
        str.append("  fkTableCat    = " + this.getProperty("fk_table_cat") + "\n");
        str.append("  fkTableSchem  = " + this.getProperty("fk_table_schema") + "\n");
        str.append("  fkTableName   = " + this.getProperty("fk_table_name") + "\n");
        str.append("  fkColumnName  = " + this.getProperty("fk_column_name") + "\n");
        str.append("  keySeq        = " + this.getProperty("key_seq") + "\n");
        str.append("  updateRule    = " + this.getProperty("update_rule") + "\n");
        str.append("  deleteRule    = " + this.getProperty("delete_rule") + "\n");
        str.append("  fkName        = " + this.getProperty("fk_name") + "\n");
        str.append("  pkName        = " + this.getProperty("pk_name") + "\n");
        str.append("  deferrability = " + this.getProperty("deferrability") + "\n");
        return str.toString();
    }
}
