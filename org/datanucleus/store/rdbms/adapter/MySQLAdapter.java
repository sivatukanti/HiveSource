// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.table.Table;
import java.util.Properties;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.table.TableImpl;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.key.PrimaryKey;
import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.schema.MySQLTypeInfo;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.StoreSchemaHandler;
import java.util.Collection;
import java.sql.DatabaseMetaData;

public class MySQLAdapter extends BaseDatastoreAdapter
{
    public static final String NONSQL92_RESERVED_WORDS = "ANALYZE,AUTO_INCREMENT,BDB,BERKELEYDB,BIGINT,BINARY,BLOB,BTREE,CHANGE,COLUMNS,DATABASE,DATABASES,DAY_HOUR,DAY_MINUTE,DAY_SECOND,DELAYED,DISTINCTROW,DIV,ENCLOSED,ERRORS,ESCAPED,EXPLAIN,FIELDS,FORCE,FULLTEXT,FUNCTION,GEOMETRY,HASH,HELP,HIGH_PRIORITY,HOUR_MINUTE,HOUR_SECOND,IF,IGNORE,INDEX,INFILE,INNODB,KEYS,KILL,LIMIT,LINES,LOAD,LOCALTIME,LOCALTIMESTAMP,LOCK,LONG,LONGBLOB,LONGTEXT,LOW_PRIORITY,MASTER_SERVER_ID,MEDIUMBLOB,MEDIUMINT,MEDIUMTEXT,MIDDLEINT,MINUTE_SECOND,MOD,MRG_MYISAM,OPTIMIZE,OPTIONALLY,OUTFILE,PURGE,REGEXP,RENAME,REPLACE,REQUIRE,RETURNS,RLIKE,RTREE,SHOW,SONAME,SPATIAL,SQL_BIG_RESULT,SQL_CALC_FOUND_ROWS,SQL_SMALL_RESULT,SSL,STARTING,STRAIGHT_JOIN,STRIPED,TABLES,TERMINATED,TINYBLOB,TINYINT,TINYTEXT,TYPES,UNLOCK,UNSIGNED,USE,USER_RESOURCES,VARBINARY,VARCHARACTER,WARNINGS,XOR,YEAR_MONTH,ZEROFILL";
    
    public MySQLAdapter(final DatabaseMetaData metadata) {
        super(metadata);
        this.reservedKeywords.addAll((Collection<?>)this.parseKeywordList("ANALYZE,AUTO_INCREMENT,BDB,BERKELEYDB,BIGINT,BINARY,BLOB,BTREE,CHANGE,COLUMNS,DATABASE,DATABASES,DAY_HOUR,DAY_MINUTE,DAY_SECOND,DELAYED,DISTINCTROW,DIV,ENCLOSED,ERRORS,ESCAPED,EXPLAIN,FIELDS,FORCE,FULLTEXT,FUNCTION,GEOMETRY,HASH,HELP,HIGH_PRIORITY,HOUR_MINUTE,HOUR_SECOND,IF,IGNORE,INDEX,INFILE,INNODB,KEYS,KILL,LIMIT,LINES,LOAD,LOCALTIME,LOCALTIMESTAMP,LOCK,LONG,LONGBLOB,LONGTEXT,LOW_PRIORITY,MASTER_SERVER_ID,MEDIUMBLOB,MEDIUMINT,MEDIUMTEXT,MIDDLEINT,MINUTE_SECOND,MOD,MRG_MYISAM,OPTIMIZE,OPTIONALLY,OUTFILE,PURGE,REGEXP,RENAME,REPLACE,REQUIRE,RETURNS,RLIKE,RTREE,SHOW,SONAME,SPATIAL,SQL_BIG_RESULT,SQL_CALC_FOUND_ROWS,SQL_SMALL_RESULT,SSL,STARTING,STRAIGHT_JOIN,STRIPED,TABLES,TERMINATED,TINYBLOB,TINYINT,TINYTEXT,TYPES,UNLOCK,UNSIGNED,USE,USER_RESOURCES,VARBINARY,VARCHARACTER,WARNINGS,XOR,YEAR_MONTH,ZEROFILL"));
        this.supportedOptions.remove("AlterTableDropConstraint_Syntax");
        if (this.datastoreMajorVersion < 4 || (this.datastoreMajorVersion == 4 && this.datastoreMinorVersion == 0 && this.datastoreRevisionVersion < 13)) {
            this.supportedOptions.remove("AlterTableDropForeignKey_Syntax");
        }
        else {
            this.supportedOptions.add("AlterTableDropForeignKey_Syntax");
        }
        this.supportedOptions.remove("DeferredConstraints");
        this.supportedOptions.remove("ColumnOptions_DefaultBeforeNull");
        this.supportedOptions.add("PrimaryKeyInCreateStatements");
        if (this.datastoreMajorVersion < 5 && (this.datastoreMajorVersion < 4 || this.datastoreMinorVersion < 1)) {
            this.supportedOptions.remove("Exists_Syntax");
        }
        else {
            this.supportedOptions.add("Exists_Syntax");
        }
        if (this.datastoreMajorVersion < 4) {
            this.supportedOptions.remove("Union_Syntax");
        }
        else {
            this.supportedOptions.add("Union_Syntax");
        }
        this.supportedOptions.add("BlobSetUsingSetString");
        this.supportedOptions.add("ClobSetUsingSetString");
        this.supportedOptions.add("CreateIndexesBeforeForeignKeys");
        this.supportedOptions.add("IdentityColumns");
        this.supportedOptions.add("LockWithSelectForUpdate");
        this.supportedOptions.add("StoredProcs");
        this.supportedOptions.remove("DateTimeStoresMillisecs");
    }
    
    @Override
    public void initialiseTypes(final StoreSchemaHandler handler, final ManagedConnection mconn) {
        super.initialiseTypes(handler, mconn);
        SQLTypeInfo sqlType = new MySQLTypeInfo("MEDIUMBLOB", (short)2004, Integer.MAX_VALUE, null, null, null, 1, false, (short)1, false, false, false, "MEDIUMBLOB", (short)0, (short)0, 0);
        this.addSQLTypeForJDBCType(handler, mconn, (short)2004, sqlType, true);
        sqlType = new MySQLTypeInfo("MEDIUMTEXT", (short)2005, Integer.MAX_VALUE, null, null, null, 1, true, (short)1, false, false, false, "MEDIUMTEXT", (short)0, (short)0, 0);
        this.addSQLTypeForJDBCType(handler, mconn, (short)2005, sqlType, true);
    }
    
    @Override
    public String getVendorID() {
        return "mysql";
    }
    
    @Override
    public RDBMSColumnInfo newRDBMSColumnInfo(final ResultSet rs) {
        final RDBMSColumnInfo info = super.newRDBMSColumnInfo(rs);
        final short dataType = info.getDataType();
        final String typeName = info.getTypeName();
        if (dataType == -4 && typeName.equalsIgnoreCase("mediumblob")) {
            info.setDataType((short)2004);
        }
        if (dataType == -1 && typeName.equalsIgnoreCase("mediumtext")) {
            info.setDataType((short)2005);
        }
        return info;
    }
    
    @Override
    public SQLTypeInfo newSQLTypeInfo(final ResultSet rs) {
        final SQLTypeInfo info = new MySQLTypeInfo(rs);
        return info;
    }
    
    @Override
    public String getCreateDatabaseStatement(final String catalogName, final String schemaName) {
        return "CREATE DATABASE IF NOT EXISTS " + catalogName;
    }
    
    @Override
    public String getDropDatabaseStatement(final String catalogName, final String schemaName) {
        return "DROP DATABASE IF EXISTS " + catalogName;
    }
    
    @Override
    public String getAddPrimaryKeyStatement(final PrimaryKey pk, final IdentifierFactory factory) {
        return null;
    }
    
    @Override
    public String getCreateTableStatement(final TableImpl table, final Column[] columns, final Properties props, final IdentifierFactory factory) {
        final StringBuffer createStmt = new StringBuffer(super.getCreateTableStatement(table, columns, props, factory));
        String engineType = "INNODB";
        if (props != null && props.containsKey("mysql-engine-type")) {
            engineType = props.getProperty("mysql-engine-type");
        }
        boolean engineKeywordPresent = false;
        if (this.datastoreMajorVersion >= 5 || (this.datastoreMajorVersion == 4 && this.datastoreMinorVersion >= 1 && this.datastoreRevisionVersion >= 2) || (this.datastoreMajorVersion == 4 && this.datastoreMinorVersion == 0 && this.datastoreRevisionVersion >= 18)) {
            engineKeywordPresent = true;
        }
        if (engineKeywordPresent) {
            createStmt.append(" ENGINE=" + engineType);
        }
        else {
            createStmt.append(" TYPE=" + engineType);
        }
        return createStmt.toString();
    }
    
    @Override
    public String getDropTableStatement(final Table table) {
        return "DROP TABLE " + table.toString();
    }
    
    @Override
    public String getAddColumnStatement(final Table table, final Column col) {
        return "ALTER TABLE " + table.toString() + " ADD COLUMN " + col.getSQLDefinition();
    }
    
    @Override
    public String getDeleteTableStatement(final SQLTable tbl) {
        return "DELETE " + tbl.getAlias() + " FROM " + tbl.toString();
    }
    
    @Override
    public String getAutoIncrementStmt(final Table table, final String columnName) {
        return "SELECT LAST_INSERT_ID()";
    }
    
    @Override
    public String getAutoIncrementKeyword() {
        return "AUTO_INCREMENT";
    }
    
    @Override
    public String getSelectNewUUIDStmt() {
        return "SELECT uuid()";
    }
    
    @Override
    public String getRangeByLimitEndOfStatementClause(final long offset, final long count) {
        if (offset >= 0L && count > 0L) {
            return "LIMIT " + offset + "," + count + " ";
        }
        if (offset <= 0L && count > 0L) {
            return "LIMIT " + count + " ";
        }
        if (offset >= 0L && count < 0L) {
            return "LIMIT " + offset + "," + Long.MAX_VALUE + " ";
        }
        return "";
    }
    
    @Override
    public String getEscapePatternExpression() {
        return "ESCAPE '\\\\'";
    }
}
