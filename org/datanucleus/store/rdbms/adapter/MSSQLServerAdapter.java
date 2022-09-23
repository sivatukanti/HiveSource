// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.sql.SQLText;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.key.Index;
import java.sql.ResultSet;
import java.sql.Statement;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.SQLException;
import java.sql.Connection;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.schema.MSSQLTypeInfo;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.StoreSchemaHandler;
import java.util.Collection;
import java.sql.DatabaseMetaData;

public class MSSQLServerAdapter extends BaseDatastoreAdapter
{
    private static final String MSSQL_RESERVED_WORDS = "ADD,ALL,ALTER,AND,ANY,AS,ASC,AUTHORIZATION,BACKUP,BEGIN,BETWEEN,BREAK,BROWSE,BULK,BY,CASCADE,CASE,CHECK,CHECKPOINT,CLOSE,CLUSTERED,COALESCE,COLLATE,COLUMN,COMMIT,COMPUTE,CONSTRAINT,CONTAINS,CONTAINSTABLE,CONTINUE,CONVERT,CREATE,CROSS,CURRENT,CURRENT_DATE,CURRENT_TIME,CURRENT_TIMESTAMP,CURRENT_USER,CURSOR,DBCC,DEALLOCATE,DECLARE,DEFAULT,DELETE,DENY,DESC,DISK,DISTINCT,DISTRIBUTED,DOUBLE,DROP,DUMMY,DUMP,ELSE,END,ERRLVL,ESCAPE,EXCEPT,EXEC,EXECUTE,EXISTS,EXIT,FETCH,FILE,FILLFACTOR,FOR,FOREIGN,FREETEXT,FREETEXTTABLE,FROM,FULL,FUNCTION,GOTO,GRANT,GROUP,HAVING,HOLDLOCK,IDENTITY,IDENTITY_INSERT,IDENTITYCOL,IF,IN,INDEX,INNER,INSERT,INTERSECT,INTO,IS,JOIN,KEY,KILL,LEFT,LIKE,LINENO,LOAD,NATIONAL,NOCHECK,NONCLUSTERED,NOT,NULL,NULLIF,OF,OFF,OFFSETS,ON,OPEN,OPENDATASOURCE,OPENQUERY,OPENROWSET,OPENXML,OPTION,OR,ORDER,OUTER,OVER,PERCENT,PLAN,PRECISION,PRIMARY,PRINT,PROC,PROCEDURE,PUBLIC,RAISERROR,READ,READTEXT,RECONFIGURE,REFERENCES,REPLICATION,RESTORE,RESTRICT,RETURN,REVOKE,RIGHT,ROLLBACK,ROWCOUNT,ROWGUIDCOL,RULE,SAVE,SCHEMA,SELECT,SESSION_USER,SET,SETUSER,SHUTDOWN,SOME,STATISTICS,SYSTEM_USER,TABLE,TEXTSIZE,THEN,TO,TOP,TRAN,DATABASE,TRANSACTION,TRIGGER,TRUNCATE,TSEQUAL,UNION,UNIQUE,UPDATE,UPDATETEXT,USE,USER,VALUES,VARYING,VIEW,WAITFOR,WHEN,WHERE,WHILE,WITH,WRITETEXT";
    
    public MSSQLServerAdapter(final DatabaseMetaData metadata) {
        super(metadata);
        this.reservedKeywords.addAll((Collection<?>)this.parseKeywordList("ADD,ALL,ALTER,AND,ANY,AS,ASC,AUTHORIZATION,BACKUP,BEGIN,BETWEEN,BREAK,BROWSE,BULK,BY,CASCADE,CASE,CHECK,CHECKPOINT,CLOSE,CLUSTERED,COALESCE,COLLATE,COLUMN,COMMIT,COMPUTE,CONSTRAINT,CONTAINS,CONTAINSTABLE,CONTINUE,CONVERT,CREATE,CROSS,CURRENT,CURRENT_DATE,CURRENT_TIME,CURRENT_TIMESTAMP,CURRENT_USER,CURSOR,DBCC,DEALLOCATE,DECLARE,DEFAULT,DELETE,DENY,DESC,DISK,DISTINCT,DISTRIBUTED,DOUBLE,DROP,DUMMY,DUMP,ELSE,END,ERRLVL,ESCAPE,EXCEPT,EXEC,EXECUTE,EXISTS,EXIT,FETCH,FILE,FILLFACTOR,FOR,FOREIGN,FREETEXT,FREETEXTTABLE,FROM,FULL,FUNCTION,GOTO,GRANT,GROUP,HAVING,HOLDLOCK,IDENTITY,IDENTITY_INSERT,IDENTITYCOL,IF,IN,INDEX,INNER,INSERT,INTERSECT,INTO,IS,JOIN,KEY,KILL,LEFT,LIKE,LINENO,LOAD,NATIONAL,NOCHECK,NONCLUSTERED,NOT,NULL,NULLIF,OF,OFF,OFFSETS,ON,OPEN,OPENDATASOURCE,OPENQUERY,OPENROWSET,OPENXML,OPTION,OR,ORDER,OUTER,OVER,PERCENT,PLAN,PRECISION,PRIMARY,PRINT,PROC,PROCEDURE,PUBLIC,RAISERROR,READ,READTEXT,RECONFIGURE,REFERENCES,REPLICATION,RESTORE,RESTRICT,RETURN,REVOKE,RIGHT,ROLLBACK,ROWCOUNT,ROWGUIDCOL,RULE,SAVE,SCHEMA,SELECT,SESSION_USER,SET,SETUSER,SHUTDOWN,SOME,STATISTICS,SYSTEM_USER,TABLE,TEXTSIZE,THEN,TO,TOP,TRAN,DATABASE,TRANSACTION,TRIGGER,TRUNCATE,TSEQUAL,UNION,UNIQUE,UPDATE,UPDATETEXT,USE,USER,VALUES,VARYING,VIEW,WAITFOR,WHEN,WHERE,WHILE,WITH,WRITETEXT"));
        this.supportedOptions.add("IdentityColumns");
        this.supportedOptions.add("LockOptionAfterFromClause");
        this.supportedOptions.add("LockOptionWithinJoinClause");
        this.supportedOptions.add("AnalysisMethods");
        this.supportedOptions.add("StoredProcs");
        this.supportedOptions.remove("BooleanExpression");
        this.supportedOptions.remove("DeferredConstraints");
        this.supportedOptions.remove("FkDeleteActionDefault");
        this.supportedOptions.remove("FkDeleteActionRestrict");
        this.supportedOptions.remove("FkDeleteActionNull");
        this.supportedOptions.remove("FkUpdateActionDefault");
        this.supportedOptions.remove("FkUpdateActionRestrict");
        this.supportedOptions.remove("FkUpdateActionNull");
    }
    
    @Override
    public void initialiseTypes(final StoreSchemaHandler handler, final ManagedConnection mconn) {
        super.initialiseTypes(handler, mconn);
        SQLTypeInfo sqlType = new MSSQLTypeInfo("UNIQUEIDENTIFIER", (short)1, 36, "'", "'", "", 1, false, (short)2, false, false, false, "UNIQUEIDENTIFIER", (short)0, (short)0, 10);
        sqlType.setAllowsPrecisionSpec(false);
        this.addSQLTypeForJDBCType(handler, mconn, (short)(-11), sqlType, true);
        sqlType = new MSSQLTypeInfo("IMAGE", (short)2004, Integer.MAX_VALUE, null, null, null, 1, false, (short)1, false, false, false, "BLOB", (short)0, (short)0, 0);
        this.addSQLTypeForJDBCType(handler, mconn, (short)2004, sqlType, true);
        sqlType = new MSSQLTypeInfo("TEXT", (short)2005, Integer.MAX_VALUE, null, null, null, 1, true, (short)1, false, false, false, "TEXT", (short)0, (short)0, 0);
        this.addSQLTypeForJDBCType(handler, mconn, (short)2005, sqlType, true);
        sqlType = new MSSQLTypeInfo("float", (short)8, 53, null, null, null, 1, false, (short)2, false, false, false, null, (short)0, (short)0, 2);
        this.addSQLTypeForJDBCType(handler, mconn, (short)8, sqlType, true);
        sqlType = new MSSQLTypeInfo("IMAGE", (short)(-4), Integer.MAX_VALUE, null, null, null, 1, false, (short)1, false, false, false, "LONGVARBINARY", (short)0, (short)0, 0);
        this.addSQLTypeForJDBCType(handler, mconn, (short)(-4), sqlType, true);
    }
    
    @Override
    public String getVendorID() {
        return "sqlserver";
    }
    
    @Override
    public String getCatalogName(final Connection conn) throws SQLException {
        final String catalog = conn.getCatalog();
        return (catalog != null) ? catalog : "";
    }
    
    @Override
    public String getSchemaName(final Connection conn) throws SQLException {
        if (this.datastoreMajorVersion >= 9) {
            final Statement stmt = conn.createStatement();
            try {
                final String stmtText = "SELECT SCHEMA_NAME();";
                final ResultSet rs = stmt.executeQuery(stmtText);
                try {
                    if (!rs.next()) {
                        throw new NucleusDataStoreException("No result returned from " + stmtText).setFatal();
                    }
                    return rs.getString(1);
                }
                finally {
                    rs.close();
                }
            }
            finally {
                stmt.close();
            }
        }
        return "";
    }
    
    @Override
    public boolean isReservedKeyword(final String word) {
        return super.isReservedKeyword(word) || (word != null && word.indexOf(32) >= 0);
    }
    
    @Override
    public String getDropDatabaseStatement(final String catalogName, final String schemaName) {
        throw new UnsupportedOperationException("SQLServer does not support dropping schema with cascade. You need to drop all tables first");
    }
    
    @Override
    public String getCreateIndexStatement(final Index idx, final IdentifierFactory factory) {
        final String idxIdentifier = factory.getIdentifierInAdapterCase(idx.getName());
        return "CREATE " + (idx.getUnique() ? "UNIQUE " : "") + "INDEX " + idxIdentifier + " ON " + idx.getTable().toString() + ' ' + idx + ((idx.getExtendedIndexSettings() == null) ? "" : (" " + idx.getExtendedIndexSettings()));
    }
    
    @Override
    public String getSelectNewUUIDStmt() {
        return "SELECT NEWID()";
    }
    
    @Override
    public String getNewUUIDFunction() {
        return "NEWID()";
    }
    
    @Override
    public boolean supportsQueryFetchSize(final int size) {
        return size >= 1;
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
    public SQLTypeInfo newSQLTypeInfo(final ResultSet rs) {
        final SQLTypeInfo ti = new MSSQLTypeInfo(rs);
        final String typeName = ti.getTypeName();
        if (typeName.toLowerCase().startsWith("tinyint")) {
            return null;
        }
        return ti;
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
    public String getAutoIncrementStmt(final Table table, final String columnName) {
        return "SELECT @@IDENTITY";
    }
    
    @Override
    public String getAutoIncrementKeyword() {
        return "IDENTITY";
    }
    
    @Override
    public boolean isIdentityFieldDataType(final String columnDef) {
        return columnDef != null && columnDef.equalsIgnoreCase("uniqueidentifier");
    }
    
    @Override
    public String getInsertStatementForNoColumns(final Table table) {
        return "INSERT INTO " + table.toString() + " DEFAULT VALUES";
    }
    
    @Override
    public String getOperatorConcat() {
        return "+";
    }
    
    @Override
    public String getSelectWithLockOption() {
        return "(UPDLOCK, ROWLOCK)";
    }
    
    @Override
    public boolean validToSelectMappingInStatement(final SQLStatement stmt, final JavaTypeMapping m) {
        if (m.getNumberOfDatastoreMappings() <= 0) {
            return true;
        }
        for (int i = 0; i < m.getNumberOfDatastoreMappings(); ++i) {
            final Column col = m.getDatastoreMapping(i).getColumn();
            if (col.getJdbcType() == 2005 || col.getJdbcType() == 2004) {
                if (stmt.isDistinct()) {
                    NucleusLogger.QUERY.debug("Not selecting " + m + " since is for BLOB/CLOB and using DISTINCT");
                    return false;
                }
                if (stmt.getNumberOfUnions() > 0) {
                    NucleusLogger.QUERY.debug("Not selecting " + m + " since is for BLOB/CLOB and using UNION");
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean isStatementTimeout(final SQLException sqle) {
        return (sqle.getSQLState() != null && sqle.getSQLState().equalsIgnoreCase("HY008")) || super.isStatementTimeout(sqle);
    }
}
