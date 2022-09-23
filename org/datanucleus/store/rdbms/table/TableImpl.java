// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.Properties;
import org.datanucleus.store.rdbms.key.CandidateKey;
import org.datanucleus.store.rdbms.schema.IndexInfo;
import org.datanucleus.store.rdbms.schema.RDBMSTableIndexInfo;
import org.datanucleus.store.rdbms.exceptions.UnexpectedColumnException;
import org.datanucleus.store.rdbms.schema.PrimaryKeyInfo;
import org.datanucleus.store.rdbms.schema.RDBMSTablePKInfo;
import org.datanucleus.store.rdbms.exceptions.NoTableManagedException;
import org.datanucleus.store.rdbms.key.ForeignKey;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.schema.StoreSchemaHandler;
import org.datanucleus.store.rdbms.schema.ForeignKeyInfo;
import org.datanucleus.store.rdbms.schema.RDBMSTableFKInfo;
import java.util.HashSet;
import org.datanucleus.store.rdbms.key.Index;
import java.sql.Statement;
import java.util.Collections;
import org.datanucleus.util.StringUtils;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import java.util.Set;
import java.util.List;
import org.datanucleus.store.rdbms.exceptions.MissingColumnException;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;
import org.datanucleus.store.rdbms.exceptions.WrongPrimaryKeyException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.exceptions.NotATableException;
import org.datanucleus.store.rdbms.exceptions.MissingTableException;
import org.datanucleus.store.rdbms.schema.RDBMSSchemaHandler;
import java.util.Collection;
import java.sql.Connection;
import java.util.Iterator;
import org.datanucleus.store.rdbms.key.PrimaryKey;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;

public abstract class TableImpl extends AbstractTable
{
    public TableImpl(final DatastoreIdentifier name, final RDBMSStoreManager storeMgr) {
        super(name, storeMgr);
    }
    
    @Override
    public void preInitialize(final ClassLoaderResolver clr) {
        this.assertIsUninitialized();
    }
    
    @Override
    public void postInitialize(final ClassLoaderResolver clr) {
        this.assertIsInitialized();
    }
    
    public PrimaryKey getPrimaryKey() {
        final PrimaryKey pk = new PrimaryKey(this);
        for (final Column col : this.columns) {
            if (col.isPrimaryKey()) {
                pk.addColumn(col);
            }
        }
        return pk;
    }
    
    @Override
    public boolean validate(final Connection conn, final boolean validateColumnStructure, final boolean autoCreate, final Collection autoCreateErrors) throws SQLException {
        this.assertIsInitialized();
        final RDBMSSchemaHandler handler = (RDBMSSchemaHandler)this.storeMgr.getSchemaHandler();
        final String tableType = handler.getTableType(conn, this);
        if (tableType == null) {
            throw new MissingTableException(this.getCatalogName(), this.getSchemaName(), this.toString());
        }
        if (!tableType.equals("TABLE")) {
            throw new NotATableException(this.toString(), tableType);
        }
        final long startTime = System.currentTimeMillis();
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(TableImpl.LOCALISER.msg("057032", this));
        }
        this.validateColumns(conn, validateColumnStructure, autoCreate, autoCreateErrors);
        try {
            this.validatePrimaryKey(conn);
        }
        catch (WrongPrimaryKeyException wpke) {
            if (autoCreateErrors == null) {
                throw wpke;
            }
            autoCreateErrors.add(wpke);
        }
        this.state = 4;
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(TableImpl.LOCALISER.msg("045000", System.currentTimeMillis() - startTime));
        }
        return false;
    }
    
    public boolean validateColumns(final Connection conn, final boolean validateColumnStructure, final boolean autoCreate, final Collection autoCreateErrors) throws SQLException {
        final HashMap unvalidated = new HashMap((Map<? extends K, ? extends V>)this.columnsByName);
        final List tableColInfo = this.storeMgr.getColumnInfoForTable(this, conn);
        for (final RDBMSColumnInfo ci : tableColInfo) {
            final DatastoreIdentifier colName = this.storeMgr.getIdentifierFactory().newColumnIdentifier(ci.getColumnName(), this.storeMgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(String.class), -1);
            final Column col = unvalidated.get(colName);
            if (col != null) {
                if (validateColumnStructure) {
                    col.initializeColumnInfoFromDatastore(ci);
                    col.validate(ci);
                    unvalidated.remove(colName);
                }
                else {
                    unvalidated.remove(colName);
                }
            }
        }
        if (unvalidated.size() > 0) {
            if (autoCreate) {
                final List stmts = new ArrayList();
                final Set columnKeys = unvalidated.entrySet();
                for (final Map.Entry entry : columnKeys) {
                    final Column col2 = entry.getValue();
                    final String addColStmt = this.dba.getAddColumnStatement(this, col2);
                    stmts.add(addColStmt);
                    NucleusLogger.DATASTORE_SCHEMA.debug(TableImpl.LOCALISER.msg("057031", col2.getIdentifier(), this.toString()));
                }
                try {
                    this.executeDdlStatementList(stmts, conn);
                }
                catch (SQLException sqle) {
                    if (autoCreateErrors == null) {
                        throw sqle;
                    }
                    autoCreateErrors.add(sqle);
                }
                this.storeMgr.invalidateColumnInfoForTable(this);
            }
            else {
                final MissingColumnException mce = new MissingColumnException(this, unvalidated.values());
                if (autoCreateErrors == null) {
                    throw mce;
                }
                autoCreateErrors.add(mce);
            }
        }
        this.state = 4;
        return true;
    }
    
    public void initializeColumnInfoForPrimaryKeyColumns(final Connection conn) throws SQLException {
        for (final Column col : this.columnsByName.values()) {
            if (col.isPrimaryKey()) {
                final RDBMSColumnInfo ci = this.storeMgr.getColumnInfoForColumnName(this, conn, col.getIdentifier());
                if (ci == null) {
                    continue;
                }
                col.initializeColumnInfoFromDatastore(ci);
            }
        }
    }
    
    public void initializeColumnInfoFromDatastore(final Connection conn) throws SQLException {
        final HashMap columns = new HashMap((Map<? extends K, ? extends V>)this.columnsByName);
        for (final RDBMSColumnInfo ci : this.storeMgr.getColumnInfoForTable(this, conn)) {
            final DatastoreIdentifier colName = this.storeMgr.getIdentifierFactory().newIdentifier(IdentifierType.COLUMN, ci.getColumnName());
            final Column col = columns.get(colName);
            if (col != null) {
                col.initializeColumnInfoFromDatastore(ci);
            }
        }
    }
    
    protected boolean validatePrimaryKey(final Connection conn) throws SQLException {
        final Map actualPKs = this.getExistingPrimaryKeys(conn);
        final PrimaryKey expectedPK = this.getPrimaryKey();
        if (expectedPK.size() == 0) {
            if (!actualPKs.isEmpty()) {
                throw new WrongPrimaryKeyException(this.toString(), expectedPK.toString(), StringUtils.collectionToString(actualPKs.values()));
            }
        }
        else if (actualPKs.size() != 1 || !actualPKs.values().contains(expectedPK)) {
            throw new WrongPrimaryKeyException(this.toString(), expectedPK.toString(), StringUtils.collectionToString(actualPKs.values()));
        }
        return true;
    }
    
    public boolean validateConstraints(final Connection conn, final boolean autoCreate, final Collection autoCreateErrors, final ClassLoaderResolver clr) throws SQLException {
        this.assertIsInitialized();
        boolean idxsWereModified;
        boolean fksWereModified;
        boolean cksWereModified;
        if (this.dba.supportsOption("CreateIndexesBeforeForeignKeys")) {
            idxsWereModified = this.validateIndices(conn, autoCreate, autoCreateErrors, clr);
            fksWereModified = this.validateForeignKeys(conn, autoCreate, autoCreateErrors, clr);
            cksWereModified = this.validateCandidateKeys(conn, autoCreate, autoCreateErrors);
        }
        else {
            cksWereModified = this.validateCandidateKeys(conn, autoCreate, autoCreateErrors);
            fksWereModified = this.validateForeignKeys(conn, autoCreate, autoCreateErrors, clr);
            idxsWereModified = this.validateIndices(conn, autoCreate, autoCreateErrors, clr);
        }
        return fksWereModified || idxsWereModified || cksWereModified;
    }
    
    public boolean createConstraints(final Connection conn, final Collection autoCreateErrors, final ClassLoaderResolver clr) throws SQLException {
        this.assertIsInitialized();
        boolean idxsWereModified;
        boolean fksWereModified;
        boolean cksWereModified;
        if (this.dba.supportsOption("CreateIndexesBeforeForeignKeys")) {
            idxsWereModified = this.createIndices(conn, autoCreateErrors, clr, Collections.EMPTY_MAP);
            fksWereModified = this.createForeignKeys(conn, autoCreateErrors, clr, Collections.EMPTY_MAP);
            cksWereModified = this.createCandidateKeys(conn, autoCreateErrors, Collections.EMPTY_MAP);
        }
        else {
            cksWereModified = this.createCandidateKeys(conn, autoCreateErrors, Collections.EMPTY_MAP);
            fksWereModified = this.createForeignKeys(conn, autoCreateErrors, clr, Collections.EMPTY_MAP);
            idxsWereModified = this.createIndices(conn, autoCreateErrors, clr, Collections.EMPTY_MAP);
        }
        return fksWereModified || idxsWereModified || cksWereModified;
    }
    
    private boolean validateForeignKeys(final Connection conn, final boolean autoCreate, final Collection autoCreateErrors, final ClassLoaderResolver clr) throws SQLException {
        boolean dbWasModified = false;
        Map actualForeignKeysByName = null;
        int numActualFKs = 0;
        if (this.storeMgr.getCompleteDDL()) {
            actualForeignKeysByName = new HashMap();
        }
        else {
            actualForeignKeysByName = this.getExistingForeignKeys(conn);
            numActualFKs = actualForeignKeysByName.size();
            if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                NucleusLogger.DATASTORE_SCHEMA.debug(TableImpl.LOCALISER.msg("058103", "" + numActualFKs, this));
            }
        }
        if (autoCreate) {
            dbWasModified = this.createForeignKeys(conn, autoCreateErrors, clr, actualForeignKeysByName);
        }
        else {
            final Map stmtsByFKName = this.getSQLAddFKStatements(actualForeignKeysByName, clr);
            if (stmtsByFKName.isEmpty()) {
                if (numActualFKs > 0 && NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                    NucleusLogger.DATASTORE_SCHEMA.debug(TableImpl.LOCALISER.msg("058104", "" + numActualFKs, this));
                }
            }
            else {
                NucleusLogger.DATASTORE_SCHEMA.warn(TableImpl.LOCALISER.msg("058101", this, stmtsByFKName.values()));
            }
        }
        return dbWasModified;
    }
    
    private boolean createForeignKeys(final Connection conn, final Collection autoCreateErrors, final ClassLoaderResolver clr, final Map actualForeignKeysByName) throws SQLException {
        final Map stmtsByFKName = this.getSQLAddFKStatements(actualForeignKeysByName, clr);
        final Statement stmt = conn.createStatement();
        try {
            for (final Map.Entry e : stmtsByFKName.entrySet()) {
                final String fkName = e.getKey();
                final String stmtText = e.getValue();
                if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                    NucleusLogger.DATASTORE_SCHEMA.debug(TableImpl.LOCALISER.msg("058100", fkName, this.getCatalogName(), this.getSchemaName()));
                }
                try {
                    this.executeDdlStatement(stmt, stmtText);
                }
                catch (SQLException sqle) {
                    if (autoCreateErrors == null) {
                        throw sqle;
                    }
                    autoCreateErrors.add(sqle);
                }
            }
        }
        finally {
            stmt.close();
        }
        return !stmtsByFKName.isEmpty();
    }
    
    private boolean validateIndices(final Connection conn, final boolean autoCreate, final Collection autoCreateErrors, final ClassLoaderResolver clr) throws SQLException {
        boolean dbWasModified = false;
        Map actualIndicesByName = null;
        int numActualIdxs = 0;
        if (this.storeMgr.getCompleteDDL()) {
            actualIndicesByName = new HashMap();
        }
        else {
            actualIndicesByName = this.getExistingIndices(conn);
            for (final Map.Entry entry : actualIndicesByName.entrySet()) {
                final Index idx = entry.getValue();
                if (idx.getTable().getIdentifier().toString().equals(this.identifier.toString())) {
                    ++numActualIdxs;
                }
            }
            if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                NucleusLogger.DATASTORE_SCHEMA.debug(TableImpl.LOCALISER.msg("058004", "" + numActualIdxs, this));
            }
        }
        if (autoCreate) {
            dbWasModified = this.createIndices(conn, autoCreateErrors, clr, actualIndicesByName);
        }
        else {
            final Map stmtsByIdxName = this.getSQLCreateIndexStatements(actualIndicesByName, clr);
            if (stmtsByIdxName.isEmpty()) {
                if (numActualIdxs > 0 && NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                    NucleusLogger.DATASTORE_SCHEMA.debug(TableImpl.LOCALISER.msg("058005", "" + numActualIdxs, this));
                }
            }
            else {
                NucleusLogger.DATASTORE_SCHEMA.warn(TableImpl.LOCALISER.msg("058003", this, stmtsByIdxName.values()));
            }
        }
        return dbWasModified;
    }
    
    private boolean createIndices(final Connection conn, final Collection autoCreateErrors, final ClassLoaderResolver clr, final Map actualIndicesByName) throws SQLException {
        final Map stmtsByIdxName = this.getSQLCreateIndexStatements(actualIndicesByName, clr);
        final Statement stmt = conn.createStatement();
        try {
            for (final Map.Entry e : stmtsByIdxName.entrySet()) {
                final String idxName = e.getKey();
                final String stmtText = e.getValue();
                if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                    NucleusLogger.DATASTORE_SCHEMA.debug(TableImpl.LOCALISER.msg("058000", idxName, this.getCatalogName(), this.getSchemaName()));
                }
                try {
                    this.executeDdlStatement(stmt, stmtText);
                }
                catch (SQLException sqle) {
                    if (autoCreateErrors == null) {
                        throw sqle;
                    }
                    autoCreateErrors.add(sqle);
                }
            }
        }
        finally {
            stmt.close();
        }
        return !stmtsByIdxName.isEmpty();
    }
    
    private boolean validateCandidateKeys(final Connection conn, final boolean autoCreate, final Collection autoCreateErrors) throws SQLException {
        boolean dbWasModified = false;
        Map actualCandidateKeysByName = null;
        int numActualCKs = 0;
        if (this.storeMgr.getCompleteDDL()) {
            actualCandidateKeysByName = new HashMap();
        }
        else {
            actualCandidateKeysByName = this.getExistingCandidateKeys(conn);
            numActualCKs = actualCandidateKeysByName.size();
            if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                NucleusLogger.DATASTORE_SCHEMA.debug(TableImpl.LOCALISER.msg("058204", "" + numActualCKs, this));
            }
        }
        if (autoCreate) {
            dbWasModified = this.createCandidateKeys(conn, autoCreateErrors, actualCandidateKeysByName);
        }
        else {
            final Map stmtsByCKName = this.getSQLAddCandidateKeyStatements(actualCandidateKeysByName);
            if (stmtsByCKName.isEmpty()) {
                if (numActualCKs > 0 && NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                    NucleusLogger.DATASTORE_SCHEMA.debug(TableImpl.LOCALISER.msg("058205", "" + numActualCKs, this));
                }
            }
            else {
                NucleusLogger.DATASTORE_SCHEMA.warn(TableImpl.LOCALISER.msg("058201", this, stmtsByCKName.values()));
            }
        }
        return dbWasModified;
    }
    
    private boolean createCandidateKeys(final Connection conn, final Collection autoCreateErrors, final Map actualCandidateKeysByName) throws SQLException {
        final Map stmtsByCKName = this.getSQLAddCandidateKeyStatements(actualCandidateKeysByName);
        final Statement stmt = conn.createStatement();
        try {
            for (final Map.Entry e : stmtsByCKName.entrySet()) {
                final String ckName = e.getKey();
                final String stmtText = e.getValue();
                if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                    NucleusLogger.DATASTORE_SCHEMA.debug(TableImpl.LOCALISER.msg("058200", ckName, this.getCatalogName(), this.getSchemaName()));
                }
                try {
                    this.executeDdlStatement(stmt, stmtText);
                }
                catch (SQLException sqle) {
                    if (autoCreateErrors == null) {
                        throw sqle;
                    }
                    autoCreateErrors.add(sqle);
                }
            }
        }
        finally {
            stmt.close();
        }
        return !stmtsByCKName.isEmpty();
    }
    
    public void dropConstraints(final Connection conn) throws SQLException {
        this.assertIsInitialized();
        final boolean drop_using_constraint = this.dba.supportsOption("AlterTableDropConstraint_Syntax");
        final boolean drop_using_foreign_key = this.dba.supportsOption("AlterTableDropForeignKey_Syntax");
        if (!drop_using_constraint && !drop_using_foreign_key) {
            return;
        }
        final HashSet fkNames = new HashSet();
        final StoreSchemaHandler handler = this.storeMgr.getSchemaHandler();
        final RDBMSTableFKInfo fkInfo = (RDBMSTableFKInfo)handler.getSchemaData(conn, "foreign-keys", new Object[] { this });
        for (final ForeignKeyInfo fki : fkInfo.getChildren()) {
            final String fkName = (String)fki.getProperty("fk_name");
            if (fkName != null) {
                fkNames.add(fkName);
            }
        }
        final int numFKs = fkNames.size();
        if (numFKs > 0) {
            if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                NucleusLogger.DATASTORE_SCHEMA.debug(TableImpl.LOCALISER.msg("058102", "" + numFKs, this));
            }
            final Iterator iter = fkNames.iterator();
            final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
            final Statement stmt = conn.createStatement();
            try {
                while (iter.hasNext()) {
                    final String constraintName = iter.next();
                    String stmtText = null;
                    if (drop_using_constraint) {
                        stmtText = "ALTER TABLE " + this.toString() + " DROP CONSTRAINT " + idFactory.getIdentifierInAdapterCase(constraintName);
                    }
                    else {
                        stmtText = "ALTER TABLE " + this.toString() + " DROP FOREIGN KEY " + idFactory.getIdentifierInAdapterCase(constraintName);
                    }
                    this.executeDdlStatement(stmt, stmtText);
                }
            }
            finally {
                stmt.close();
            }
        }
    }
    
    public List<ForeignKey> getExpectedForeignKeys(final ClassLoaderResolver clr) {
        this.assertIsInitialized();
        final Set colsInFKs = new HashSet();
        final ArrayList foreignKeys = new ArrayList();
        for (final Column col : this.columns) {
            if (!colsInFKs.contains(col)) {
                try {
                    final DatastoreClass referencedTable = this.storeMgr.getDatastoreClass(col.getStoredJavaType(), clr);
                    if (referencedTable == null) {
                        continue;
                    }
                    for (int j = 0; j < col.getJavaTypeMapping().getNumberOfDatastoreMappings(); ++j) {
                        colsInFKs.add(col.getJavaTypeMapping().getDatastoreMapping(j).getColumn());
                    }
                    final ForeignKey fk = new ForeignKey(col.getJavaTypeMapping(), this.dba, referencedTable, true);
                    foreignKeys.add(fk);
                }
                catch (NoTableManagedException ex) {}
            }
        }
        return (List<ForeignKey>)foreignKeys;
    }
    
    protected List getExpectedCandidateKeys() {
        this.assertIsInitialized();
        final ArrayList candidateKeys = new ArrayList();
        return candidateKeys;
    }
    
    protected Set getExpectedIndices(final ClassLoaderResolver clr) {
        this.assertIsInitialized();
        final HashSet indices = new HashSet();
        final PrimaryKey pk = this.getPrimaryKey();
        for (final ForeignKey fk : this.getExpectedForeignKeys(clr)) {
            if (!pk.getColumnList().equals(fk.getColumnList())) {
                indices.add(new Index(fk));
            }
        }
        return indices;
    }
    
    private Map getExistingPrimaryKeys(final Connection conn) throws SQLException {
        final HashMap primaryKeysByName = new HashMap();
        if (this.tableExistsInDatastore(conn)) {
            final StoreSchemaHandler handler = this.storeMgr.getSchemaHandler();
            final RDBMSTablePKInfo tablePkInfo = (RDBMSTablePKInfo)handler.getSchemaData(conn, "primary-keys", new Object[] { this });
            final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
            for (final PrimaryKeyInfo pkInfo : tablePkInfo.getChildren()) {
                final String pkName = (String)pkInfo.getProperty("pk_name");
                DatastoreIdentifier pkIdentifier;
                if (pkName == null) {
                    pkIdentifier = idFactory.newPrimaryKeyIdentifier(this);
                }
                else {
                    pkIdentifier = idFactory.newIdentifier(IdentifierType.COLUMN, pkName);
                }
                PrimaryKey pk = primaryKeysByName.get(pkIdentifier);
                if (pk == null) {
                    pk = new PrimaryKey(this);
                    pk.setName(pkIdentifier.getIdentifierName());
                    primaryKeysByName.put(pkIdentifier, pk);
                }
                final int keySeq = (short)pkInfo.getProperty("key_seq") - 1;
                final String colName = (String)pkInfo.getProperty("column_name");
                final DatastoreIdentifier colIdentifier = idFactory.newIdentifier(IdentifierType.COLUMN, colName);
                final Column col = this.columnsByName.get(colIdentifier);
                if (col == null) {
                    throw new UnexpectedColumnException(this.toString(), colIdentifier.getIdentifierName(), this.getSchemaName(), this.getCatalogName());
                }
                pk.setColumn(keySeq, col);
            }
        }
        return primaryKeysByName;
    }
    
    private Map getExistingForeignKeys(final Connection conn) throws SQLException {
        final HashMap foreignKeysByName = new HashMap();
        if (this.tableExistsInDatastore(conn)) {
            final StoreSchemaHandler handler = this.storeMgr.getSchemaHandler();
            final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
            final RDBMSTableFKInfo tableFkInfo = (RDBMSTableFKInfo)handler.getSchemaData(conn, "foreign-keys", new Object[] { this });
            for (final ForeignKeyInfo fkInfo : tableFkInfo.getChildren()) {
                final String fkName = (String)fkInfo.getProperty("fk_name");
                DatastoreIdentifier fkIdentifier;
                if (fkName == null) {
                    fkIdentifier = idFactory.newForeignKeyIdentifier(this, foreignKeysByName.size());
                }
                else {
                    fkIdentifier = idFactory.newIdentifier(IdentifierType.FOREIGN_KEY, fkName);
                }
                final short deferrability = (short)fkInfo.getProperty("deferrability");
                final boolean initiallyDeferred = deferrability == 5;
                ForeignKey fk = foreignKeysByName.get(fkIdentifier);
                if (fk == null) {
                    fk = new ForeignKey(initiallyDeferred);
                    fk.setName(fkIdentifier.getIdentifierName());
                    foreignKeysByName.put(fkIdentifier, fk);
                }
                final String pkTableName = (String)fkInfo.getProperty("pk_table_name");
                final DatastoreClass refTable = this.storeMgr.getDatastoreClass(idFactory.newTableIdentifier(pkTableName));
                if (refTable != null) {
                    final String fkColumnName = (String)fkInfo.getProperty("fk_column_name");
                    final String pkColumnName = (String)fkInfo.getProperty("pk_column_name");
                    final DatastoreIdentifier colName = idFactory.newIdentifier(IdentifierType.COLUMN, fkColumnName);
                    final DatastoreIdentifier refColName = idFactory.newIdentifier(IdentifierType.COLUMN, pkColumnName);
                    final Column col = this.columnsByName.get(colName);
                    final Column refCol = refTable.getColumn(refColName);
                    if (col == null || refCol == null) {
                        continue;
                    }
                    fk.addColumn(col, refCol);
                }
            }
        }
        return foreignKeysByName;
    }
    
    private Map getExistingCandidateKeys(final Connection conn) throws SQLException {
        final HashMap candidateKeysByName = new HashMap();
        if (this.tableExistsInDatastore(conn)) {
            final StoreSchemaHandler handler = this.storeMgr.getSchemaHandler();
            final RDBMSTableIndexInfo tableIndexInfo = (RDBMSTableIndexInfo)handler.getSchemaData(conn, "indices", new Object[] { this });
            final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
            for (final IndexInfo indexInfo : tableIndexInfo.getChildren()) {
                final boolean isUnique = !(boolean)indexInfo.getProperty("non_unique");
                if (isUnique) {
                    final short idxType = (short)indexInfo.getProperty("type");
                    if (idxType == 0) {
                        continue;
                    }
                    final String keyName = (String)indexInfo.getProperty("index_name");
                    final DatastoreIdentifier idxName = idFactory.newIdentifier(IdentifierType.CANDIDATE_KEY, keyName);
                    CandidateKey key = candidateKeysByName.get(idxName);
                    if (key == null) {
                        key = new CandidateKey(this);
                        key.setName(keyName);
                        candidateKeysByName.put(idxName, key);
                    }
                    final int colSeq = (short)indexInfo.getProperty("ordinal_position") - 1;
                    final DatastoreIdentifier colName = idFactory.newIdentifier(IdentifierType.COLUMN, (String)indexInfo.getProperty("column_name"));
                    final Column col = this.columnsByName.get(colName);
                    if (col == null) {
                        continue;
                    }
                    key.setColumn(colSeq, col);
                }
            }
        }
        return candidateKeysByName;
    }
    
    private Map getExistingIndices(final Connection conn) throws SQLException {
        final HashMap indicesByName = new HashMap();
        if (this.tableExistsInDatastore(conn)) {
            final StoreSchemaHandler handler = this.storeMgr.getSchemaHandler();
            final RDBMSTableIndexInfo tableIndexInfo = (RDBMSTableIndexInfo)handler.getSchemaData(conn, "indices", new Object[] { this });
            final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
            for (final IndexInfo indexInfo : tableIndexInfo.getChildren()) {
                final short idxType = (short)indexInfo.getProperty("type");
                if (idxType == 0) {
                    continue;
                }
                final String indexName = (String)indexInfo.getProperty("index_name");
                final DatastoreIdentifier indexIdentifier = idFactory.newIdentifier(IdentifierType.CANDIDATE_KEY, indexName);
                Index idx = indicesByName.get(indexIdentifier);
                if (idx == null) {
                    final boolean isUnique = !(boolean)indexInfo.getProperty("non_unique");
                    idx = new Index(this, isUnique, null);
                    idx.setName(indexName);
                    indicesByName.put(indexIdentifier, idx);
                }
                final int colSeq = (short)indexInfo.getProperty("ordinal_position") - 1;
                final DatastoreIdentifier colName = idFactory.newIdentifier(IdentifierType.COLUMN, (String)indexInfo.getProperty("column_name"));
                final Column col = this.columnsByName.get(colName);
                if (col == null) {
                    continue;
                }
                idx.setColumn(colSeq, col);
            }
        }
        return indicesByName;
    }
    
    @Override
    protected List getSQLCreateStatements(final Properties props) {
        this.assertIsInitialized();
        Column[] cols = null;
        for (final Column col : this.columns) {
            final ColumnMetaData colmd = col.getColumnMetaData();
            final Integer colPos = (colmd != null) ? colmd.getPosition() : null;
            if (colPos != null) {
                final int index = colPos;
                if (index >= this.columns.size() || index < 0) {
                    continue;
                }
                if (cols == null) {
                    cols = new Column[this.columns.size()];
                }
                if (cols[index] != null) {
                    throw new NucleusUserException("Column index " + index + " has been specified multiple times : " + cols[index] + " and " + col);
                }
                cols[index] = col;
            }
        }
        if (cols != null) {
            for (final Column col : this.columns) {
                final ColumnMetaData colmd = col.getColumnMetaData();
                final Integer colPos = (colmd != null) ? colmd.getPosition() : null;
                if (colPos == null) {
                    for (int i = 0; i < cols.length; ++i) {
                        if (cols[i] == null) {
                            cols[i] = col;
                        }
                    }
                }
            }
        }
        else {
            cols = this.columns.toArray(new Column[this.columns.size()]);
        }
        final ArrayList stmts = new ArrayList();
        stmts.add(this.dba.getCreateTableStatement(this, cols, props, this.storeMgr.getIdentifierFactory()));
        final PrimaryKey pk = this.getPrimaryKey();
        if (pk.size() > 0) {
            final String pkStmt = this.dba.getAddPrimaryKeyStatement(pk, this.storeMgr.getIdentifierFactory());
            if (pkStmt != null) {
                stmts.add(pkStmt);
            }
        }
        return stmts;
    }
    
    protected Map getSQLAddFKStatements(final Map actualForeignKeysByName, final ClassLoaderResolver clr) {
        this.assertIsInitialized();
        final HashMap stmtsByFKName = new HashMap();
        final List expectedForeignKeys = this.getExpectedForeignKeys(clr);
        final Iterator i = expectedForeignKeys.iterator();
        int n = 1;
        final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
        while (i.hasNext()) {
            final ForeignKey fk = i.next();
            if (!actualForeignKeysByName.containsValue(fk)) {
                if (fk.getName() == null) {
                    DatastoreIdentifier fkName;
                    do {
                        fkName = idFactory.newForeignKeyIdentifier(this, n++);
                    } while (actualForeignKeysByName.containsKey(fkName));
                    fk.setName(fkName.getIdentifierName());
                }
                final String stmtText = this.dba.getAddForeignKeyStatement(fk, idFactory);
                if (stmtText == null) {
                    continue;
                }
                stmtsByFKName.put(fk.getName(), stmtText);
            }
        }
        return stmtsByFKName;
    }
    
    protected Map getSQLAddCandidateKeyStatements(final Map actualCandidateKeysByName) {
        this.assertIsInitialized();
        final HashMap stmtsByCKName = new HashMap();
        final List expectedCandidateKeys = this.getExpectedCandidateKeys();
        final Iterator i = expectedCandidateKeys.iterator();
        int n = 1;
        final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
        while (i.hasNext()) {
            final CandidateKey ck = i.next();
            if (!actualCandidateKeysByName.containsValue(ck)) {
                if (ck.getName() == null) {
                    DatastoreIdentifier ckName;
                    do {
                        ckName = idFactory.newCandidateKeyIdentifier(this, n++);
                    } while (actualCandidateKeysByName.containsKey(ckName));
                    ck.setName(ckName.getIdentifierName());
                }
                final String stmtText = this.dba.getAddCandidateKeyStatement(ck, idFactory);
                if (stmtText == null) {
                    continue;
                }
                stmtsByCKName.put(ck.getName(), stmtText);
            }
        }
        return stmtsByCKName;
    }
    
    private boolean isIndexReallyNeeded(final Index requiredIdx, final Collection actualIndices) {
        final Iterator i = actualIndices.iterator();
        if (requiredIdx.getName() != null) {
            final IdentifierFactory idFactory = requiredIdx.getTable().getStoreManager().getIdentifierFactory();
            final String reqdName = idFactory.getIdentifierInAdapterCase(requiredIdx.getName());
            while (i.hasNext()) {
                final Index actualIdx = i.next();
                final String actualName = idFactory.getIdentifierInAdapterCase(actualIdx.getName());
                if (actualName.equals(reqdName) && actualIdx.getTable().getIdentifier().toString().equals(requiredIdx.getTable().getIdentifier().toString())) {
                    return false;
                }
            }
        }
        else {
            while (i.hasNext()) {
                final Index actualIdx2 = i.next();
                if (actualIdx2.toString().equals(requiredIdx.toString()) && actualIdx2.getTable().getIdentifier().toString().equals(requiredIdx.getTable().getIdentifier().toString())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    protected Map getSQLCreateIndexStatements(final Map actualIndicesByName, final ClassLoaderResolver clr) {
        this.assertIsInitialized();
        final HashMap stmtsByIdxName = new HashMap();
        final Set expectedIndices = this.getExpectedIndices(clr);
        int n = 1;
        final Iterator i = expectedIndices.iterator();
        final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
        while (i.hasNext()) {
            final Index idx = i.next();
            if (this.isIndexReallyNeeded(idx, actualIndicesByName.values())) {
                if (idx.getName() == null) {
                    DatastoreIdentifier idxName;
                    do {
                        idxName = idFactory.newIndexIdentifier(this, idx.getUnique(), n++);
                        idx.setName(idxName.getIdentifierName());
                    } while (actualIndicesByName.containsKey(idxName));
                }
                final String stmtText = this.dba.getCreateIndexStatement(idx, idFactory);
                stmtsByIdxName.put(idx.getName(), stmtText);
            }
        }
        return stmtsByIdxName;
    }
    
    @Override
    protected List getSQLDropStatements() {
        this.assertIsInitialized();
        final ArrayList stmts = new ArrayList();
        stmts.add(this.dba.getDropTableStatement(this));
        return stmts;
    }
}
