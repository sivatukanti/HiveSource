// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.key.PrimaryKey;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import java.util.Iterator;
import java.util.Collection;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.schema.HSQLTypeInfo;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.StoreSchemaHandler;
import java.sql.DatabaseMetaData;

public class HSQLAdapter extends BaseDatastoreAdapter
{
    public HSQLAdapter(final DatabaseMetaData metadata) {
        super(metadata);
        this.supportedOptions.add("PrimaryKeyInCreateStatements");
        this.supportedOptions.add("IdentityColumns");
        this.supportedOptions.add("Sequences");
        this.supportedOptions.add("UniqueInEndCreateStatements");
        if (this.datastoreMajorVersion < 2) {
            this.supportedOptions.remove("StatementBatching");
            this.supportedOptions.remove("GetGeneratedKeysStatement");
        }
        this.supportedOptions.remove("DeferredConstraints");
        this.supportedOptions.remove("CheckInCreateStatements");
        this.supportedOptions.remove("AutoIncrementNullSpecification");
        if (this.datastoreMajorVersion >= 2) {
            this.supportedOptions.add("LockWithSelectForUpdate");
            this.supportedOptions.add("OrderByWithNullsDirectives");
        }
        if (this.datastoreMajorVersion < 1 || (this.datastoreMajorVersion == 1 && this.datastoreMinorVersion < 7) || (this.datastoreMajorVersion == 1 && this.datastoreMinorVersion == 7 && this.datastoreRevisionVersion < 2)) {
            this.supportedOptions.remove("CheckInEndCreateStatements");
        }
        else {
            this.supportedOptions.add("CheckInEndCreateStatements");
        }
        this.supportedOptions.remove("AccessParentQueryInSubquery");
        if (this.datastoreMajorVersion < 1 || (this.datastoreMajorVersion == 1 && this.datastoreMinorVersion < 7)) {
            this.supportedOptions.remove("FkDeleteActionCascade");
            this.supportedOptions.remove("FkDeleteActionRestrict");
            this.supportedOptions.remove("FkDeleteActionDefault");
            this.supportedOptions.remove("FkDeleteActionNull");
            this.supportedOptions.remove("FkUpdateActionCascade");
            this.supportedOptions.remove("FkUpdateActionRestrict");
            this.supportedOptions.remove("FkUpdateActionDefault");
            this.supportedOptions.remove("FkUpdateActionNull");
        }
        else if (this.datastoreMajorVersion < 2) {
            this.supportedOptions.remove("FkDeleteActionRestrict");
            this.supportedOptions.remove("FkUpdateActionRestrict");
        }
        if (this.datastoreMajorVersion < 2) {
            this.supportedOptions.remove("TxIsolationReadRepeatableRead");
            if (this.datastoreMinorVersion <= 7) {
                this.supportedOptions.remove("TxIsolationReadCommitted");
                this.supportedOptions.remove("TxIsolationSerializable");
            }
        }
        this.supportedOptions.remove("TxIsolationNone");
    }
    
    @Override
    public void initialiseTypes(final StoreSchemaHandler handler, final ManagedConnection mconn) {
        super.initialiseTypes(handler, mconn);
        SQLTypeInfo sqlType = new HSQLTypeInfo("LONGVARCHAR", (short)2005, Integer.MAX_VALUE, "'", "'", null, 1, true, (short)3, false, false, false, "LONGVARCHAR", (short)0, (short)0, 0);
        this.addSQLTypeForJDBCType(handler, mconn, (short)2005, sqlType, true);
        sqlType = new HSQLTypeInfo("LONGVARBINARY", (short)2004, Integer.MAX_VALUE, "'", "'", null, 1, false, (short)3, false, false, false, "LONGVARBINARY", (short)0, (short)0, 0);
        this.addSQLTypeForJDBCType(handler, mconn, (short)2004, sqlType, true);
        sqlType = new HSQLTypeInfo("LONGVARCHAR", (short)(-1), Integer.MAX_VALUE, "'", "'", null, 1, true, (short)3, false, false, false, "LONGVARCHAR", (short)0, (short)0, 0);
        this.addSQLTypeForJDBCType(handler, mconn, (short)(-1), sqlType, true);
        Collection<SQLTypeInfo> sqlTypes = this.getSQLTypeInfoForJdbcType(handler, mconn, (short)2004);
        if (sqlTypes != null) {
            final Iterator<SQLTypeInfo> iter = sqlTypes.iterator();
            while (iter.hasNext()) {
                sqlType = iter.next();
                sqlType.setAllowsPrecisionSpec(false);
            }
        }
        sqlTypes = this.getSQLTypeInfoForJdbcType(handler, mconn, (short)2005);
        if (sqlTypes != null) {
            final Iterator<SQLTypeInfo> iter = sqlTypes.iterator();
            while (iter.hasNext()) {
                sqlType = iter.next();
                sqlType.setAllowsPrecisionSpec(false);
            }
        }
        sqlTypes = this.getSQLTypeInfoForJdbcType(handler, mconn, (short)(-4));
        if (sqlTypes != null) {
            final Iterator<SQLTypeInfo> iter = sqlTypes.iterator();
            while (iter.hasNext()) {
                sqlType = iter.next();
                sqlType.setAllowsPrecisionSpec(false);
            }
        }
        sqlTypes = this.getSQLTypeInfoForJdbcType(handler, mconn, (short)(-1));
        if (sqlTypes != null) {
            final Iterator<SQLTypeInfo> iter = sqlTypes.iterator();
            while (iter.hasNext()) {
                sqlType = iter.next();
                sqlType.setAllowsPrecisionSpec(false);
            }
        }
    }
    
    @Override
    public String getVendorID() {
        return "hsql";
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
    public String getDropDatabaseStatement(final String catalogName, final String schemaName) {
        return "DROP SCHEMA IF EXISTS " + schemaName + " CASCADE";
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
    public SQLTypeInfo newSQLTypeInfo(final ResultSet rs) {
        return new HSQLTypeInfo(rs);
    }
    
    @Override
    public String getSchemaName(final Connection conn) throws SQLException {
        return "";
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
        return "GENERATED BY DEFAULT AS IDENTITY";
    }
    
    @Override
    public String getInsertStatementForNoColumns(final Table table) {
        return "INSERT INTO " + table.toString() + " VALUES (null)";
    }
    
    @Override
    public boolean isValidPrimaryKeyType(final int datatype) {
        return datatype != 2004 && datatype != 2005 && datatype != -4 && datatype != 1111 && datatype != -1;
    }
    
    @Override
    public String getDatastoreDateStatement() {
        return "CALL NOW()";
    }
    
    @Override
    public String getSequenceCreateStmt(final String sequence_name, final Integer min, final Integer max, final Integer start, final Integer increment, final Integer cache_size) {
        if (sequence_name == null) {
            throw new NucleusUserException(HSQLAdapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("CREATE SEQUENCE ");
        stmt.append(sequence_name);
        if (min != null) {
            stmt.append(" START WITH " + min);
        }
        else if (start != null) {
            stmt.append(" START WITH " + start);
        }
        if (max != null) {
            throw new NucleusUserException(HSQLAdapter.LOCALISER.msg("051022"));
        }
        if (increment != null) {
            stmt.append(" INCREMENT BY " + increment);
        }
        if (cache_size != null) {
            throw new NucleusUserException(HSQLAdapter.LOCALISER.msg("051023"));
        }
        return stmt.toString();
    }
    
    @Override
    public String getSequenceNextStmt(final String sequence_name) {
        if (sequence_name == null) {
            throw new NucleusUserException(HSQLAdapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("CALL NEXT VALUE FOR ");
        stmt.append(sequence_name);
        return stmt.toString();
    }
}
