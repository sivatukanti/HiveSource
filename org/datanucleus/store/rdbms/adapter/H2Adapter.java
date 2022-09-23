// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.key.PrimaryKey;
import java.sql.Connection;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import org.datanucleus.store.rdbms.schema.H2TypeInfo;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

public class H2Adapter extends BaseDatastoreAdapter
{
    private String schemaName;
    
    public H2Adapter(final DatabaseMetaData metadata) {
        super(metadata);
        try {
            final ResultSet rs = metadata.getSchemas();
            while (rs.next()) {
                if (rs.getBoolean("IS_DEFAULT")) {
                    this.schemaName = rs.getString("TABLE_SCHEM");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        this.supportedOptions.add("PrimaryKeyInCreateStatements");
        this.supportedOptions.add("LockWithSelectForUpdate");
        this.supportedOptions.add("IdentityColumns");
        this.supportedOptions.add("CheckInEndCreateStatements");
        this.supportedOptions.add("UniqueInEndCreateStatements");
        this.supportedOptions.add("OrderByWithNullsDirectives");
        this.supportedOptions.remove("DeferredConstraints");
        this.supportedOptions.remove("TxIsolationReadRepeatableRead");
        this.supportedOptions.remove("TxIsolationNone");
        this.supportedOptions.add("CreateIndexesBeforeForeignKeys");
    }
    
    @Override
    public String getVendorID() {
        return "h2";
    }
    
    @Override
    public SQLTypeInfo newSQLTypeInfo(final ResultSet rs) {
        return new H2TypeInfo(rs);
    }
    
    @Override
    public int getDatastoreIdentifierMaxLength(final IdentifierType identifierType) {
        if (identifierType == IdentifierType.TABLE) {
            return 128;
        }
        if (identifierType == IdentifierType.COLUMN) {
            return 128;
        }
        if (identifierType == IdentifierType.CANDIDATE_KEY) {
            return 128;
        }
        if (identifierType == IdentifierType.FOREIGN_KEY) {
            return 128;
        }
        if (identifierType == IdentifierType.INDEX) {
            return 128;
        }
        if (identifierType == IdentifierType.PRIMARY_KEY) {
            return 128;
        }
        if (identifierType == IdentifierType.SEQUENCE) {
            return 128;
        }
        return super.getDatastoreIdentifierMaxLength(identifierType);
    }
    
    @Override
    public String getCreateDatabaseStatement(final String catalogName, final String schemaName) {
        return "CREATE SCHEMA IF NOT EXISTS " + catalogName;
    }
    
    @Override
    public String getDropDatabaseStatement(final String catalogName, final String schemaName) {
        return "DROP SCHEMA IF EXISTS " + catalogName;
    }
    
    @Override
    public String getAddColumnStatement(final Table table, final Column col) {
        return "ALTER TABLE " + table.toString() + " ADD COLUMN " + col.getSQLDefinition();
    }
    
    @Override
    public String getRangeByLimitEndOfStatementClause(final long offset, final long count) {
        if (offset >= 0L && count > 0L) {
            return "LIMIT " + count + " OFFSET " + offset + " ";
        }
        if (offset <= 0L && count > 0L) {
            return "LIMIT " + count + " ";
        }
        if (offset >= 0L && count < 0L) {
            return "LIMIT 2147483647 OFFSET " + offset + " ";
        }
        return "";
    }
    
    @Override
    public String getSchemaName(final Connection conn) throws SQLException {
        return this.schemaName;
    }
    
    @Override
    public String getAddPrimaryKeyStatement(final PrimaryKey pk, final IdentifierFactory factory) {
        return null;
    }
    
    @Override
    public String getDropTableStatement(final Table table) {
        return "DROP TABLE " + table.toString();
    }
    
    @Override
    public String getAutoIncrementStmt(final Table table, final String columnName) {
        return "CALL IDENTITY()";
    }
    
    @Override
    public String getAutoIncrementKeyword() {
        return "IDENTITY";
    }
    
    @Override
    public String getInsertStatementForNoColumns(final Table table) {
        return "INSERT INTO " + table.toString() + " VALUES(NULL)";
    }
    
    @Override
    public boolean isValidPrimaryKeyType(final int datatype) {
        return true;
    }
    
    @Override
    public String getSequenceCreateStmt(final String sequence_name, final Integer min, final Integer max, final Integer start, final Integer increment, final Integer cache_size) {
        if (sequence_name == null) {
            throw new NucleusUserException(H2Adapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("CREATE SEQUENCE IF NOT EXISTS ");
        stmt.append(sequence_name);
        if (min != null) {
            stmt.append(" START WITH " + min);
        }
        else if (start != null) {
            stmt.append(" START WITH " + start);
        }
        if (max != null) {
            throw new NucleusUserException(H2Adapter.LOCALISER.msg("051022"));
        }
        if (increment != null) {
            stmt.append(" INCREMENT BY " + increment);
        }
        if (cache_size != null) {
            stmt.append(" CACHE " + cache_size);
        }
        return stmt.toString();
    }
    
    @Override
    public String getSequenceNextStmt(final String sequence_name) {
        if (sequence_name == null) {
            throw new NucleusUserException(H2Adapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("CALL NEXT VALUE FOR ");
        stmt.append(sequence_name);
        return stmt.toString();
    }
    
    @Override
    public boolean isStatementCancel(final SQLException sqle) {
        return sqle.getErrorCode() == 90051;
    }
}
