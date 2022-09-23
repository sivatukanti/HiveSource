// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.store.rdbms.key.ForeignKey;
import org.datanucleus.store.rdbms.key.CandidateKey;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.key.PrimaryKey;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.schema.SQLiteTypeInfo;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.StoreSchemaHandler;
import java.sql.DatabaseMetaData;

public class SQLiteAdapter extends BaseDatastoreAdapter
{
    protected static final int MAX_IDENTIFIER_LENGTH = 128;
    
    public SQLiteAdapter(final DatabaseMetaData metadata) {
        super(metadata);
        this.supportedOptions.add("PrimaryKeyInCreateStatements");
        this.supportedOptions.add("CheckInEndCreateStatements");
        this.supportedOptions.add("UniqueInEndCreateStatements");
        this.supportedOptions.add("FKInEndCreateStatements");
        this.supportedOptions.add("IdentityColumns");
        this.supportedOptions.add("AutoIncrementPkInCreateTableColumnDef");
        this.supportedOptions.remove("TxIsolationReadCommitted");
        this.supportedOptions.remove("TxIsolationReadRepeatableRead");
        this.supportedOptions.remove("AutoIncrementNullSpecification");
        this.maxTableNameLength = 128;
        this.maxColumnNameLength = 128;
        this.maxConstraintNameLength = 128;
        this.maxIndexNameLength = 128;
    }
    
    @Override
    public String getVendorID() {
        return "sqlite";
    }
    
    @Override
    public void initialiseTypes(final StoreSchemaHandler handler, final ManagedConnection mconn) {
        super.initialiseTypes(handler, mconn);
        SQLTypeInfo sqlType = new SQLiteTypeInfo("double", (short)8, 0, null, null, null, 1, true, (short)3, false, false, false, "double", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)8, sqlType, true);
        sqlType = new SQLiteTypeInfo("float", (short)6, 0, null, null, null, 1, true, (short)3, false, false, false, "float", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)6, sqlType, true);
        sqlType = new SQLiteTypeInfo("decimal", (short)3, 0, null, null, null, 1, true, (short)3, false, false, false, "decimal", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)3, sqlType, true);
        sqlType = new SQLiteTypeInfo("numeric", (short)2, 0, null, null, null, 1, true, (short)3, false, false, false, "numeric", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)2, sqlType, true);
        sqlType = new SQLiteTypeInfo("integer", (short)16, 0, null, null, null, 1, true, (short)3, false, false, false, "integer", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)16, sqlType, true);
        sqlType = new SQLiteTypeInfo("integer", (short)(-7), 0, null, null, null, 1, true, (short)3, false, false, false, "integer", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)(-7), sqlType, true);
        sqlType = new SQLiteTypeInfo("tinyint", (short)(-6), 0, null, null, null, 1, true, (short)3, false, false, false, "tinyint", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)(-6), sqlType, true);
        sqlType = new SQLiteTypeInfo("smallint", (short)5, 0, null, null, null, 1, true, (short)3, false, false, false, "smallint", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)5, sqlType, true);
        sqlType = new SQLiteTypeInfo("bigint", (short)(-5), 0, null, null, null, 1, true, (short)3, false, false, false, "bigint", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)(-5), sqlType, true);
        sqlType = new SQLiteTypeInfo("char", (short)1, 255, null, null, null, 1, true, (short)3, false, false, false, "char", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)1, sqlType, true);
        sqlType = new SQLiteTypeInfo("varchar", (short)12, 255, null, null, null, 1, true, (short)3, false, false, false, "varchar", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)12, sqlType, true);
        sqlType = new SQLiteTypeInfo("longvarchar", (short)(-1), 16777215, null, null, null, 1, true, (short)3, false, false, false, "longvarchar", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)(-1), sqlType, true);
        sqlType = new SQLiteTypeInfo("clob", (short)2005, Integer.MAX_VALUE, null, null, null, 1, true, (short)3, false, false, false, "clob", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)2005, sqlType, true);
        sqlType = new SQLiteTypeInfo("date", (short)91, 0, null, null, null, 1, true, (short)3, false, false, false, "date", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)91, sqlType, true);
        sqlType = new SQLiteTypeInfo("time", (short)92, 0, null, null, null, 1, true, (short)3, false, false, false, "time", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)92, sqlType, true);
        sqlType = new SQLiteTypeInfo("timestamp", (short)93, 0, null, null, null, 1, true, (short)3, false, false, false, "timestamp", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)93, sqlType, true);
        sqlType = new SQLiteTypeInfo("blob", (short)(-2), 255, null, null, null, 1, true, (short)3, false, false, false, "blob", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)(-2), sqlType, true);
        sqlType = new SQLiteTypeInfo("blob", (short)(-3), 255, null, null, null, 1, true, (short)3, false, false, false, "blob", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)(-3), sqlType, true);
        sqlType = new SQLiteTypeInfo("blob", (short)(-4), 16777215, null, null, null, 1, true, (short)3, false, false, false, "blob", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)(-4), sqlType, true);
    }
    
    @Override
    public String getCreateDatabaseStatement(final String catalogName, final String schemaName) {
        throw new UnsupportedOperationException("SQLite does not support CREATE SCHEMA; everything is in a single schema");
    }
    
    @Override
    public String getDropDatabaseStatement(final String catalogName, final String schemaName) {
        throw new UnsupportedOperationException("SQLite does not support DROP SCHEMA; everything is in a single schema");
    }
    
    @Override
    public String getDropTableStatement(final Table table) {
        return "DROP TABLE " + table.toString();
    }
    
    @Override
    public String getAddPrimaryKeyStatement(final PrimaryKey pk, final IdentifierFactory factory) {
        return null;
    }
    
    @Override
    public String getAddCandidateKeyStatement(final CandidateKey ck, final IdentifierFactory factory) {
        return null;
    }
    
    @Override
    public String getAddForeignKeyStatement(final ForeignKey fk, final IdentifierFactory factory) {
        return null;
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
            return "LIMIT 9223372036854775807 OFFSET " + offset + " ";
        }
        return "";
    }
    
    @Override
    public String getAutoIncrementStmt(final Table table, final String columnName) {
        return "SELECT last_insert_rowid()";
    }
    
    @Override
    public String getAutoIncrementKeyword() {
        return "autoincrement";
    }
}
