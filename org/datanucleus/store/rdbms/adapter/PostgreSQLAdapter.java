// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.key.Index;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.key.PrimaryKey;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.schema.ForeignKeyInfo;
import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import org.datanucleus.store.rdbms.JDBCUtils;
import org.datanucleus.util.NucleusLogger;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.schema.PostgresqlTypeInfo;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.StoreSchemaHandler;
import java.util.Collection;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.DatabaseMetaData;
import java.util.Hashtable;

public class PostgreSQLAdapter extends BaseDatastoreAdapter
{
    public static final String POSTGRESQL_RESERVED_WORDS = "ALL,ANALYSE,ANALYZE,DO,FREEZE,ILIKE,ISNULL,OFFSET,PLACING,VERBOSE";
    protected Hashtable psqlTypes;
    
    public PostgreSQLAdapter(final DatabaseMetaData metadata) {
        super(metadata);
        if (this.datastoreMajorVersion < 7) {
            throw new NucleusDataStoreException("PostgreSQL version is " + this.datastoreMajorVersion + '.' + this.datastoreMinorVersion + ", 7.0 or later required");
        }
        if (this.datastoreMajorVersion == 7 && this.datastoreMinorVersion <= 2) {
            --this.maxTableNameLength;
            --this.maxConstraintNameLength;
            --this.maxIndexNameLength;
        }
        this.reservedKeywords.addAll((Collection<?>)this.parseKeywordList("ALL,ANALYSE,ANALYZE,DO,FREEZE,ILIKE,ISNULL,OFFSET,PLACING,VERBOSE"));
        this.supportedOptions.add("LockWithSelectForUpdate");
        this.supportedOptions.add("PrimaryKeyInCreateStatements");
        this.supportedOptions.add("Sequences");
        this.supportedOptions.add("IdentityColumns");
        this.supportedOptions.add("OrderByWithNullsDirectives");
        this.supportedOptions.remove("AutoIncrementColumnTypeSpecification");
        this.supportedOptions.remove("AutoIncrementNullSpecification");
        this.supportedOptions.remove("DistinctWithSelectForUpdate");
        this.supportedOptions.remove("PersistOfUnassignedChar");
        if (this.datastoreMajorVersion < 7 || (this.datastoreMajorVersion == 7 && this.datastoreMinorVersion < 2)) {
            this.supportedOptions.remove("AlterTableDropConstraint_Syntax");
        }
        else {
            this.supportedOptions.add("AlterTableDropConstraint_Syntax");
        }
        this.supportedOptions.add("BitIsReallyBoolean");
        this.supportedOptions.add("CharColumnsPaddedWithSpaces");
        this.supportedOptions.add("StoredProcs");
        this.supportedOptions.remove("TxIsolationNone");
        this.supportedOptions.remove("TableAliasInUpdateSet");
    }
    
    @Override
    public void initialiseTypes(final StoreSchemaHandler handler, final ManagedConnection mconn) {
        super.initialiseTypes(handler, mconn);
        SQLTypeInfo sqlType = new PostgresqlTypeInfo("char", (short)1, 65000, null, null, null, 0, false, (short)3, false, false, false, "char", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)1, sqlType, true);
        sqlType = new PostgresqlTypeInfo("text", (short)2005, 9, null, null, null, 0, false, (short)3, false, false, false, null, (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)2005, sqlType, true);
        sqlType = new PostgresqlTypeInfo("BYTEA", (short)2004, 9, null, null, null, 0, false, (short)3, false, false, false, null, (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)2004, sqlType, true);
    }
    
    @Override
    public String getVendorID() {
        return "postgresql";
    }
    
    @Override
    public SQLTypeInfo newSQLTypeInfo(final ResultSet rs) {
        final SQLTypeInfo info = new PostgresqlTypeInfo(rs);
        if (this.psqlTypes == null) {
            (this.psqlTypes = new Hashtable()).put("-7", "bool");
            this.psqlTypes.put("93", "timestamptz");
            this.psqlTypes.put("-5", "int8");
            this.psqlTypes.put("1", "char");
            this.psqlTypes.put("91", "date");
            this.psqlTypes.put("8", "float8");
            this.psqlTypes.put("4", "int4");
            this.psqlTypes.put("-1", "text");
            this.psqlTypes.put("2005", "text");
            this.psqlTypes.put("2004", "bytea");
            this.psqlTypes.put("2", "numeric");
            this.psqlTypes.put("7", "float4");
            this.psqlTypes.put("5", "int2");
            this.psqlTypes.put("92", "time");
            this.psqlTypes.put("12", "varchar");
            this.psqlTypes.put("1111", "***TOTALRUBBISH***");
        }
        final Object obj = this.psqlTypes.get("" + info.getDataType());
        if (obj != null) {
            final String psql_type_name = (String)obj;
            if (!info.getTypeName().equalsIgnoreCase(psql_type_name)) {
                NucleusLogger.DATASTORE.debug(PostgreSQLAdapter.LOCALISER.msg("051007", info.getTypeName(), JDBCUtils.getNameForJDBCType(info.getDataType())));
                return null;
            }
        }
        return info;
    }
    
    @Override
    public RDBMSColumnInfo newRDBMSColumnInfo(final ResultSet rs) {
        final RDBMSColumnInfo info = new RDBMSColumnInfo(rs);
        final String typeName = info.getTypeName();
        if (typeName.equalsIgnoreCase("text")) {
            info.setDataType((short)(-1));
        }
        else if (typeName.equalsIgnoreCase("bytea")) {
            info.setDataType((short)(-4));
        }
        final int columnSize = info.getColumnSize();
        if (columnSize > 65000) {
            info.setColumnSize(-1);
        }
        final int decimalDigits = info.getDecimalDigits();
        if (decimalDigits > 65000) {
            info.setDecimalDigits(-1);
        }
        return info;
    }
    
    @Override
    public ForeignKeyInfo newFKInfo(final ResultSet rs) {
        final ForeignKeyInfo info = super.newFKInfo(rs);
        final String fkName = (String)info.getProperty("fk_name");
        final int firstBackslashIdx = fkName.indexOf(92);
        if (firstBackslashIdx > 0) {
            info.addProperty("fk_name", fkName.substring(0, firstBackslashIdx));
        }
        return info;
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
    public String getInsertStatementForNoColumns(final Table table) {
        return "INSERT INTO " + table.toString() + " VALUES (DEFAULT)";
    }
    
    @Override
    public String getAddPrimaryKeyStatement(final PrimaryKey pk, final IdentifierFactory factory) {
        return null;
    }
    
    @Override
    public String getDropTableStatement(final Table table) {
        if (this.datastoreMajorVersion < 7 || (this.datastoreMajorVersion == 7 && this.datastoreMinorVersion < 3)) {
            return "DROP TABLE " + table.toString();
        }
        return "DROP TABLE " + table.toString() + " CASCADE";
    }
    
    @Override
    public String getCreateIndexStatement(final Index idx, final IdentifierFactory factory) {
        final String idxIdentifier = factory.getIdentifierInAdapterCase(idx.getName());
        return "CREATE " + (idx.getUnique() ? "UNIQUE " : "") + "INDEX " + idxIdentifier + " ON " + idx.getTable().toString() + ' ' + idx + ((idx.getExtendedIndexSettings() == null) ? "" : (" " + idx.getExtendedIndexSettings()));
    }
    
    @Override
    public String getAutoIncrementStmt(final Table table, final String columnName) {
        final StringBuffer stmt = new StringBuffer("SELECT currval('");
        if (table.getSchemaName() != null) {
            stmt.append(table.getSchemaName().replace(this.getIdentifierQuoteString(), ""));
            stmt.append(this.getCatalogSeparator());
        }
        final String tableName = table.getIdentifier().toString();
        final boolean quoted = tableName.startsWith(this.getIdentifierQuoteString());
        if (quoted) {
            stmt.append(this.getIdentifierQuoteString());
        }
        stmt.append(tableName.replace(this.getIdentifierQuoteString(), ""));
        stmt.append("_");
        stmt.append(columnName.replace(this.getIdentifierQuoteString(), ""));
        stmt.append("_seq");
        if (quoted) {
            stmt.append(this.getIdentifierQuoteString());
        }
        stmt.append("')");
        return stmt.toString();
    }
    
    @Override
    public String getAutoIncrementKeyword() {
        return "SERIAL";
    }
    
    @Override
    public String getSequenceCreateStmt(final String sequence_name, final Integer min, final Integer max, final Integer start, final Integer increment, final Integer cache_size) {
        if (sequence_name == null) {
            throw new NucleusUserException(PostgreSQLAdapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("CREATE SEQUENCE ");
        stmt.append(sequence_name);
        if (min != null) {
            stmt.append(" MINVALUE " + min);
        }
        if (max != null) {
            stmt.append(" MAXVALUE " + max);
        }
        if (start != null) {
            stmt.append(" START WITH " + start);
        }
        if (increment != null) {
            stmt.append(" INCREMENT BY " + increment);
        }
        if (cache_size != null) {
            stmt.append(" CACHE " + cache_size);
        }
        else {
            stmt.append(" CACHE 1");
        }
        return stmt.toString();
    }
    
    @Override
    public String getSequenceNextStmt(final String sequence_name) {
        if (sequence_name == null) {
            throw new NucleusUserException(PostgreSQLAdapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("SELECT nextval('");
        stmt.append(sequence_name);
        stmt.append("')");
        return stmt.toString();
    }
    
    @Override
    public boolean supportsQueryFetchSize(final int size) {
        return this.driverMajorVersion > 7;
    }
    
    @Override
    public String getRangeByLimitEndOfStatementClause(final long offset, final long count) {
        String str = "";
        if (count > 0L) {
            str = str + "LIMIT " + count + " ";
        }
        if (offset >= 0L) {
            str = str + "OFFSET " + offset + " ";
        }
        return str;
    }
    
    @Override
    public String getEscapePatternExpression() {
        if (this.datastoreMajorVersion > 8 || (this.datastoreMajorVersion == 8 && this.datastoreMinorVersion >= 3)) {
            return "ESCAPE E'\\\\'";
        }
        return "ESCAPE '\\\\'";
    }
    
    @Override
    public boolean isStatementCancel(final SQLException sqle) {
        return sqle.getErrorCode() == 57014;
    }
}
