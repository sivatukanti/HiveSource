// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.ClassConstants;
import org.datanucleus.store.rdbms.sql.SQLText;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.table.ViewImpl;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.key.Index;
import org.datanucleus.store.rdbms.key.CandidateKey;
import java.util.List;
import org.datanucleus.store.rdbms.key.PrimaryKey;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.key.ForeignKey;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import java.util.Properties;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.table.TableImpl;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.StoreManager;
import java.sql.Connection;
import java.util.Set;
import org.datanucleus.store.rdbms.schema.ForeignKeyInfo;
import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import java.sql.ResultSet;
import java.util.Iterator;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import java.sql.Timestamp;
import org.datanucleus.store.schema.StoreSchemaData;
import org.datanucleus.store.rdbms.schema.JDBCTypeInfo;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.JDBCUtils;
import org.datanucleus.store.rdbms.schema.RDBMSTypesInfo;
import org.datanucleus.store.rdbms.mapping.RDBMSMappingManager;
import java.util.HashMap;
import org.datanucleus.store.rdbms.mapping.MappingManager;
import org.datanucleus.plugin.PluginManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.StoreSchemaHandler;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.util.StringTokenizer;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.DatabaseMetaData;
import java.util.Map;
import java.util.Collection;
import java.util.HashSet;
import org.datanucleus.util.Localiser;

public class BaseDatastoreAdapter implements DatastoreAdapter
{
    protected static final Localiser LOCALISER_BASE;
    protected static final Localiser LOCALISER;
    protected final HashSet<String> reservedKeywords;
    protected String datastoreProductName;
    protected String datastoreProductVersion;
    protected int datastoreMajorVersion;
    protected int datastoreMinorVersion;
    protected int datastoreRevisionVersion;
    protected String identifierQuoteString;
    protected Collection<String> supportedOptions;
    protected String driverName;
    protected String driverVersion;
    protected int driverMajorVersion;
    protected int driverMinorVersion;
    protected int maxTableNameLength;
    protected int maxConstraintNameLength;
    protected int maxIndexNameLength;
    protected int maxColumnNameLength;
    protected String catalogSeparator;
    protected Map<String, Object> properties;
    
    protected BaseDatastoreAdapter(final DatabaseMetaData metadata) {
        this.reservedKeywords = new HashSet<String>();
        this.datastoreRevisionVersion = 0;
        this.supportedOptions = new HashSet<String>();
        this.properties = null;
        this.reservedKeywords.addAll((Collection<?>)this.parseKeywordList("ABSOLUTE,ACTION,ADD,ALL,ALLOCATE,ALTER,AND,ANY,ARE,AS,ASC,ASSERTION,AT,AUTHORIZATION,AVG,BEGIN,BETWEEN,BIT,BIT_LENGTH,BOTH,BY,CASCADE,CASCADED,CASE,CAST,CATALOG,CHAR,CHARACTER,CHAR_LENGTH,CHARACTER_LENGTH,CHECK,CLOSE,COALESCE,COLLATE,COLLATION,COLUMN,COMMIT,CONNECT,CONNECTION,CONSTRAINT,CONSTRAINTS,CONTINUE,CONVERT,CORRESPONDING,COUNT,CREATE,CROSS,CURRENT,CURRENT_DATE,CURRENT_TIME,CURRENT_TIMESTAMP,CURRENT_USER,CURSOR,DATE,DAY,DEALLOCATE,DEC,DECIMAL,DECLARE,DEFAULT,DEFERRABLE,DEFERRED,DELETE,DESC,DESCRIBE,DESCRIPTOR,DIAGNOSTICS,DISCONNECT,DISTINCT,DOMAIN,DOUBLE,DROP,ELSE,END,END-EXEC,ESCAPE,EXCEPT,EXCEPTION,EXEC,EXECUTE,EXISTS,EXTERNAL,EXTRACT,FALSE,FETCH,FIRST,FLOAT,FOR,FOREIGN,FOUND,FROM,FULL,GET,GLOBAL,GO,GOTO,GRANT,GROUP,HAVING,HOUR,IDENTITY,IMMEDIATE,IN,INDICATOR,INITIALLY,INNER,INPUT,INSENSITIVE,INSERT,INT,INTEGER,INTERSECT,INTERVAL,INTO,IS,ISOLATION,JOIN,KEY,LANGUAGE,LAST,LEADING,LEFT,LEVEL,LIKE,LOCAL,LOWER,MATCH,MAX,MIN,MINUTE,MODULE,MONTH,NAMES,NATIONAL,NATURAL,NCHAR,NEXT,NO,NOT,NULL,NULLIF,NUMERIC,OCTET_LENGTH,OF,ON,ONLY,OPEN,OPTION,OR,ORDER,OUTER,OUTPUT,OVERLAPS,PAD,PARTIAL,POSITION,PRECISION,PREPARE,PRESERVE,PRIMARY,PRIOR,PRIVILEGES,PROCEDURE,PUBLIC,READ,REAL,REFERENCES,RELATIVE,RESTRICT,REVOKE,RIGHT,ROLLBACK,ROWS,SCHEMA,SCROLL,SECOND,SECTION,SELECT,SESSION,SESSION_USER,SET,SIZE,SMALLINT,SOME,SPACE,SQL,SQLCODE,SQLERROR,SQLSTATE,SUBSTRING,SUM,SYSTEM_USER,TABLE,TEMPORARY,THEN,TIME,TIMESTAMP,TIMEZONE_HOUR,TIMEZONE_MINUTE,TO,TRAILING,TRANSACTION,TRANSLATE,TRANSLATION,TRIM,TRUE,UNION,UNIQUE,UNKNOWN,UPDATE,UPPER,USAGE,USER,USING,VALUE,VALUES,VARCHAR,VARYING,VIEW,WHEN,WHENEVER,WHERE,WITH,WORK,WRITE,YEAR,ZONE"));
        this.reservedKeywords.addAll((Collection<?>)this.parseKeywordList("ABSOLUTE,ACTION,ADD,AFTER,ALL,ALLOCATE,ALTER,AND,ANY,ARE,ARRAY,AS,ASC,ASENSITIVE,ASSERTION,ASYMMETRIC,AT,ATOMIC,AUTHORIZATION,BEFORE,BEGIN,BETWEEN,BINARY,BIT,BLOB,BOOLEAN,BOTH,BREADTH,BY,CALL,CALLED,CASCADE,CASCADED,CASE,CAST,CATALOG,CHAR,CHARACTER,CHECK,CLOB,CLOSE,COLLATE,COLLATION,COLUMN,COMMIT,CONDITION,CONNECT,CONNECTION,CONSTRAINT,CONSTRAINTS,CONSTRUCTOR,CONTINUE,CORRESPONDING,CREATE,CROSS,CUBE,CURRENT,CURRENT_DATE,CURRENT_DEFAULT_TRANSFORM_GROUP,CURRENT_PATH,CURRENT_ROLE,CURRENT_TIME,CURRENT_TIMESTAMP,CURRENT_TRANSFORM_GROUP_FOR_TYPE,CURRENT_USER,CURSOR,CYCLE,DATA,DATE,DAY,DEALLOCATE,DEC,DECIMAL,DECLARE,DEFAULT,DEFERRABLE,DEFERRED,DELETE,DEPTH,DEREF,DESC,DESCRIBE,DESCRIPTOR,DETERMINISTIC,DIAGNOSTICS,DISCONNECT,DISTINCT,DO,DOMAIN,DOUBLE,DROP,DYNAMIC,EACH,ELSE,ELSEIF,END,EQUALS,ESCAPE,EXCEPT,EXCEPTION,EXEC,EXECUTE,EXISTS,EXIT,EXTERNAL,FALSE,FETCH,FILTER,FIRST,FLOAT,FOR,FOREIGN,FOUND,FREE,FROM,FULL,FUNCTION,GENERAL,GET,GLOBAL,GO,GOTO,GRANT,GROUP,GROUPING,HANDLER,HAVING,HOLD,HOUR,IDENTITY,IF,IMMEDIATE,IN,INDICATOR,INITIALLY,INNER,INOUT,INPUT,INSENSITIVE,INSERT,INT,INTEGER,INTERSECT,INTERVAL,INTO,IS,ISOLATION,ITERATE,JOIN,KEY,LANGUAGE,LARGE,LAST,LATERAL,LEADING,LEAVE,LEFT,LEVEL,LIKE,LOCAL,LOCALTIME,LOCALTIMESTAMP,LOCATOR,LOOP,MAP,MATCH,METHOD,MINUTE,MODIFIES,MODULE,MONTH,NAMES,NATIONAL,NATURAL,NCHAR,NCLOB,NEW,NEXT,NO,NONE,NOT,NULL,NUMERIC,OBJECT,OF,OLD,ON,ONLY,OPEN,OPTION,OR,ORDER,ORDINALITY,OUT,OUTER,OUTPUT,OVER,OVERLAPS,PAD,PARAMETER,PARTIAL,PARTITION,PATH,PRECISION,PREPARE,PRESERVE,PRIMARY,PRIOR,PRIVILEGES,PROCEDURE,PUBLIC,RANGE,READ,READS,REAL,RECURSIVE,REF,REFERENCES,REFERENCING,RELATIVE,RELEASE,REPEAT,RESIGNAL,RESTRICT,RESULT,RETURN,RETURNS,REVOKE,RIGHT,ROLE,ROLLBACK,ROLLUP,ROUTINE,ROW,ROWS,SAVEPOINT,SCHEMA,SCOPE,SCROLL,SEARCH,SECOND,SECTION,SELECT,SENSITIVE,SESSION,SESSION_USER,SET,SETS,SIGNAL,SIMILAR,SIZE,SMALLINT,SOME,SPACE,SPECIFIC,SPECIFICTYPE,SQL,SQLEXCEPTION,SQLSTATE,SQLWARNING,START,STATE,STATIC,SYMMETRIC,SYSTEM,SYSTEM_USER,TABLE,TEMPORARY,THEN,TIME,TIMESTAMP,TIMEZONE_HOUR,TIMEZONE_MINUTE,TO,TRAILING,TRANSACTION,TRANSLATION,TREAT,TRIGGER,TRUE,UNDER,UNDO,UNION,UNIQUE,UNKNOWN,UNNEST,UNTIL,UPDATE,USAGE,USER,USING,VALUE,VALUES,VARCHAR,VARYING,VIEW,WHEN,WHENEVER,WHERE,WHILE,WINDOW,WITH,WITHIN,WITHOUT,WORK,WRITE,YEAR,ZONE"));
        this.reservedKeywords.addAll((Collection<?>)this.parseKeywordList("ADD,ALL,ALLOCATE,ALTER,AND,ANY,ARE,ARRAY,AS,ASENSITIVE,ASYMMETRIC,AT,ATOMIC,AUTHORIZATION,BEGIN,BETWEEN,BIGINT,BINARY,BLOB,BOOLEAN,BOTH,BY,CALL,CALLED,CASCADED,CASE,CAST,CHAR,CHARACTER,CHECK,CLOB,CLOSE,COLLATE,COLUMN,COMMIT,CONDITION,CONNECT,CONSTRAINT,CONTINUE,CORRESPONDING,CREATE,CROSS,CUBE,CURRENT,CURRENT_DATE,CURRENT_DEFAULT_TRANSFORM_GROUP,CURRENT_PATH,CURRENT_ROLE,CURRENT_TIME,CURRENT_TIMESTAMP,CURRENT_TRANSFORM_GROUP_FOR_TYPE,CURRENT_USER,CURSOR,CYCLE,DATE,DAY,DEALLOCATE,DEC,DECIMAL,DECLARE,DEFAULT,DELETE,DEREF,DESCRIBE,DETERMINISTIC,DISCONNECT,DISTINCT,DO,DOUBLE,DROP,DYNAMIC,EACH,ELEMENT,ELSE,ELSEIF,END,ESCAPE,EXCEPT,EXEC,EXECUTE,EXISTS,EXIT,EXTERNAL,FALSE,FETCH,FILTER,FLOAT,FOR,FOREIGN,FREE,FROM,FULL,FUNCTION,GET,GLOBAL,GRANT,GROUP,GROUPING,HANDLER,HAVING,HOLD,HOUR,IDENTITY,IF,IMMEDIATE,IN,INDICATOR,INNER,INOUT,INPUT,INSENSITIVE,INSERT,INT,INTEGER,INTERSECT,INTERVAL,INTO,IS,ITERATE,JOIN,LANGUAGE,LARGE,LATERAL,LEADING,LEAVE,LEFT,LIKE,LOCAL,LOCALTIME,LOCALTIMESTAMP,LOOP,MATCH,MEMBER,MERGE,METHOD,MINUTE,MODIFIES,MODULE,MONTH,MULTISET,NATIONAL,NATURAL,NCHAR,NCLOB,NEW,NO,NONE,NOT,NULL,NUMERIC,OF,OLD,ON,ONLY,OPEN,OR,ORDER,OUT,OUTER,OUTPUT,OVER,OVERLAPS,PARAMETER,PARTITION,PRECISION,PREPARE,PRIMARY,PROCEDURE,RANGE,READS,REAL,RECURSIVE,REF,REFERENCES,REFERENCING,RELEASE,REPEAT,RESIGNAL,RESULT,RETURN,RETURNS,REVOKE,RIGHT,ROLLBACK,ROLLUP,ROW,ROWS,SAVEPOINT,SCOPE,SCROLL,SEARCH,SECOND,SELECT,SENSITIVE,SESSION_USER,SET,SIGNAL,SIMILAR,SMALLINT,SOME,SPECIFIC,SPECIFICTYPE,SQL,SQLEXCEPTION,SQLSTATE,SQLWARNING,START,STATIC,SUBMULTISET,SYMMETRIC,SYSTEM,SYSTEM_USER,TABLE,TABLESAMPLE,THEN,TIME,TIMESTAMP,TIMEZONE_HOUR,TIMEZONE_MINUTE,TO,TRAILING,TRANSLATION,TREAT,TRIGGER,TRUE,UNDO,UNION,UNIQUE,UNKNOWN,UNNEST,UNTIL,UPDATE,USER,USING,VALUE,VALUES,VARCHAR,VARYING,WHEN,WHENEVER,WHERE,WHILE,WINDOW,WITH,WITHIN,WITHOUT,YEAR"));
        this.reservedKeywords.addAll((Collection<?>)this.parseKeywordList("ADA,C,CATALOG_NAME,CHARACTER_SET_CATALOG,CHARACTER_SET_NAME,CHARACTER_SET_SCHEMA,CLASS_ORIGIN,COBOL,COLLATION_CATALOG,COLLATION_NAME,COLLATION_SCHEMA,COLUMN_NAME,COMMAND_FUNCTION,COMMITTED,CONDITION_NUMBER,CONNECTION_NAME,CONSTRAINT_CATALOG,CONSTRAINT_NAME,CONSTRAINT_SCHEMA,CURSOR_NAME,DATA,DATETIME_INTERVAL_CODE,DATETIME_INTERVAL_PRECISION,DYNAMIC_FUNCTION,FORTRAN,LENGTH,MESSAGE_LENGTH,MESSAGE_OCTET_LENGTH,MESSAGE_TEXT,MORE,MUMPS,NAME,NULLABLE,NUMBER,PASCAL,PLI,REPEATABLE,RETURNED_LENGTH,RETURNED_OCTET_LENGTH,RETURNED_SQLSTATE,ROW_COUNT,SCALE,SCHEMA_NAME,SERIALIZABLE,SERVER_NAME,SUBCLASS_ORIGIN,TABLE_NAME,TYPE,UNCOMMITTED,UNNAMED"));
        try {
            try {
                final String sqlKeywordsString = metadata.getSQLKeywords();
                this.reservedKeywords.addAll((Collection<?>)this.parseKeywordList(sqlKeywordsString));
            }
            catch (SQLFeatureNotSupportedException ex) {}
            this.driverMinorVersion = metadata.getDriverMinorVersion();
            this.driverMajorVersion = metadata.getDriverMajorVersion();
            this.driverName = metadata.getDriverName();
            this.driverVersion = metadata.getDriverVersion();
            this.datastoreProductName = metadata.getDatabaseProductName();
            this.datastoreProductVersion = metadata.getDatabaseProductVersion();
            final StringBuffer strippedProductVersion = new StringBuffer();
            char previousChar = ' ';
            for (int i = 0; i < this.datastoreProductVersion.length(); ++i) {
                final char c = this.datastoreProductVersion.charAt(i);
                if (Character.isDigit(c) || c == '.') {
                    if (previousChar != ' ') {
                        if (strippedProductVersion.length() == 0) {
                            strippedProductVersion.append(previousChar);
                        }
                        strippedProductVersion.append(c);
                    }
                    previousChar = c;
                }
                else {
                    previousChar = ' ';
                }
            }
            this.datastoreMajorVersion = metadata.getDatabaseMajorVersion();
            this.datastoreMinorVersion = metadata.getDatabaseMinorVersion();
            try {
                boolean noDBVersion = false;
                if (this.datastoreMajorVersion <= 0 && this.datastoreMinorVersion <= 0) {
                    noDBVersion = true;
                }
                final StringTokenizer parts = new StringTokenizer(strippedProductVersion.toString(), ".");
                if (parts.hasMoreTokens()) {
                    if (noDBVersion) {
                        try {
                            this.datastoreMajorVersion = Integer.parseInt(parts.nextToken());
                        }
                        catch (Exception e2) {
                            this.datastoreMajorVersion = -1;
                        }
                    }
                    else {
                        parts.nextToken();
                    }
                }
                if (parts.hasMoreTokens()) {
                    if (noDBVersion) {
                        try {
                            this.datastoreMinorVersion = Integer.parseInt(parts.nextToken());
                        }
                        catch (Exception e2) {
                            this.datastoreMajorVersion = -1;
                        }
                    }
                    else {
                        parts.nextToken();
                    }
                }
                if (parts.hasMoreTokens()) {
                    try {
                        this.datastoreRevisionVersion = Integer.parseInt(parts.nextToken());
                    }
                    catch (Exception e2) {
                        this.datastoreRevisionVersion = -1;
                    }
                }
            }
            catch (Throwable t) {
                final StringTokenizer parts = new StringTokenizer(strippedProductVersion.toString(), ".");
                if (parts.hasMoreTokens()) {
                    try {
                        this.datastoreMajorVersion = Integer.parseInt(parts.nextToken());
                    }
                    catch (Exception e2) {
                        this.datastoreMajorVersion = -1;
                    }
                }
                if (parts.hasMoreTokens()) {
                    try {
                        this.datastoreMinorVersion = Integer.parseInt(parts.nextToken());
                    }
                    catch (Exception e2) {
                        this.datastoreMajorVersion = -1;
                    }
                }
                if (parts.hasMoreTokens()) {
                    try {
                        this.datastoreRevisionVersion = Integer.parseInt(parts.nextToken());
                    }
                    catch (Exception e2) {
                        this.datastoreRevisionVersion = -1;
                    }
                }
            }
            this.maxTableNameLength = metadata.getMaxTableNameLength();
            this.maxConstraintNameLength = metadata.getMaxTableNameLength();
            this.maxIndexNameLength = metadata.getMaxTableNameLength();
            this.maxColumnNameLength = metadata.getMaxColumnNameLength();
            if (metadata.supportsCatalogsInTableDefinitions()) {
                this.supportedOptions.add("CatalogInTableDefinition");
            }
            if (metadata.supportsSchemasInTableDefinitions()) {
                this.supportedOptions.add("SchemaInTableDefinition");
            }
            if (metadata.supportsBatchUpdates()) {
                this.supportedOptions.add("StatementBatching");
            }
            if (metadata.storesLowerCaseIdentifiers()) {
                this.supportedOptions.add("LowerCaseIdentifiers");
            }
            if (metadata.storesMixedCaseIdentifiers()) {
                this.supportedOptions.add("MixedCaseIdentifiers");
            }
            if (metadata.storesUpperCaseIdentifiers()) {
                this.supportedOptions.add("UpperCaseIdentifiers");
            }
            if (metadata.storesLowerCaseQuotedIdentifiers()) {
                this.supportedOptions.add("LowerCaseQuotedIdentifiers");
            }
            if (metadata.storesMixedCaseQuotedIdentifiers()) {
                this.supportedOptions.add("MixedCaseQuotedIdentifiers");
            }
            if (metadata.storesUpperCaseQuotedIdentifiers()) {
                this.supportedOptions.add("UpperCaseQuotedIdentifiers");
            }
            if (metadata.supportsMixedCaseIdentifiers()) {
                this.supportedOptions.add("MixedCaseSensitiveIdentifiers");
            }
            if (metadata.supportsMixedCaseQuotedIdentifiers()) {
                this.supportedOptions.add("MixedCaseQuotedSensitiveIdentifiers");
            }
            this.catalogSeparator = metadata.getCatalogSeparator();
            this.catalogSeparator = ((this.catalogSeparator == null || this.catalogSeparator.trim().length() < 1) ? "." : this.catalogSeparator);
            this.identifierQuoteString = metadata.getIdentifierQuoteString();
            this.identifierQuoteString = ((null == this.identifierQuoteString || this.identifierQuoteString.trim().length() < 1) ? "\"" : this.identifierQuoteString);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(BaseDatastoreAdapter.LOCALISER.msg("051004"), e);
        }
        this.supportedOptions.add("TableAliasInUpdateSet");
        this.supportedOptions.add("Views");
        this.supportedOptions.add("DateTimeStoresMillisecs");
        this.supportedOptions.add("EscapeExpressionInLikePredicate");
        this.supportedOptions.add("Union_Syntax");
        this.supportedOptions.add("Exists_Syntax");
        this.supportedOptions.add("AlterTableDropConstraint_Syntax");
        this.supportedOptions.add("DeferredConstraints");
        this.supportedOptions.add("DistinctWithSelectForUpdate");
        this.supportedOptions.add("PersistOfUnassignedChar");
        this.supportedOptions.add("CheckInCreateStatements");
        this.supportedOptions.add("GetGeneratedKeysStatement");
        this.supportedOptions.add("BooleanExpression");
        this.supportedOptions.add("NullsInCandidateKeys");
        this.supportedOptions.add("ColumnOptions_NullsKeyword");
        this.supportedOptions.add("ColumnOptions_DefaultKeyword");
        this.supportedOptions.add("ColumnOptions_DefaultWithNotNull");
        this.supportedOptions.add("ColumnOptions_DefaultBeforeNull");
        this.supportedOptions.add("ANSI_Join_Syntax");
        this.supportedOptions.add("ANSI_CrossJoin_Syntax");
        this.supportedOptions.add("AutoIncrementNullSpecification");
        this.supportedOptions.add("AutoIncrementColumnTypeSpecification");
        this.supportedOptions.add("IncludeOrderByColumnsInSelect");
        this.supportedOptions.add("AccessParentQueryInSubquery");
        this.supportedOptions.add("FkDeleteActionCascade");
        this.supportedOptions.add("FkDeleteActionRestrict");
        this.supportedOptions.add("FkDeleteActionDefault");
        this.supportedOptions.add("FkDeleteActionNull");
        this.supportedOptions.add("FkUpdateActionCascade");
        this.supportedOptions.add("FkUpdateActionRestrict");
        this.supportedOptions.add("FkUpdateActionDefault");
        this.supportedOptions.add("FkUpdateActionNull");
        this.supportedOptions.add("TxIsolationReadCommitted");
        this.supportedOptions.add("TxIsolationReadUncommitted");
        this.supportedOptions.add("TxIsolationReadRepeatableRead");
        this.supportedOptions.add("TxIsolationSerializable");
    }
    
    @Override
    public void initialiseDatastore(final Object conn) {
    }
    
    @Override
    public void initialiseTypes(final StoreSchemaHandler handler, final ManagedConnection mconn) {
        final RDBMSStoreManager storeMgr = (RDBMSStoreManager)handler.getStoreManager();
        final ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        final PluginManager pluginMgr = storeMgr.getNucleusContext().getPluginManager();
        final MappingManager mapMgr = storeMgr.getMappingManager();
        mapMgr.loadDatastoreMapping(pluginMgr, clr, this.getVendorID());
        handler.getSchemaData(mconn.getConnection(), "types", null);
    }
    
    @Override
    public void setProperties(final Map<String, Object> props) {
        if (props != null) {
            this.properties = new HashMap<String, Object>();
        }
        this.properties.putAll(props);
    }
    
    public Object getValueForProperty(final String name) {
        return (this.properties != null) ? this.properties.get(name) : null;
    }
    
    @Override
    public void removeUnsupportedMappings(final StoreSchemaHandler handler, final ManagedConnection mconn) {
        final RDBMSStoreManager storeMgr = (RDBMSStoreManager)handler.getStoreManager();
        final RDBMSMappingManager mapMgr = (RDBMSMappingManager)storeMgr.getMappingManager();
        final RDBMSTypesInfo types = (RDBMSTypesInfo)handler.getSchemaData(mconn.getConnection(), "types", null);
        final int[] jdbcTypes = JDBCUtils.getJDBCTypes();
        for (int i = 0; i < jdbcTypes.length; ++i) {
            if (types.getChild("" + jdbcTypes[i]) == null) {
                mapMgr.deregisterDatastoreMappingsForJDBCType(JDBCUtils.getNameForJDBCType(jdbcTypes[i]));
            }
        }
    }
    
    protected Collection<SQLTypeInfo> getSQLTypeInfoForJdbcType(final StoreSchemaHandler handler, final ManagedConnection mconn, final short jdbcTypeNumber) {
        final RDBMSTypesInfo types = (RDBMSTypesInfo)handler.getSchemaData(mconn.getConnection(), "types", null);
        final String key = "" + jdbcTypeNumber;
        final JDBCTypeInfo jdbcType = (JDBCTypeInfo)types.getChild(key);
        if (jdbcType == null) {
            return null;
        }
        return jdbcType.getChildren().values();
    }
    
    protected void addSQLTypeForJDBCType(final StoreSchemaHandler handler, final ManagedConnection mconn, final short jdbcTypeNumber, final SQLTypeInfo sqlType, final boolean addIfNotPresent) {
        final RDBMSTypesInfo types = (RDBMSTypesInfo)handler.getSchemaData(mconn.getConnection(), "types", null);
        final String key = "" + jdbcTypeNumber;
        JDBCTypeInfo jdbcType = (JDBCTypeInfo)types.getChild(key);
        if (jdbcType != null && !addIfNotPresent) {
            return;
        }
        if (jdbcType == null) {
            jdbcType = new JDBCTypeInfo(jdbcTypeNumber);
            types.addChild(jdbcType);
            jdbcType.addChild(sqlType);
        }
        else {
            jdbcType.addChild(sqlType);
        }
    }
    
    @Override
    public boolean supportsTransactionIsolation(final int level) {
        return (level == 0 && this.supportsOption("TxIsolationNone")) || (level == 2 && this.supportsOption("TxIsolationReadCommitted")) || (level == 1 && this.supportsOption("TxIsolationReadUncommitted")) || (level == 4 && this.supportsOption("TxIsolationReadRepeatableRead")) || (level == 8 && this.supportsOption("TxIsolationSerializable"));
    }
    
    @Override
    public Collection<String> getSupportedOptions() {
        return this.supportedOptions;
    }
    
    @Override
    public boolean supportsOption(final String option) {
        return this.supportedOptions.contains(option);
    }
    
    @Override
    public MappingManager getMappingManager(final RDBMSStoreManager storeMgr) {
        return new RDBMSMappingManager(storeMgr);
    }
    
    @Override
    public long getAdapterTime(final Timestamp time) {
        final long timestamp = this.getTime(time.getTime(), time.getNanos());
        final int ms = this.getMiliseconds(time.getNanos());
        return timestamp + ms;
    }
    
    protected long getTime(final long time, final long nanos) {
        if (nanos < 0L) {
            return (time / 1000L - 1L) * 1000L;
        }
        return time / 1000L * 1000L;
    }
    
    protected int getMiliseconds(final long nanos) {
        return (int)(nanos / 1000000L);
    }
    
    @Override
    public String getDatastoreProductName() {
        return this.datastoreProductName;
    }
    
    @Override
    public String getDatastoreProductVersion() {
        return this.datastoreProductVersion;
    }
    
    @Override
    public String getDatastoreDriverName() {
        return this.driverName;
    }
    
    @Override
    public String getDatastoreDriverVersion() {
        return this.driverVersion;
    }
    
    @Override
    public boolean supportsQueryFetchSize(final int size) {
        return true;
    }
    
    @Override
    public String getVendorID() {
        return null;
    }
    
    @Override
    public boolean isReservedKeyword(final String word) {
        return this.reservedKeywords.contains(word.toUpperCase());
    }
    
    @Override
    public String getIdentifierQuoteString() {
        return this.identifierQuoteString;
    }
    
    @Override
    public int getDriverMajorVersion() {
        return this.driverMajorVersion;
    }
    
    @Override
    public int getDriverMinorVersion() {
        return this.driverMinorVersion;
    }
    
    @Override
    public int getDatastoreIdentifierMaxLength(final IdentifierType identifierType) {
        if (identifierType == IdentifierType.TABLE) {
            return this.maxTableNameLength;
        }
        if (identifierType == IdentifierType.COLUMN) {
            return this.maxColumnNameLength;
        }
        if (identifierType == IdentifierType.CANDIDATE_KEY) {
            return this.maxConstraintNameLength;
        }
        if (identifierType == IdentifierType.FOREIGN_KEY) {
            return this.maxConstraintNameLength;
        }
        if (identifierType == IdentifierType.INDEX) {
            return this.maxIndexNameLength;
        }
        if (identifierType == IdentifierType.PRIMARY_KEY) {
            return this.maxConstraintNameLength;
        }
        if (identifierType == IdentifierType.SEQUENCE) {
            return this.maxTableNameLength;
        }
        return -1;
    }
    
    @Override
    public int getMaxForeignKeys() {
        return 9999;
    }
    
    @Override
    public int getMaxIndexes() {
        return 9999;
    }
    
    @Override
    public Iterator iteratorReservedWords() {
        return this.reservedKeywords.iterator();
    }
    
    @Override
    public RDBMSColumnInfo newRDBMSColumnInfo(final ResultSet rs) {
        return new RDBMSColumnInfo(rs);
    }
    
    @Override
    public SQLTypeInfo newSQLTypeInfo(final ResultSet rs) {
        return new SQLTypeInfo(rs);
    }
    
    @Override
    public ForeignKeyInfo newFKInfo(final ResultSet rs) {
        return new ForeignKeyInfo(rs);
    }
    
    protected Set parseKeywordList(final String list) {
        final StringTokenizer tokens = new StringTokenizer(list, ",");
        final HashSet words = new HashSet();
        while (tokens.hasMoreTokens()) {
            words.add(tokens.nextToken().trim().toUpperCase());
        }
        return words;
    }
    
    public boolean isKeyword(final String word) {
        return this.isReservedKeyword(word.toUpperCase());
    }
    
    @Override
    public int getUnlimitedLengthPrecisionValue(final SQLTypeInfo typeInfo) {
        if (typeInfo.getCreateParams() != null && typeInfo.getCreateParams().length() > 0) {
            return typeInfo.getPrecision();
        }
        return -1;
    }
    
    @Override
    public boolean isValidPrimaryKeyType(final int datatype) {
        return datatype != 2004 && datatype != 2005 && datatype != -4;
    }
    
    @Override
    public String getSurrogateForEmptyStrings() {
        return null;
    }
    
    @Override
    public int getTransactionIsolationForSchemaCreation() {
        return 8;
    }
    
    @Override
    public int getRequiredTransactionIsolationLevel() {
        return -1;
    }
    
    @Override
    public String getCatalogName(final Connection conn) throws SQLException {
        throw new UnsupportedOperationException(BaseDatastoreAdapter.LOCALISER.msg("051015", this.datastoreProductName, this.datastoreProductVersion));
    }
    
    @Override
    public String getSchemaName(final Connection conn) throws SQLException {
        throw new UnsupportedOperationException(BaseDatastoreAdapter.LOCALISER.msg("051016", this.datastoreProductName, this.datastoreProductVersion));
    }
    
    @Override
    public String getCatalogSeparator() {
        return this.catalogSeparator;
    }
    
    @Override
    public String getSelectWithLockOption() {
        return null;
    }
    
    @Override
    public String getSelectForUpdateText() {
        return "FOR UPDATE";
    }
    
    @Override
    public String getSelectNewUUIDStmt() {
        return null;
    }
    
    public String getNewUUIDFunction() {
        return null;
    }
    
    @Override
    public String getOrderString(final StoreManager storeMgr, final String orderString, final SQLExpression sqlExpr) {
        return orderString;
    }
    
    @Override
    public boolean validToSelectMappingInStatement(final SQLStatement stmt, final JavaTypeMapping m) {
        return true;
    }
    
    @Override
    public String getAutoIncrementStmt(final Table table, final String columnName) {
        throw new UnsupportedOperationException(BaseDatastoreAdapter.LOCALISER.msg("051019"));
    }
    
    @Override
    public String getAutoIncrementKeyword() {
        throw new UnsupportedOperationException(BaseDatastoreAdapter.LOCALISER.msg("051019"));
    }
    
    @Override
    public boolean isIdentityFieldDataType(final String typeName) {
        throw new UnsupportedOperationException(BaseDatastoreAdapter.LOCALISER.msg("051019"));
    }
    
    @Override
    public String getInsertStatementForNoColumns(final Table table) {
        return "INSERT INTO " + table.toString() + " () VALUES ()";
    }
    
    @Override
    public String getSequenceCreateStmt(final String sequence_name, final Integer min, final Integer max, final Integer start, final Integer increment, final Integer cache_size) {
        throw new UnsupportedOperationException(BaseDatastoreAdapter.LOCALISER.msg("051020"));
    }
    
    @Override
    public String getSequenceNextStmt(final String sequence_name) {
        throw new UnsupportedOperationException(BaseDatastoreAdapter.LOCALISER.msg("051020"));
    }
    
    @Override
    public ResultSet getExistingIndexes(final Connection conn, final String catalog, final String schema, final String table) throws SQLException {
        return null;
    }
    
    @Override
    public String getCreateTableStatement(final TableImpl table, final Column[] columns, final Properties props, final IdentifierFactory factory) {
        final StringBuffer createStmt = new StringBuffer();
        String indent = "    ";
        if (this.getContinuationString().length() == 0) {
            indent = "";
        }
        createStmt.append("CREATE TABLE ").append(table.toString()).append(this.getContinuationString()).append("(").append(this.getContinuationString());
        for (int i = 0; i < columns.length; ++i) {
            if (i > 0) {
                createStmt.append(",").append(this.getContinuationString());
            }
            createStmt.append(indent).append(columns[i].getSQLDefinition());
        }
        if (this.supportsOption("PrimaryKeyInCreateStatements")) {
            final PrimaryKey pk = table.getPrimaryKey();
            if (pk != null && pk.size() > 0) {
                boolean includePk = true;
                if (this.supportsOption("AutoIncrementPkInCreateTableColumnDef")) {
                    for (final Column pkCol : pk.getColumns()) {
                        if (pkCol.isIdentity()) {
                            includePk = false;
                            break;
                        }
                    }
                }
                if (includePk) {
                    createStmt.append(",").append(this.getContinuationString());
                    if (pk.getName() != null) {
                        final String identifier = factory.getIdentifierInAdapterCase(pk.getName());
                        createStmt.append(indent).append("CONSTRAINT ").append(identifier).append(" ").append(pk.toString());
                    }
                    else {
                        createStmt.append(indent).append(pk.toString());
                    }
                }
            }
        }
        if (this.supportsOption("UniqueInEndCreateStatements")) {
            final StringBuffer uniqueConstraintStmt = new StringBuffer();
            for (int j = 0; j < columns.length; ++j) {
                if (columns[j].isUnique()) {
                    if (uniqueConstraintStmt.length() < 1) {
                        uniqueConstraintStmt.append(",").append(this.getContinuationString());
                        uniqueConstraintStmt.append(indent).append(" UNIQUE (");
                    }
                    else {
                        uniqueConstraintStmt.append(",");
                    }
                    uniqueConstraintStmt.append(columns[j].getIdentifier().toString());
                }
            }
            if (uniqueConstraintStmt.length() > 1) {
                uniqueConstraintStmt.append(")");
                createStmt.append(uniqueConstraintStmt.toString());
            }
        }
        if (this.supportsOption("FKInEndCreateStatements")) {
            final StringBuffer fkConstraintStmt = new StringBuffer();
            final ClassLoaderResolver clr = table.getStoreManager().getNucleusContext().getClassLoaderResolver(null);
            final List<ForeignKey> fks = table.getExpectedForeignKeys(clr);
            if (fks != null && !fks.isEmpty()) {
                for (final ForeignKey fk : fks) {
                    NucleusLogger.GENERAL.debug(">> TODO Add FK in CREATE TABLE as " + fk);
                }
            }
            if (fkConstraintStmt.length() > 1) {
                createStmt.append(fkConstraintStmt.toString());
            }
        }
        if (this.supportsOption("CheckInEndCreateStatements")) {
            final StringBuffer checkConstraintStmt = new StringBuffer();
            for (int j = 0; j < columns.length; ++j) {
                if (columns[j].getConstraints() != null) {
                    checkConstraintStmt.append(",").append(this.getContinuationString());
                    checkConstraintStmt.append(indent).append(columns[j].getConstraints());
                }
            }
            if (checkConstraintStmt.length() > 1) {
                createStmt.append(checkConstraintStmt.toString());
            }
        }
        createStmt.append(this.getContinuationString()).append(")");
        return createStmt.toString();
    }
    
    @Override
    public String getAddPrimaryKeyStatement(final PrimaryKey pk, final IdentifierFactory factory) {
        if (pk.getName() != null) {
            final String identifier = factory.getIdentifierInAdapterCase(pk.getName());
            return "ALTER TABLE " + pk.getTable().toString() + " ADD CONSTRAINT " + identifier + ' ' + pk;
        }
        return "ALTER TABLE " + pk.getTable().toString() + " ADD " + pk;
    }
    
    @Override
    public String getAddCandidateKeyStatement(final CandidateKey ck, final IdentifierFactory factory) {
        if (ck.getName() != null) {
            final String identifier = factory.getIdentifierInAdapterCase(ck.getName());
            return "ALTER TABLE " + ck.getTable().toString() + " ADD CONSTRAINT " + identifier + ' ' + ck;
        }
        return "ALTER TABLE " + ck.getTable().toString() + " ADD " + ck;
    }
    
    @Override
    public String getAddForeignKeyStatement(final ForeignKey fk, final IdentifierFactory factory) {
        if (fk.getName() != null) {
            final String identifier = factory.getIdentifierInAdapterCase(fk.getName());
            return "ALTER TABLE " + fk.getTable().toString() + " ADD CONSTRAINT " + identifier + ' ' + fk;
        }
        return "ALTER TABLE " + fk.getTable().toString() + " ADD " + fk;
    }
    
    @Override
    public String getAddColumnStatement(final Table table, final Column col) {
        return "ALTER TABLE " + table.toString() + " ADD " + col.getSQLDefinition();
    }
    
    @Override
    public String getCreateIndexStatement(final Index idx, final IdentifierFactory factory) {
        final DatastoreIdentifier indexIdentifier = factory.newTableIdentifier(idx.getName());
        return "CREATE " + (idx.getUnique() ? "UNIQUE " : "") + "INDEX " + indexIdentifier.getFullyQualifiedName(true) + " ON " + idx.getTable().toString() + ' ' + idx + ((idx.getExtendedIndexSettings() == null) ? "" : (" " + idx.getExtendedIndexSettings()));
    }
    
    @Override
    public String getCheckConstraintForValues(final DatastoreIdentifier identifier, final Object[] values, final boolean nullable) {
        final StringBuffer constraints = new StringBuffer("CHECK (");
        constraints.append(identifier);
        constraints.append(" IN (");
        for (int i = 0; i < values.length; ++i) {
            if (i > 0) {
                constraints.append(",");
            }
            if (values[i] instanceof String) {
                constraints.append("'").append(values[i]).append("'");
            }
            else {
                constraints.append(values[i]);
            }
        }
        constraints.append(")");
        if (nullable) {
            constraints.append(" OR " + identifier + " IS NULL");
        }
        constraints.append(")");
        return constraints.toString();
    }
    
    @Override
    public String getCreateDatabaseStatement(final String catalogName, final String schemaName) {
        return "CREATE SCHEMA " + schemaName;
    }
    
    @Override
    public String getDropDatabaseStatement(final String catalogName, final String schemaName) {
        return "DROP SCHEMA " + schemaName;
    }
    
    @Override
    public String getDropTableStatement(final Table table) {
        return "DROP TABLE " + table.toString() + " CASCADE";
    }
    
    @Override
    public String getDropViewStatement(final ViewImpl view) {
        return "DROP VIEW " + view.toString();
    }
    
    @Override
    public String getDeleteTableStatement(final SQLTable tbl) {
        return "DELETE FROM " + tbl.toString();
    }
    
    @Override
    public SQLText getUpdateTableStatement(final SQLTable tbl, final SQLText setSQL) {
        final SQLText sql = new SQLText("UPDATE ");
        sql.append(tbl.toString());
        sql.append(" ").append(setSQL);
        return sql;
    }
    
    @Override
    public String getRangeByLimitEndOfStatementClause(final long offset, final long count) {
        return "";
    }
    
    @Override
    public String getRangeByRowNumberColumn() {
        return "";
    }
    
    @Override
    public String getRangeByRowNumberColumn2() {
        return "";
    }
    
    @Override
    public ResultSet getColumns(final Connection conn, final String catalog, final String schema, final String table, final String columnNamePattern) throws SQLException {
        final DatabaseMetaData dmd = conn.getMetaData();
        return dmd.getColumns(catalog, schema, table, columnNamePattern);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("================ DatabaseAdapter ==================");
        sb.append("\n");
        sb.append("Adapter : " + this.getClass().getName());
        sb.append("\n");
        sb.append("Datastore : name=\"" + this.datastoreProductName + "\" version=\"" + this.datastoreProductVersion + "\" (major=" + this.datastoreMajorVersion + ", minor=" + this.datastoreMinorVersion + ", revision=" + this.datastoreRevisionVersion + ")");
        sb.append("\n");
        sb.append("Driver : name=\"" + this.driverName + "\" version=\"" + this.driverVersion + "\" (major=" + this.driverMajorVersion + ", minor=" + this.driverMinorVersion + ")");
        sb.append("\n");
        sb.append("===================================================");
        return sb.toString();
    }
    
    @Override
    public String getDatastoreDateStatement() {
        return "SELECT CURRENT_TIMESTAMP";
    }
    
    @Override
    public String getPatternExpressionAnyCharacter() {
        return "_";
    }
    
    @Override
    public String getPatternExpressionZeroMoreCharacters() {
        return "%";
    }
    
    @Override
    public String getEscapePatternExpression() {
        return "ESCAPE '\\'";
    }
    
    @Override
    public String getEscapeCharacter() {
        return "\\";
    }
    
    public String getContinuationString() {
        return "\n";
    }
    
    @Override
    public String getNumericConversionFunction() {
        return "ASCII";
    }
    
    public String getOperatorConcat() {
        return "||";
    }
    
    @Override
    public boolean isStatementCancel(final SQLException sqle) {
        return false;
    }
    
    @Override
    public boolean isStatementTimeout(final SQLException sqle) {
        return false;
    }
    
    static {
        LOCALISER_BASE = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
