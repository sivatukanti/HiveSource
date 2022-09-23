// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.Connection;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.schema.DB2TypeInfo;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.StoreSchemaHandler;
import java.util.Collection;
import java.sql.DatabaseMetaData;

public class DB2Adapter extends BaseDatastoreAdapter
{
    public static final String DB2_RESERVED_WORDS = "ACCESS,ALIAS,ALLOW,ASUTIME,AUDIT,AUX,AUXILIARY,BUFFERPOOL,CAPTURE,CCSID,CLUSTER,COLLECTION,COLLID,COMMENT,CONCAT,CONTAINS,COUNT_BIG,CURRENT_LC_PATH,CURRENT_SERVER,CURRENT_TIMEZONE,DATABASE,DAYS,DB2GENERAL,DB2SQL,DBA,DBINFO,DBSPACE,DISALLOW,DSSIZE,EDITPROC,ERASE,EXCLUSIVE,EXPLAIN,FENCED,FIELDPROC,FILE,FINAL,GENERATED,GRAPHIC,HOURS,IDENTIFIED,INDEX,INTEGRITY,ISOBID,JAVA,LABEL,LC_CTYPE,LINKTYPE,LOCALE,LOCATORS,LOCK,LOCKSIZE,LONG,MICROSECOND,MICROSECONDS,MINUTES,MODE,MONTHS,NAME,NAMED,NHEADER,NODENAME,NODENUMBER,NULLS,NUMPARTS,OBID,OPTIMIZATION,OPTIMIZE,PACKAGE,PAGE,PAGES,PART,PCTFREE,PCTINDEX,PIECESIZE,PLAN,PRIQTY,PRIVATE,PROGRAM,PSID,QYERYNO,RECOVERY,RENAME,RESET,RESOURCE,RRN,RUN,SCHEDULE,SCRATCHPAD,SECONDS,SECQTY,SECURITY,SHARE,SIMPLE,SOURCE,STANDARD,STATISTICS,STAY,STOGROUP,STORES,STORPOOL,STYLE,SUBPAGES,SYNONYM,TABLESPACE,TYPE,VALIDPROC,VARIABLE,VARIANT,VCAT,VOLUMES,WLM,YEARS";
    
    public DB2Adapter(final DatabaseMetaData metadata) {
        super(metadata);
        this.reservedKeywords.addAll((Collection<?>)this.parseKeywordList("ACCESS,ALIAS,ALLOW,ASUTIME,AUDIT,AUX,AUXILIARY,BUFFERPOOL,CAPTURE,CCSID,CLUSTER,COLLECTION,COLLID,COMMENT,CONCAT,CONTAINS,COUNT_BIG,CURRENT_LC_PATH,CURRENT_SERVER,CURRENT_TIMEZONE,DATABASE,DAYS,DB2GENERAL,DB2SQL,DBA,DBINFO,DBSPACE,DISALLOW,DSSIZE,EDITPROC,ERASE,EXCLUSIVE,EXPLAIN,FENCED,FIELDPROC,FILE,FINAL,GENERATED,GRAPHIC,HOURS,IDENTIFIED,INDEX,INTEGRITY,ISOBID,JAVA,LABEL,LC_CTYPE,LINKTYPE,LOCALE,LOCATORS,LOCK,LOCKSIZE,LONG,MICROSECOND,MICROSECONDS,MINUTES,MODE,MONTHS,NAME,NAMED,NHEADER,NODENAME,NODENUMBER,NULLS,NUMPARTS,OBID,OPTIMIZATION,OPTIMIZE,PACKAGE,PAGE,PAGES,PART,PCTFREE,PCTINDEX,PIECESIZE,PLAN,PRIQTY,PRIVATE,PROGRAM,PSID,QYERYNO,RECOVERY,RENAME,RESET,RESOURCE,RRN,RUN,SCHEDULE,SCRATCHPAD,SECONDS,SECQTY,SECURITY,SHARE,SIMPLE,SOURCE,STANDARD,STATISTICS,STAY,STOGROUP,STORES,STORPOOL,STYLE,SUBPAGES,SYNONYM,TABLESPACE,TYPE,VALIDPROC,VARIABLE,VARIANT,VCAT,VOLUMES,WLM,YEARS"));
        this.supportedOptions.add("LockWithSelectForUpdate");
        this.supportedOptions.add("IdentityColumns");
        this.supportedOptions.add("Sequences");
        this.supportedOptions.add("AnalysisMethods");
        this.supportedOptions.add("StoredProcs");
        this.supportedOptions.add("UseUnionAll");
        this.supportedOptions.add("OrderByWithNullsDirectives");
        this.supportedOptions.remove("BooleanExpression");
        this.supportedOptions.remove("DeferredConstraints");
        this.supportedOptions.remove("NullsInCandidateKeys");
        this.supportedOptions.remove("ColumnOptions_NullsKeyword");
        this.supportedOptions.remove("FkDeleteActionDefault");
        this.supportedOptions.remove("FkUpdateActionDefault");
        this.supportedOptions.remove("FkUpdateActionCascade");
        this.supportedOptions.remove("FkUpdateActionNull");
    }
    
    @Override
    public void initialiseTypes(final StoreSchemaHandler handler, final ManagedConnection mconn) {
        super.initialiseTypes(handler, mconn);
        SQLTypeInfo sqlType = new DB2TypeInfo("FLOAT", (short)6, 53, null, null, null, 1, false, (short)2, false, false, false, null, (short)0, (short)0, 0);
        this.addSQLTypeForJDBCType(handler, mconn, (short)6, sqlType, true);
        sqlType = new DB2TypeInfo("NUMERIC", (short)2, 31, null, null, "PRECISION,SCALE", 1, false, (short)2, false, false, false, null, (short)0, (short)31, 0);
        this.addSQLTypeForJDBCType(handler, mconn, (short)2, sqlType, true);
        sqlType = new DB2TypeInfo("BIGINT", (short)(-5), 20, null, null, null, 1, false, (short)2, false, true, false, null, (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)(-5), sqlType, true);
        sqlType = new DB2TypeInfo("XML", (short)2009, Integer.MAX_VALUE, null, null, null, 1, false, (short)2, false, false, false, null, (short)0, (short)0, 0);
        this.addSQLTypeForJDBCType(handler, mconn, (short)2009, sqlType, true);
        sqlType = new DB2TypeInfo("SMALLINT", (short)5, 5, null, null, null, 1, false, (short)2, false, true, false, null, (short)0, (short)0, 10);
        this.addSQLTypeForJDBCType(handler, mconn, (short)(-7), sqlType, true);
    }
    
    @Override
    public String getVendorID() {
        return "db2";
    }
    
    @Override
    public String getSchemaName(final Connection conn) throws SQLException {
        final Statement stmt = conn.createStatement();
        try {
            final String stmtText = "VALUES (CURRENT SCHEMA)";
            final ResultSet rs = stmt.executeQuery(stmtText);
            try {
                if (!rs.next()) {
                    throw new NucleusDataStoreException("No result returned from " + stmtText).setFatal();
                }
                return rs.getString(1).trim();
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
    public int getDatastoreIdentifierMaxLength(final IdentifierType identifierType) {
        if (identifierType == IdentifierType.CANDIDATE_KEY) {
            return 18;
        }
        if (identifierType == IdentifierType.FOREIGN_KEY) {
            return 18;
        }
        if (identifierType == IdentifierType.INDEX) {
            return 18;
        }
        if (identifierType == IdentifierType.PRIMARY_KEY) {
            return 18;
        }
        return super.getDatastoreIdentifierMaxLength(identifierType);
    }
    
    @Override
    public SQLTypeInfo newSQLTypeInfo(final ResultSet rs) {
        return new DB2TypeInfo(rs);
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
    public String getDropDatabaseStatement(final String schemaName, final String catalogName) {
        throw new UnsupportedOperationException("DB2 does not support dropping schema with cascade. You need to drop all tables first");
    }
    
    @Override
    public String getDropTableStatement(final Table table) {
        return "DROP TABLE " + table.toString();
    }
    
    @Override
    public String getAutoIncrementStmt(final Table table, final String columnName) {
        return "VALUES IDENTITY_VAL_LOCAL()";
    }
    
    @Override
    public String getAutoIncrementKeyword() {
        return "generated always as identity (start with 1)";
    }
    
    @Override
    public String getContinuationString() {
        return "";
    }
    
    @Override
    public String getSequenceCreateStmt(final String sequence_name, final Integer min, final Integer max, final Integer start, final Integer increment, final Integer cache_size) {
        if (sequence_name == null) {
            throw new NucleusUserException(DB2Adapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("CREATE SEQUENCE ");
        stmt.append(sequence_name);
        stmt.append(" AS INTEGER ");
        if (start != null) {
            stmt.append(" START WITH " + start);
        }
        if (increment != null) {
            stmt.append(" INCREMENT BY " + increment);
        }
        if (min != null) {
            stmt.append(" MINVALUE " + min);
        }
        if (max != null) {
            stmt.append(" MAXVALUE " + max);
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
            throw new NucleusUserException(DB2Adapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("VALUES NEXTVAL FOR ");
        stmt.append(sequence_name);
        return stmt.toString();
    }
    
    @Override
    public String getRangeByRowNumberColumn() {
        return "row_number()over()";
    }
    
    @Override
    public boolean isStatementCancel(final SQLException sqle) {
        return sqle.getErrorCode() == -952;
    }
    
    @Override
    public boolean isStatementTimeout(final SQLException sqle) {
        return (sqle.getSQLState() != null && sqle.getSQLState().equalsIgnoreCase("57014") && (sqle.getErrorCode() == -952 || sqle.getErrorCode() == -905)) || super.isStatementTimeout(sqle);
    }
}
