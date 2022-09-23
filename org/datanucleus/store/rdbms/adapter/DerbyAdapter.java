// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.key.CandidateKey;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.schema.DerbyTypeInfo;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import java.sql.ResultSet;
import java.sql.Statement;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.util.NucleusLogger;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Collection;
import java.sql.DatabaseMetaData;

public class DerbyAdapter extends BaseDatastoreAdapter
{
    private static final String CLOUDSCAPE_RESERVED_WORDS = "ADD,ALL,ALLOCATE,ALTER,AND,ANY,ARE,AS,ASC,ASSERTION,AT,AUTHORIZATION,AVG,BEGIN,BETWEEN,BIT,BIT_LENGTH,BOOLEAN,BOTH,BY,CALL,CASCADE,CASCADED,CASE,CAST,CHAR,CHARACTER,CHARACTER_LENGTH,CHAR_LENGTH,CHECK,CLOSE,COLLATE,COLLATION,COLUMN,COMMIT,CONNECT,CONNECTION,CONSTRAINT,CONSTRAINTS,CONTINUE,CONVERT,CORRESPONDING,COUNT,CREATE,CROSS,CURRENT,CURRENT_DATE,CURRENT_TIME,CURRENT_TIMESTAMP,CURRENT_USER,CURSOR,DEALLOCATE,DEC,DECIMAL,DECLARE,DEFERRABLE,DEFERRED,DELETE,DESC,DESCRIBE,DIAGNOSTICS,DISCONNECT,DISTINCT,DOUBLE,DROP,ELSE,END,ENDEXEC,ESCAPE,EXCEPT,EXCEPTION,EXEC,EXECUTE,EXISTS,EXPLAIN,EXTERNAL,EXTRACT,FALSE,FETCH,FIRST,FLOAT,FOR,FOREIGN,FOUND,FROM,FULL,FUNCTION,GET,GET_CURRENT_CONNECTION,GLOBAL,GO,GOTO,GRANT,GROUP,HAVING,HOUR,IDENTITY,IMMEDIATE,IN,INDICATOR,INITIALLY,INNER,INOUT,INPUT,INSENSITIVE,INSERT,INT,INTEGER,INTERSECT,INTO,IS,ISOLATION,JOIN,KEY,LAST,LEADING,LEFT,LIKE,LOCAL,LONGINT,LOWER,LTRIM,MATCH,MAX,MIN,MINUTE,NATIONAL,NATURAL,NCHAR,NVARCHAR,NEXT,NO,NOT,NULL,NULLIF,NUMERIC,OCTET_LENGTH,OF,ON,ONLY,OPEN,OPTION,OR,ORDER,OUT,OUTER,OUTPUT,OVERLAPS,PAD,PARTIAL,PREPARE,PRESERVE,PRIMARY,PRIOR,PRIVILEGES,PROCEDURE,PUBLIC,READ,REAL,REFERENCES,RELATIVE,RESTRICT,REVOKE,RIGHT,ROLLBACK,ROWS,RTRIM,RUNTIMESTATISTICS,SCHEMA,SCROLL,SECOND,SELECT,SESSION_USER,SET,SMALLINT,SOME,SPACE,SQL,SQLCODE,SQLERROR,SQLSTATE,SUBSTR,SUBSTRING,SUM,SYSTEM_USER,TABLE,TEMPORARY,TIMEZONE_HOUR,TIMEZONE_MINUTE,TINYINT,TO,TRAILING,TRANSACTION,TRANSLATE,TRANSLATION,TRIM,TRUE,UNION,UNIQUE,UNKNOWN,UPDATE,UPPER,USER,USING,VALUES,VARCHAR,VARYING,VIEW,WHENEVER,WHERE,WITH,WORK,WRITE,YEAR";
    
    public DerbyAdapter(final DatabaseMetaData metadata) {
        super(metadata);
        this.reservedKeywords.addAll((Collection<?>)this.parseKeywordList("ADD,ALL,ALLOCATE,ALTER,AND,ANY,ARE,AS,ASC,ASSERTION,AT,AUTHORIZATION,AVG,BEGIN,BETWEEN,BIT,BIT_LENGTH,BOOLEAN,BOTH,BY,CALL,CASCADE,CASCADED,CASE,CAST,CHAR,CHARACTER,CHARACTER_LENGTH,CHAR_LENGTH,CHECK,CLOSE,COLLATE,COLLATION,COLUMN,COMMIT,CONNECT,CONNECTION,CONSTRAINT,CONSTRAINTS,CONTINUE,CONVERT,CORRESPONDING,COUNT,CREATE,CROSS,CURRENT,CURRENT_DATE,CURRENT_TIME,CURRENT_TIMESTAMP,CURRENT_USER,CURSOR,DEALLOCATE,DEC,DECIMAL,DECLARE,DEFERRABLE,DEFERRED,DELETE,DESC,DESCRIBE,DIAGNOSTICS,DISCONNECT,DISTINCT,DOUBLE,DROP,ELSE,END,ENDEXEC,ESCAPE,EXCEPT,EXCEPTION,EXEC,EXECUTE,EXISTS,EXPLAIN,EXTERNAL,EXTRACT,FALSE,FETCH,FIRST,FLOAT,FOR,FOREIGN,FOUND,FROM,FULL,FUNCTION,GET,GET_CURRENT_CONNECTION,GLOBAL,GO,GOTO,GRANT,GROUP,HAVING,HOUR,IDENTITY,IMMEDIATE,IN,INDICATOR,INITIALLY,INNER,INOUT,INPUT,INSENSITIVE,INSERT,INT,INTEGER,INTERSECT,INTO,IS,ISOLATION,JOIN,KEY,LAST,LEADING,LEFT,LIKE,LOCAL,LONGINT,LOWER,LTRIM,MATCH,MAX,MIN,MINUTE,NATIONAL,NATURAL,NCHAR,NVARCHAR,NEXT,NO,NOT,NULL,NULLIF,NUMERIC,OCTET_LENGTH,OF,ON,ONLY,OPEN,OPTION,OR,ORDER,OUT,OUTER,OUTPUT,OVERLAPS,PAD,PARTIAL,PREPARE,PRESERVE,PRIMARY,PRIOR,PRIVILEGES,PROCEDURE,PUBLIC,READ,REAL,REFERENCES,RELATIVE,RESTRICT,REVOKE,RIGHT,ROLLBACK,ROWS,RTRIM,RUNTIMESTATISTICS,SCHEMA,SCROLL,SECOND,SELECT,SESSION_USER,SET,SMALLINT,SOME,SPACE,SQL,SQLCODE,SQLERROR,SQLSTATE,SUBSTR,SUBSTRING,SUM,SYSTEM_USER,TABLE,TEMPORARY,TIMEZONE_HOUR,TIMEZONE_MINUTE,TINYINT,TO,TRAILING,TRANSACTION,TRANSLATE,TRANSLATION,TRIM,TRUE,UNION,UNIQUE,UNKNOWN,UPDATE,UPPER,USER,USING,VALUES,VARCHAR,VARYING,VIEW,WHENEVER,WHERE,WITH,WORK,WRITE,YEAR"));
        this.supportedOptions.add("IdentityColumns");
        this.supportedOptions.add("LockWithSelectForUpdate");
        this.supportedOptions.add("CreateIndexesBeforeForeignKeys");
        this.supportedOptions.add("StoredProcs");
        this.supportedOptions.add("Sequences");
        this.supportedOptions.remove("DeferredConstraints");
        this.supportedOptions.remove("NullsInCandidateKeys");
        this.supportedOptions.remove("ColumnOptions_DefaultWithNotNull");
        if (this.datastoreMajorVersion >= 10) {
            this.supportedOptions.remove("ColumnOptions_NullsKeyword");
        }
        else {
            this.supportedOptions.add("ColumnOptions_NullsKeyword");
        }
        if (this.datastoreMajorVersion < 10 || (this.datastoreMajorVersion == 10 && this.datastoreMinorVersion < 6)) {
            this.supportedOptions.remove("ANSI_CrossJoin_Syntax");
            this.supportedOptions.add("ANSI_CrossJoinAsInner11_Syntax");
            this.supportedOptions.remove("Sequences");
        }
        if (this.datastoreMajorVersion >= 11 || this.datastoreMinorVersion > 4) {
            this.supportedOptions.add("OrderByWithNullsDirectives");
        }
    }
    
    @Override
    public void initialiseDatastore(final Object conn) {
        try {
            final Statement st = ((Connection)conn).createStatement();
            try {
                st.execute("DROP FUNCTION NUCLEUS_ASCII");
            }
            catch (SQLException ex) {}
            try {
                st.execute("CREATE FUNCTION NUCLEUS_ASCII(C CHAR(1)) RETURNS INTEGER EXTERNAL NAME 'org.datanucleus.store.rdbms.adapter.DerbySQLFunction.ascii' CALLED ON NULL INPUT LANGUAGE JAVA PARAMETER STYLE JAVA");
            }
            catch (SQLException sqle) {
                NucleusLogger.DATASTORE.warn(DerbyAdapter.LOCALISER.msg("051027", sqle));
            }
            try {
                st.execute("DROP FUNCTION NUCLEUS_MATCHES");
            }
            catch (SQLException ex2) {}
            try {
                st.execute("CREATE FUNCTION NUCLEUS_MATCHES(TEXT VARCHAR(8000), PATTERN VARCHAR(8000)) RETURNS INTEGER EXTERNAL NAME 'org.datanucleus.store.rdbms.adapter.DerbySQLFunction.matches' CALLED ON NULL INPUT LANGUAGE JAVA PARAMETER STYLE JAVA");
            }
            catch (SQLException sqle) {
                NucleusLogger.DATASTORE.warn(DerbyAdapter.LOCALISER.msg("051027", sqle));
            }
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new NucleusDataStoreException(e.getMessage(), e);
        }
    }
    
    @Override
    public String getSchemaName(final Connection conn) throws SQLException {
        return conn.getMetaData().getUserName().toUpperCase();
    }
    
    @Override
    public String getCatalogName(final Connection conn) throws SQLException {
        final String catalog = conn.getCatalog();
        return (catalog != null) ? catalog : "";
    }
    
    @Override
    public String getVendorID() {
        return "derby";
    }
    
    @Override
    public SQLTypeInfo newSQLTypeInfo(final ResultSet rs) {
        return new DerbyTypeInfo(rs);
    }
    
    @Override
    public String getDropDatabaseStatement(final String catalogName, final String schemaName) {
        throw new UnsupportedOperationException("Derby does not support dropping schema with cascade. You need to drop all tables first");
    }
    
    @Override
    public String getDropTableStatement(final Table table) {
        return "DROP TABLE " + table.toString();
    }
    
    @Override
    public String getAddCandidateKeyStatement(final CandidateKey ck, final IdentifierFactory factory) {
        if (ck.getName() != null) {
            final String identifier = factory.getIdentifierInAdapterCase(ck.getName());
            return "CREATE UNIQUE INDEX " + identifier + " ON " + ck.getTable().toString() + " " + ck.getColumnList();
        }
        return "ALTER TABLE " + ck.getTable().toString() + " ADD " + ck;
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
    public boolean isIdentityFieldDataType(final String columnDef) {
        return columnDef != null && columnDef.toUpperCase().indexOf("AUTOINCREMENT") >= 0;
    }
    
    @Override
    public String getInsertStatementForNoColumns(final Table table) {
        return "INSERT INTO " + table.toString() + " VALUES (DEFAULT)";
    }
    
    @Override
    public String getDatastoreDateStatement() {
        return "VALUES CURRENT_TIMESTAMP";
    }
    
    @Override
    public String getSelectForUpdateText() {
        return "WITH RR";
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
    public String getNumericConversionFunction() {
        return "NUCLEUS_ASCII";
    }
    
    @Override
    public boolean isStatementCancel(final SQLException sqle) {
        return sqle.getSQLState().equalsIgnoreCase("XCL52");
    }
    
    @Override
    public String getSequenceCreateStmt(final String sequence_name, final Integer min, final Integer max, final Integer start, final Integer increment, final Integer cache_size) {
        if (sequence_name == null) {
            throw new NucleusUserException(DerbyAdapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("CREATE SEQUENCE ");
        stmt.append(sequence_name);
        if (start != null) {
            stmt.append(" START WITH " + start);
        }
        if (increment != null) {
            stmt.append(" INCREMENT BY " + increment);
        }
        if (max != null) {
            stmt.append(" MAXVALUE " + max);
        }
        else {
            stmt.append(" NO MAXVALUE");
        }
        if (min != null) {
            stmt.append(" MINVALUE " + min);
        }
        else {
            stmt.append(" NO MINVALUE");
        }
        if (cache_size != null) {
            throw new NucleusUserException(DerbyAdapter.LOCALISER.msg("051023"));
        }
        return stmt.toString();
    }
    
    @Override
    public String getSequenceNextStmt(final String sequence_name) {
        if (sequence_name == null) {
            throw new NucleusUserException(DerbyAdapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("VALUES NEXT VALUE FOR ");
        stmt.append(sequence_name);
        stmt.append(" ");
        return stmt.toString();
    }
}
