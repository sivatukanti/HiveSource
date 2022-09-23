// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.rdbms.schema.ForeignKeyInfo;
import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import java.util.Iterator;
import org.datanucleus.store.rdbms.table.ViewImpl;
import org.datanucleus.store.rdbms.key.ForeignKey;
import org.datanucleus.store.rdbms.key.PrimaryKey;
import java.util.Properties;
import org.datanucleus.store.rdbms.table.TableImpl;
import org.datanucleus.store.rdbms.key.Index;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.key.CandidateKey;
import org.datanucleus.store.rdbms.sql.SQLText;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Table;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import java.sql.Timestamp;
import java.util.Map;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.StoreSchemaHandler;
import org.datanucleus.store.rdbms.mapping.MappingManager;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.Collection;

public interface DatastoreAdapter
{
    public static final String IDENTITY_COLUMNS = "IdentityColumns";
    public static final String SEQUENCES = "Sequences";
    public static final String BIT_IS_REALLY_BOOLEAN = "BitIsReallyBoolean";
    public static final String BOOLEAN_COMPARISON = "BooleanExpression";
    public static final String ESCAPE_EXPRESSION_IN_LIKE_PREDICATE = "EscapeExpressionInLikePredicate";
    public static final String PROJECTION_IN_TABLE_REFERENCE_JOINS = "ProjectionInTableReferenceJoins";
    public static final String ANALYSIS_METHODS = "AnalysisMethods";
    public static final String CATALOGS_IN_TABLE_DEFINITIONS = "CatalogInTableDefinition";
    public static final String SCHEMAS_IN_TABLE_DEFINITIONS = "SchemaInTableDefinition";
    public static final String IDENTIFIERS_LOWERCASE = "LowerCaseIdentifiers";
    public static final String IDENTIFIERS_MIXEDCASE = "MixedCaseIdentifiers";
    public static final String IDENTIFIERS_UPPERCASE = "UpperCaseIdentifiers";
    public static final String IDENTIFIERS_LOWERCASE_QUOTED = "LowerCaseQuotedIdentifiers";
    public static final String IDENTIFIERS_MIXEDCASE_QUOTED = "MixedCaseQuotedIdentifiers";
    public static final String IDENTIFIERS_UPPERCASE_QUOTED = "UpperCaseQuotedIdentifiers";
    public static final String IDENTIFIERS_MIXEDCASE_SENSITIVE = "MixedCaseSensitiveIdentifiers";
    public static final String IDENTIFIERS_MIXEDCASE_QUOTED_SENSITIVE = "MixedCaseQuotedSensitiveIdentifiers";
    public static final String VIEWS = "Views";
    public static final String UNION_SYNTAX = "Union_Syntax";
    public static final String USE_UNION_ALL = "UseUnionAll";
    public static final String EXISTS_SYNTAX = "Exists_Syntax";
    public static final String ALTER_TABLE_DROP_CONSTRAINT_SYNTAX = "AlterTableDropConstraint_Syntax";
    public static final String ALTER_TABLE_DROP_FOREIGN_KEY_CONSTRAINT = "AlterTableDropForeignKey_Syntax";
    public static final String DEFERRED_CONSTRAINTS = "DeferredConstraints";
    public static final String DISTINCT_WITH_SELECT_FOR_UPDATE = "DistinctWithSelectForUpdate";
    public static final String ALLOW_TABLE_ALIAS_IN_UPDATE_SET_CLAUSE = "TableAliasInUpdateSet";
    public static final String PERSIST_OF_UNASSIGNED_CHAR = "PersistOfUnassignedChar";
    public static final String CHAR_COLUMNS_PADDED_WITH_SPACES = "CharColumnsPaddedWithSpaces";
    public static final String NULL_EQUALS_EMPTY_STRING = "NullEqualsEmptyString";
    public static final String STATEMENT_BATCHING = "StatementBatching";
    public static final String CHECK_IN_CREATE_STATEMENTS = "CheckInCreateStatements";
    public static final String CHECK_IN_END_CREATE_STATEMENTS = "CheckInEndCreateStatements";
    public static final String UNIQUE_IN_END_CREATE_STATEMENTS = "UniqueInEndCreateStatements";
    public static final String FK_IN_END_CREATE_STATEMENTS = "FKInEndCreateStatements";
    public static final String PRIMARYKEY_IN_CREATE_STATEMENTS = "PrimaryKeyInCreateStatements";
    public static final String GET_GENERATED_KEYS_STATEMENT = "GetGeneratedKeysStatement";
    public static final String NULLS_IN_CANDIDATE_KEYS = "NullsInCandidateKeys";
    public static final String NULLS_KEYWORD_IN_COLUMN_OPTIONS = "ColumnOptions_NullsKeyword";
    public static final String DEFAULT_KEYWORD_IN_COLUMN_OPTIONS = "ColumnOptions_DefaultKeyword";
    public static final String DEFAULT_KEYWORD_WITH_NOT_NULL_IN_COLUMN_OPTIONS = "ColumnOptions_DefaultWithNotNull";
    public static final String DEFAULT_BEFORE_NULL_IN_COLUMN_OPTIONS = "ColumnOptions_DefaultBeforeNull";
    public static final String ANSI_JOIN_SYNTAX = "ANSI_Join_Syntax";
    public static final String ANSI_CROSSJOIN_SYNTAX = "ANSI_CrossJoin_Syntax";
    public static final String CROSSJOIN_ASINNER11_SYNTAX = "ANSI_CrossJoinAsInner11_Syntax";
    public static final String AUTO_INCREMENT_KEYS_NULL_SPECIFICATION = "AutoIncrementNullSpecification";
    public static final String AUTO_INCREMENT_COLUMN_TYPE_SPECIFICATION = "AutoIncrementColumnTypeSpecification";
    public static final String AUTO_INCREMENT_PK_IN_CREATE_TABLE_COLUMN_DEF = "AutoIncrementPkInCreateTableColumnDef";
    public static final String LOCK_WITH_SELECT_FOR_UPDATE = "LockWithSelectForUpdate";
    public static final String LOCK_OPTION_PLACED_AFTER_FROM = "LockOptionAfterFromClause";
    public static final String LOCK_OPTION_PLACED_WITHIN_JOIN = "LockOptionWithinJoinClause";
    public static final String BLOB_SET_USING_SETSTRING = "BlobSetUsingSetString";
    public static final String CLOB_SET_USING_SETSTRING = "ClobSetUsingSetString";
    public static final String CREATE_INDEXES_BEFORE_FOREIGN_KEYS = "CreateIndexesBeforeForeignKeys";
    public static final String INCLUDE_ORDERBY_COLS_IN_SELECT = "IncludeOrderByColumnsInSelect";
    public static final String DATETIME_STORES_MILLISECS = "DateTimeStoresMillisecs";
    public static final String ACCESS_PARENTQUERY_IN_SUBQUERY_JOINED = "AccessParentQueryInSubquery";
    public static final String ORDERBY_USING_SELECT_COLUMN_INDEX = "OrderByUsingSelectColumnIndex";
    public static final String ORDERBY_NULLS_DIRECTIVES = "OrderByWithNullsDirectives";
    public static final String STORED_PROCEDURES = "StoredProcs";
    public static final String FK_UPDATE_ACTION_CASCADE = "FkUpdateActionCascade";
    public static final String FK_UPDATE_ACTION_DEFAULT = "FkUpdateActionDefault";
    public static final String FK_UPDATE_ACTION_NULL = "FkUpdateActionNull";
    public static final String FK_UPDATE_ACTION_RESTRICT = "FkUpdateActionRestrict";
    public static final String FK_DELETE_ACTION_CASCADE = "FkDeleteActionCascade";
    public static final String FK_DELETE_ACTION_DEFAULT = "FkDeleteActionDefault";
    public static final String FK_DELETE_ACTION_NULL = "FkDeleteActionNull";
    public static final String FK_DELETE_ACTION_RESTRICT = "FkDeleteActionRestrict";
    public static final String TX_ISOLATION_NONE = "TxIsolationNone";
    public static final String TX_ISOLATION_READ_COMMITTED = "TxIsolationReadCommitted";
    public static final String TX_ISOLATION_READ_UNCOMMITTED = "TxIsolationReadUncommitted";
    public static final String TX_ISOLATION_REPEATABLE_READ = "TxIsolationReadRepeatableRead";
    public static final String TX_ISOLATION_SERIALIZABLE = "TxIsolationSerializable";
    
    Collection<String> getSupportedOptions();
    
    boolean supportsOption(final String p0);
    
    MappingManager getMappingManager(final RDBMSStoreManager p0);
    
    String getVendorID();
    
    void initialiseTypes(final StoreSchemaHandler p0, final ManagedConnection p1);
    
    void setProperties(final Map<String, Object> p0);
    
    void removeUnsupportedMappings(final StoreSchemaHandler p0, final ManagedConnection p1);
    
    boolean isReservedKeyword(final String p0);
    
    void initialiseDatastore(final Object p0);
    
    String getIdentifierQuoteString();
    
    String getCatalogSeparator();
    
    long getAdapterTime(final Timestamp p0);
    
    String getDatastoreProductName();
    
    String getDatastoreProductVersion();
    
    String getDatastoreDriverName();
    
    String getDatastoreDriverVersion();
    
    int getDriverMajorVersion();
    
    int getDriverMinorVersion();
    
    boolean isIdentityFieldDataType(final String p0);
    
    int getDatastoreIdentifierMaxLength(final IdentifierType p0);
    
    int getMaxForeignKeys();
    
    int getMaxIndexes();
    
    boolean supportsQueryFetchSize(final int p0);
    
    String toString();
    
    boolean supportsTransactionIsolation(final int p0);
    
    String getRangeByLimitEndOfStatementClause(final long p0, final long p1);
    
    String getRangeByRowNumberColumn();
    
    String getRangeByRowNumberColumn2();
    
    ResultSet getColumns(final Connection p0, final String p1, final String p2, final String p3, final String p4) throws SQLException;
    
    String getInsertStatementForNoColumns(final Table p0);
    
    int getUnlimitedLengthPrecisionValue(final SQLTypeInfo p0);
    
    String getAutoIncrementStmt(final Table p0, final String p1);
    
    String getAutoIncrementKeyword();
    
    String getCreateDatabaseStatement(final String p0, final String p1);
    
    String getDropDatabaseStatement(final String p0, final String p1);
    
    String getDropTableStatement(final Table p0);
    
    String getDeleteTableStatement(final SQLTable p0);
    
    SQLText getUpdateTableStatement(final SQLTable p0, final SQLText p1);
    
    String getAddCandidateKeyStatement(final CandidateKey p0, final IdentifierFactory p1);
    
    boolean isValidPrimaryKeyType(final int p0);
    
    String getAddColumnStatement(final Table p0, final Column p1);
    
    String getCreateIndexStatement(final Index p0, final IdentifierFactory p1);
    
    ResultSet getExistingIndexes(final Connection p0, final String p1, final String p2, final String p3) throws SQLException;
    
    String getCreateTableStatement(final TableImpl p0, final Column[] p1, final Properties p2, final IdentifierFactory p3);
    
    String getAddPrimaryKeyStatement(final PrimaryKey p0, final IdentifierFactory p1);
    
    String getAddForeignKeyStatement(final ForeignKey p0, final IdentifierFactory p1);
    
    String getDropViewStatement(final ViewImpl p0);
    
    String getSelectForUpdateText();
    
    String getSurrogateForEmptyStrings();
    
    int getTransactionIsolationForSchemaCreation();
    
    int getRequiredTransactionIsolationLevel();
    
    String getCatalogName(final Connection p0) throws SQLException;
    
    String getSchemaName(final Connection p0) throws SQLException;
    
    String getSelectWithLockOption();
    
    String getSelectNewUUIDStmt();
    
    String getSequenceNextStmt(final String p0);
    
    String getSequenceCreateStmt(final String p0, final Integer p1, final Integer p2, final Integer p3, final Integer p4, final Integer p5);
    
    Iterator iteratorReservedWords();
    
    String getDatastoreDateStatement();
    
    String getCheckConstraintForValues(final DatastoreIdentifier p0, final Object[] p1, final boolean p2);
    
    SQLTypeInfo newSQLTypeInfo(final ResultSet p0);
    
    RDBMSColumnInfo newRDBMSColumnInfo(final ResultSet p0);
    
    ForeignKeyInfo newFKInfo(final ResultSet p0);
    
    String getOrderString(final StoreManager p0, final String p1, final SQLExpression p2);
    
    boolean validToSelectMappingInStatement(final SQLStatement p0, final JavaTypeMapping p1);
    
    boolean isStatementCancel(final SQLException p0);
    
    boolean isStatementTimeout(final SQLException p0);
    
    String getNumericConversionFunction();
    
    String getEscapePatternExpression();
    
    String getEscapeCharacter();
    
    String getPatternExpressionAnyCharacter();
    
    String getPatternExpressionZeroMoreCharacters();
}
