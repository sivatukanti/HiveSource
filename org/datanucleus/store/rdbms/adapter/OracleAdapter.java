// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.store.rdbms.sql.expression.CharacterExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.table.Table;
import java.sql.PreparedStatement;
import org.datanucleus.util.NucleusLogger;
import java.sql.Statement;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.SQLException;
import java.sql.Connection;
import org.datanucleus.store.rdbms.mapping.oracle.OracleRDBMSMappingManager;
import org.datanucleus.store.rdbms.mapping.MappingManager;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.sql.ResultSet;
import java.util.Iterator;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.schema.OracleTypeInfo;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.StoreSchemaHandler;
import java.util.Collection;
import java.sql.DatabaseMetaData;

public class OracleAdapter extends BaseDatastoreAdapter
{
    public static final String OJDBC_DRIVER_NAME = "Oracle JDBC driver";
    public static final String ORACLE_8_RESERVED_WORDS = "ACCESS,AUDIT,CLUSTER,COMMENT,COMPRESS,EXCLUSIVE,FILE,IDENTIFIED,INCREMENT,INDEX,INITIAL,LOCK,LONG,MAXEXTENTS,MINUS,MLSLABEL,MODE,MODIFY,NOAUDIT,NOCOMPRESS,NOWAIT,NUMBER,OFFLINE,ONLINE,PCTFREE,RAW,RENAME,RESOURCE,ROWID,ROWNUM,SHARE,SUCCESSFUL,SYNONYM,SYSDATE,UID,VALIDATE,VARCHAR2,VALIDATE,VARCHAR2";
    public static final String ORACLE_9_RESERVED_WORDS = "ACCESS,CHAR,DEFAULT,ADD,CHECK,DELETE,ALL,CLUSTER,DESC,ALTER,COLUMN,DISTINCT,AND,COMMENT,DROP,ANY,COMPRESS,ELSE,AS,CONNECT,EXCLUSIVE,ASC,CREATE,EXISTS,AUDIT,CURRENT,FILE,BETWEEN,DATE,FLOAT,BY,DECIMAL,FOR,FROM,NOT,SHARE,GRANT,NOWAIT,SIZE,GROUP,NULL,SMALLINT,HAVING,NUMBER,START,IDENTIFIED,OF,SUCCESSFUL,IMMEDIATE,OFFLINE,SYNONYM,IN,ON,SYSDATE,INCREMENT,ONLINE,TABLE,INDEX,OPTION,THEN,INITIAL,OR,TO,INSERT,ORDER,TRIGGER,INTEGER,PCTFREE,UID,INTERSECT,PRIOR,UNION,INTO,PRIVILEGES,UNIQUE,IS,PUBLIC,UPDATE,LEVEL,RAW,USER,LIKE,RENAME,VALIDATE,LOCK,RESOURCE,VALUES,LONG,REVOKE,VARCHAR,MAXEXTENTS,ROW,VARCHAR2,MINUS,ROWID,VIEW,MLSLABEL,ROWNUM,WHENEVER,MODE,ROWS,WHERE,MODIFY,SELECT,WITH,NOAUDIT,SESSION,NOCOMPRESS,SET";
    public static final String ORACLE_10_RESERVED_WORDS = "ACCESS,ADD,ALL,ALTER,AND,ANY,AS,ASC,AUDIT,BETWEEN,BY,CHAR,CHECK,CLUSTER,COLUMN,COMMENT,COMPRESS,CONNECT,CREATE,CURRENT,DATE,DECIMAL,DEFAULT,DELETE,DESC,DISTINCT,DROP,ELSE,EXCLUSIVE,EXISTS,FILE,FLOAT,FOR,FROM,GRANT,GROUP,HAVING,IDENTIFIED,IMMEDIATE,IN,INCREMENT,INDEX,INITIAL,INSERT,INTEGER,INTERSECT,INTO,IS,LEVEL,LIKE,LOCK,LONG,MAXEXTENTS,MINUS,MLSLABEL,MODE,MODIFY,NOAUDIT,NOCOMPRESS,NOT,NOWAIT,NULL,NUMBER,OF,OFFLINE,ON,ONLINE,OPTION,OR,ORDER,PCTFREE,PRIOR,PRIVILEGES,PUBLIC,RAW,RENAME,RESOURCE,REVOKE,ROW,ROWID,ROWNUM,ROWS,SELECT,SESSION,SET,SHARE,SIZE,SMALLINT,START,SUCCESSFUL,SYNONYM,SYSDATE,TABLE,THEN,TO,TRIGGER,UID,UNION,UNIQUE,UPDATE,USER,VALIDATE,VALUES,VARCHAR,VARCHAR2,VIEW,WHENEVER,WHERE,WITH";
    
    public OracleAdapter(final DatabaseMetaData metadata) {
        super(metadata);
        if (this.datastoreMajorVersion <= 8) {
            this.reservedKeywords.addAll((Collection<?>)this.parseKeywordList("ACCESS,AUDIT,CLUSTER,COMMENT,COMPRESS,EXCLUSIVE,FILE,IDENTIFIED,INCREMENT,INDEX,INITIAL,LOCK,LONG,MAXEXTENTS,MINUS,MLSLABEL,MODE,MODIFY,NOAUDIT,NOCOMPRESS,NOWAIT,NUMBER,OFFLINE,ONLINE,PCTFREE,RAW,RENAME,RESOURCE,ROWID,ROWNUM,SHARE,SUCCESSFUL,SYNONYM,SYSDATE,UID,VALIDATE,VARCHAR2,VALIDATE,VARCHAR2"));
        }
        else if (this.datastoreMajorVersion == 9) {
            this.reservedKeywords.addAll((Collection<?>)this.parseKeywordList("ACCESS,CHAR,DEFAULT,ADD,CHECK,DELETE,ALL,CLUSTER,DESC,ALTER,COLUMN,DISTINCT,AND,COMMENT,DROP,ANY,COMPRESS,ELSE,AS,CONNECT,EXCLUSIVE,ASC,CREATE,EXISTS,AUDIT,CURRENT,FILE,BETWEEN,DATE,FLOAT,BY,DECIMAL,FOR,FROM,NOT,SHARE,GRANT,NOWAIT,SIZE,GROUP,NULL,SMALLINT,HAVING,NUMBER,START,IDENTIFIED,OF,SUCCESSFUL,IMMEDIATE,OFFLINE,SYNONYM,IN,ON,SYSDATE,INCREMENT,ONLINE,TABLE,INDEX,OPTION,THEN,INITIAL,OR,TO,INSERT,ORDER,TRIGGER,INTEGER,PCTFREE,UID,INTERSECT,PRIOR,UNION,INTO,PRIVILEGES,UNIQUE,IS,PUBLIC,UPDATE,LEVEL,RAW,USER,LIKE,RENAME,VALIDATE,LOCK,RESOURCE,VALUES,LONG,REVOKE,VARCHAR,MAXEXTENTS,ROW,VARCHAR2,MINUS,ROWID,VIEW,MLSLABEL,ROWNUM,WHENEVER,MODE,ROWS,WHERE,MODIFY,SELECT,WITH,NOAUDIT,SESSION,NOCOMPRESS,SET"));
        }
        else if (this.datastoreMajorVersion >= 10) {
            this.reservedKeywords.addAll((Collection<?>)this.parseKeywordList("ACCESS,ADD,ALL,ALTER,AND,ANY,AS,ASC,AUDIT,BETWEEN,BY,CHAR,CHECK,CLUSTER,COLUMN,COMMENT,COMPRESS,CONNECT,CREATE,CURRENT,DATE,DECIMAL,DEFAULT,DELETE,DESC,DISTINCT,DROP,ELSE,EXCLUSIVE,EXISTS,FILE,FLOAT,FOR,FROM,GRANT,GROUP,HAVING,IDENTIFIED,IMMEDIATE,IN,INCREMENT,INDEX,INITIAL,INSERT,INTEGER,INTERSECT,INTO,IS,LEVEL,LIKE,LOCK,LONG,MAXEXTENTS,MINUS,MLSLABEL,MODE,MODIFY,NOAUDIT,NOCOMPRESS,NOT,NOWAIT,NULL,NUMBER,OF,OFFLINE,ON,ONLINE,OPTION,OR,ORDER,PCTFREE,PRIOR,PRIVILEGES,PUBLIC,RAW,RENAME,RESOURCE,REVOKE,ROW,ROWID,ROWNUM,ROWS,SELECT,SESSION,SET,SHARE,SIZE,SMALLINT,START,SUCCESSFUL,SYNONYM,SYSDATE,TABLE,THEN,TO,TRIGGER,UID,UNION,UNIQUE,UPDATE,USER,VALIDATE,VALUES,VARCHAR,VARCHAR2,VIEW,WHENEVER,WHERE,WITH"));
        }
        this.supportedOptions.add("LockWithSelectForUpdate");
        this.supportedOptions.add("Sequences");
        this.supportedOptions.add("NullEqualsEmptyString");
        this.supportedOptions.add("AnalysisMethods");
        this.supportedOptions.add("StoredProcs");
        this.supportedOptions.add("OrderByWithNullsDirectives");
        this.supportedOptions.remove("BooleanExpression");
        if (this.datastoreMajorVersion < 9) {
            this.supportedOptions.remove("ANSI_Join_Syntax");
        }
        else {
            this.supportedOptions.add("ANSI_Join_Syntax");
        }
        this.supportedOptions.remove("FkDeleteActionDefault");
        this.supportedOptions.remove("FkDeleteActionRestrict");
        this.supportedOptions.remove("FkUpdateActionDefault");
        this.supportedOptions.remove("FkUpdateActionRestrict");
        this.supportedOptions.remove("FkUpdateActionNull");
        this.supportedOptions.remove("FkUpdateActionCascade");
    }
    
    @Override
    public void initialiseTypes(final StoreSchemaHandler handler, final ManagedConnection mconn) {
        super.initialiseTypes(handler, mconn);
        SQLTypeInfo sqlType = new OracleTypeInfo("CLOB", (short)2005, 1073741823, "'", "'", null, 1, true, (short)0, false, false, false, "CLOB", (short)0, (short)0, 10);
        sqlType.setAllowsPrecisionSpec(false);
        this.addSQLTypeForJDBCType(handler, mconn, (short)2005, sqlType, true);
        sqlType = new OracleTypeInfo("DATE", (short)91, 7, null, null, null, 1, false, (short)3, false, false, false, "DATE", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)91, sqlType, true);
        sqlType = new OracleTypeInfo("DECIMAL", (short)3, 38, null, null, null, 1, false, (short)3, false, true, false, "NUMBER", (short)(-84), (short)127, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)3, sqlType, true);
        sqlType = new OracleTypeInfo("DOUBLE PRECISION", (short)8, 38, null, null, null, 1, false, (short)3, false, true, false, "NUMBER", (short)(-84), (short)127, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)8, sqlType, true);
        sqlType = new OracleTypeInfo("SDO_GEOMETRY", (short)2002, 0, null, null, null, 1, false, (short)0, false, false, false, "SDO_GEOMETRY", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)(-123), sqlType, true);
        sqlType = new OracleTypeInfo("SYS.XMLTYPE", (short)2007, 1073741823, "'", "'", null, 1, true, (short)0, false, false, false, "SYS.XMLTYPE", (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)2007, sqlType, true);
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
    }
    
    @Override
    public SQLTypeInfo newSQLTypeInfo(final ResultSet rs) {
        return new OracleTypeInfo(rs);
    }
    
    @Override
    public MappingManager getMappingManager(final RDBMSStoreManager storeMgr) {
        return new OracleRDBMSMappingManager(storeMgr);
    }
    
    @Override
    public String getVendorID() {
        return "oracle";
    }
    
    @Override
    public String getSurrogateForEmptyStrings() {
        return "\u0001";
    }
    
    @Override
    public String getCatalogName(final Connection conn) throws SQLException {
        return null;
    }
    
    @Override
    public String getSchemaName(final Connection conn) throws SQLException {
        final Statement stmt = conn.createStatement();
        try {
            final String stmtText = "SELECT SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA') FROM DUAL";
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
    
    @Override
    public String getCreateDatabaseStatement(final String catalogName, final String schemaName) {
        throw new UnsupportedOperationException("Oracle does not support CREATE SCHEMA; you need to create a USER (manually)");
    }
    
    @Override
    public String getDropDatabaseStatement(final String catalogName, final String schemaName) {
        throw new UnsupportedOperationException("Oracle does not support DROP SCHEMA; you need to delete a USER (manually)");
    }
    
    @Override
    public ResultSet getExistingIndexes(final Connection conn, final String catalog, final String schema, final String table) throws SQLException {
        if (this.isReservedKeyword(table) || !table.matches("[a-zA-Z]{1}\\w*(\\$|\\#)*\\w*")) {
            final String GET_INDEXES_STMT = "SELECT null as table_cat, owner as table_schem, table_name, 0 as NON_UNIQUE, null as index_qualifier, null as index_name, 0 as type, 0 as ordinal_position, null as column_name, null as asc_or_desc, num_rows as cardinality, blocks as pages, null as filter_condition FROM all_tables WHERE table_name = ? AND owner = ? UNION SELECT null as table_cat, i.owner as table_schem, i.table_name, decode (i.uniqueness, 'UNIQUE', 0, 1), null as index_qualifier, i.index_name, 1 as type, c.column_position as ordinal_position, c.column_name, null as asc_or_desc, i.distinct_keys as cardinality, i.leaf_blocks as pages, null as filter_condition FROM all_indexes i, all_ind_columns c WHERE i.table_name = ? AND i.owner = ? AND i.index_name = c.index_name AND i.table_owner = c.table_owner AND i.table_name = c.table_name AND i.owner = c.index_owner ORDER BY non_unique, type, index_name, ordinal_position";
            NucleusLogger.DATASTORE_SCHEMA.debug("Retrieving Oracle index info using the following SQL : " + GET_INDEXES_STMT);
            final PreparedStatement stmt = conn.prepareStatement(GET_INDEXES_STMT);
            stmt.setString(1, table);
            stmt.setString(2, schema);
            stmt.setString(3, table);
            stmt.setString(4, schema);
            return stmt.executeQuery();
        }
        return super.getExistingIndexes(conn, catalog, schema, table);
    }
    
    @Override
    public String getDropTableStatement(final Table table) {
        if (this.datastoreMajorVersion >= 10) {
            return "DROP TABLE " + table.toString() + " CASCADE CONSTRAINTS PURGE";
        }
        return "DROP TABLE " + table.toString() + " CASCADE CONSTRAINTS";
    }
    
    @Override
    public String getSequenceCreateStmt(final String sequence_name, final Integer min, final Integer max, final Integer start, final Integer increment, final Integer cache_size) {
        if (sequence_name == null) {
            throw new NucleusUserException(OracleAdapter.LOCALISER.msg("051028"));
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
            stmt.append(" NOCACHE");
        }
        return stmt.toString();
    }
    
    @Override
    public String getSequenceNextStmt(final String sequence_name) {
        if (sequence_name == null) {
            throw new NucleusUserException(OracleAdapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("SELECT ");
        stmt.append(sequence_name);
        stmt.append(".NEXTVAL from dual ");
        return stmt.toString();
    }
    
    @Override
    public RDBMSColumnInfo newRDBMSColumnInfo(final ResultSet rs) {
        final RDBMSColumnInfo info = new RDBMSColumnInfo(rs);
        final String typeName = info.getTypeName();
        int dataType = -1;
        if (typeName == null) {
            dataType = 0;
        }
        else if (typeName.equals("ROWID")) {
            dataType = 4;
        }
        else if (typeName.equals("NUMBER") || typeName.equals("VARNUM")) {
            dataType = 2;
        }
        else if (typeName.equals("VARCHAR2")) {
            dataType = 12;
        }
        else if (typeName.equals("CHAR")) {
            dataType = 1;
        }
        else if (typeName.equals("DATE")) {
            dataType = 91;
        }
        else if (typeName.equals("CLOB") || typeName.equals("NCLOB")) {
            dataType = 2005;
        }
        else if (typeName.equals("BLOB")) {
            dataType = 2004;
        }
        else if (typeName.equals("LONG")) {
            dataType = -1;
        }
        else if (typeName.equals("LONG RAW")) {
            dataType = -4;
        }
        else if (typeName.equals("RAW")) {
            dataType = -3;
        }
        else if (typeName.startsWith("TIMESTAMP")) {
            dataType = 93;
        }
        else if (typeName.equals("FLOAT")) {
            dataType = 6;
        }
        else {
            NucleusLogger.DATASTORE.warn(OracleAdapter.LOCALISER.msg("020191", typeName));
            dataType = 1111;
        }
        info.setDataType((short)dataType);
        return info;
    }
    
    @Override
    public int getTransactionIsolationForSchemaCreation() {
        return 2;
    }
    
    @Override
    public ResultSet getColumns(final Connection conn, final String catalog, final String schema, final String table, final String columnNamePattern) throws SQLException {
        StringBuffer columnsQuery = new StringBuffer();
        columnsQuery.append("SELECT NULL TABLE_CAT, OWNER TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, NULL DATA_TYPE, ");
        columnsQuery.append("DATA_TYPE TYPE_NAME, DECODE(DATA_TYPE,'NUMBER',DATA_PRECISION,DATA_LENGTH) COLUMN_SIZE, ");
        columnsQuery.append("0 BUFFER_LENGTH, DATA_SCALE DECIMAL_DIGITS, 10 NUM_PREC_RADIX, ");
        columnsQuery.append("DECODE(NULLABLE,'Y',1,0) NULLABLE, NULL REMARKS, NULL COLUMN_DEF, 0 SQL_DATA_TYPE, 0 SQL_DATETIME_SUB, ");
        columnsQuery.append("DATA_LENGTH CHAR_OCTET_LENGTH, COLUMN_ID ORDINAL_POSITION, DECODE(NULLABLE,'Y','YES','NO') IS_NULLABLE ");
        columnsQuery.append("FROM ALL_TAB_COLUMNS ");
        boolean outputWhere = false;
        if (schema != null && schema.length() > 0) {
            columnsQuery.append("WHERE OWNER LIKE '").append(schema).append("' ");
            outputWhere = true;
        }
        if (table != null) {
            if (!outputWhere) {
                columnsQuery.append("WHERE ");
                outputWhere = true;
            }
            else {
                columnsQuery.append("AND ");
            }
            if (table.length() > 0) {
                columnsQuery.append("TABLE_NAME LIKE '").append(table).append("' ");
            }
            else {
                columnsQuery.append("TABLE_NAME IS NULL ");
            }
        }
        columnsQuery.append("ORDER BY TABLE_SCHEM, TABLE_NAME, ORDINAL_POSITION ");
        NucleusLogger.DATASTORE_SCHEMA.debug("Retrieving Oracle column info using the following SQL : " + (Object)columnsQuery);
        final PreparedStatement columnsStmt = conn.prepareStatement(columnsQuery.toString());
        final ResultSet columnsResult = columnsStmt.executeQuery();
        columnsQuery = null;
        return columnsResult;
    }
    
    @Override
    public String getDatastoreDateStatement() {
        return "SELECT CURRENT_TIMESTAMP FROM DUAL";
    }
    
    @Override
    public String getOrderString(final StoreManager storeMgr, final String orderString, final SQLExpression sqlExpr) {
        String nlsSortOrder = "LATIN";
        final String sortOrder = storeMgr.getStringProperty("datanucleus.rdbms.oracleNlsSortOrder");
        if (sortOrder != null) {
            nlsSortOrder = sortOrder.toUpperCase();
        }
        if (sqlExpr instanceof CharacterExpression && !nlsSortOrder.equals("BINARY")) {
            return "NLSSORT(" + orderString + ", 'NLS_SORT = " + nlsSortOrder + "')";
        }
        if (this.datastoreMajorVersion < 9 && sqlExpr instanceof BooleanExpression && !sqlExpr.getJavaTypeMapping().getDatastoreMapping(0).isStringBased()) {
            throw new NucleusException(OracleAdapter.LOCALISER.msg("052505")).setFatal();
        }
        return orderString;
    }
    
    @Override
    public boolean isStatementTimeout(final SQLException sqle) {
        return (sqle.getSQLState() != null && sqle.getSQLState().equalsIgnoreCase("72000") && sqle.getErrorCode() == 1013) || super.isStatementTimeout(sqle);
    }
    
    @Override
    public boolean validToSelectMappingInStatement(final SQLStatement stmt, final JavaTypeMapping m) {
        if (m.getNumberOfDatastoreMappings() <= 0) {
            return true;
        }
        for (int i = 0; i < m.getNumberOfDatastoreMappings(); ++i) {
            final Column col = m.getDatastoreMapping(i).getColumn();
            if ((col.getJdbcType() == 2005 || col.getJdbcType() == 2004) && stmt.isDistinct()) {
                NucleusLogger.QUERY.debug("Not selecting " + m + " since is for BLOB/CLOB and using DISTINCT");
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String getRangeByRowNumberColumn2() {
        return "ROWNUM";
    }
}
