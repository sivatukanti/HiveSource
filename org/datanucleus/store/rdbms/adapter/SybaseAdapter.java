// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.sql.SQLText;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.table.Table;
import java.sql.DatabaseMetaData;

public class SybaseAdapter extends BaseDatastoreAdapter
{
    public SybaseAdapter(final DatabaseMetaData metadata) {
        super(metadata);
        this.supportedOptions.add("IdentityColumns");
        this.supportedOptions.add("StoredProcs");
        this.supportedOptions.remove("DeferredConstraints");
        this.supportedOptions.remove("BooleanExpression");
        this.supportedOptions.remove("LockWithSelectForUpdate");
        this.supportedOptions.remove("AutoIncrementNullSpecification");
    }
    
    @Override
    public String getVendorID() {
        return "sybase";
    }
    
    @Override
    public String getDropDatabaseStatement(final String catalogName, final String schemaName) {
        throw new UnsupportedOperationException("Sybase does not support dropping schema with cascade. You need to drop all tables first");
    }
    
    @Override
    public String getDropTableStatement(final Table table) {
        return "DROP TABLE " + table.toString();
    }
    
    @Override
    public String getDeleteTableStatement(final SQLTable tbl) {
        return "DELETE " + tbl.getAlias() + " FROM " + tbl.toString();
    }
    
    @Override
    public SQLText getUpdateTableStatement(final SQLTable tbl, final SQLText setSQL) {
        final SQLText sql = new SQLText("UPDATE ").append(tbl.getAlias().toString());
        sql.append(" ").append(setSQL);
        sql.append(" FROM ").append(tbl.toString());
        return sql;
    }
    
    @Override
    public SQLTypeInfo newSQLTypeInfo(final ResultSet rs) {
        final SQLTypeInfo info = new SQLTypeInfo(rs);
        if (info.getTypeName().toLowerCase().startsWith("tinyint")) {
            return null;
        }
        if (info.getTypeName().toLowerCase().startsWith("longsysname")) {
            return null;
        }
        return info;
    }
    
    @Override
    public RDBMSColumnInfo newRDBMSColumnInfo(final ResultSet rs) {
        final RDBMSColumnInfo info = new RDBMSColumnInfo(rs);
        final short dataType = info.getDataType();
        switch (dataType) {
            case 91:
            case 92:
            case 93: {
                info.setDecimalDigits(0);
                break;
            }
        }
        return info;
    }
    
    @Override
    public String getAutoIncrementStmt(final Table table, final String columnName) {
        return "SELECT @@IDENTITY";
    }
    
    @Override
    public String getAutoIncrementKeyword() {
        return "IDENTITY";
    }
}
