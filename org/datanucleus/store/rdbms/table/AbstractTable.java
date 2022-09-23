// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import java.io.Writer;
import org.datanucleus.store.rdbms.JDBCUtils;
import java.io.IOException;
import java.util.Iterator;
import java.sql.Statement;
import org.datanucleus.store.rdbms.exceptions.MissingTableException;
import org.datanucleus.store.rdbms.schema.RDBMSSchemaHandler;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.Connection;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.exceptions.DuplicateColumnException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.DiscriminatorMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;

public abstract class AbstractTable implements Table
{
    protected static final int TABLE_STATE_NEW = 0;
    protected static final int TABLE_STATE_PK_INITIALIZED = 1;
    protected static final int TABLE_STATE_INITIALIZED = 2;
    protected static final int TABLE_STATE_INITIALIZED_MODIFIED = 3;
    protected static final int TABLE_STATE_VALIDATED = 4;
    protected static final Localiser LOCALISER;
    protected final RDBMSStoreManager storeMgr;
    protected final DatastoreAdapter dba;
    protected final DatastoreIdentifier identifier;
    protected int state;
    protected List<Column> columns;
    protected HashMap<DatastoreIdentifier, Column> columnsByName;
    private String fullyQualifiedName;
    private final int hashCode;
    protected Boolean existsInDatastore;
    
    public AbstractTable(final DatastoreIdentifier identifier, final RDBMSStoreManager storeMgr) {
        this.state = 0;
        this.columns = new ArrayList<Column>();
        this.columnsByName = new HashMap<DatastoreIdentifier, Column>();
        this.existsInDatastore = null;
        this.storeMgr = storeMgr;
        this.dba = storeMgr.getDatastoreAdapter();
        this.identifier = identifier;
        this.hashCode = (identifier.hashCode() ^ storeMgr.hashCode());
    }
    
    @Override
    public boolean isInitialized() {
        return this.state >= 2;
    }
    
    public boolean isPKInitialized() {
        return this.state >= 1;
    }
    
    @Override
    public boolean isValidated() {
        return this.state == 4;
    }
    
    @Override
    public boolean isInitializedModified() {
        return this.state == 3;
    }
    
    @Override
    public RDBMSStoreManager getStoreManager() {
        return this.storeMgr;
    }
    
    @Override
    public String getCatalogName() {
        return this.identifier.getCatalogName();
    }
    
    @Override
    public String getSchemaName() {
        return this.identifier.getSchemaName();
    }
    
    @Override
    public DatastoreIdentifier getIdentifier() {
        return this.identifier;
    }
    
    @Override
    public DiscriminatorMetaData getDiscriminatorMetaData() {
        return null;
    }
    
    @Override
    public JavaTypeMapping getDiscriminatorMapping(final boolean allowSuperclasses) {
        return null;
    }
    
    @Override
    public JavaTypeMapping getMultitenancyMapping() {
        return null;
    }
    
    @Override
    public VersionMetaData getVersionMetaData() {
        return null;
    }
    
    @Override
    public JavaTypeMapping getVersionMapping(final boolean allowSuperclasses) {
        return null;
    }
    
    @Override
    public synchronized Column addColumn(final String storedJavaType, final DatastoreIdentifier name, final JavaTypeMapping mapping, final ColumnMetaData colmd) {
        boolean duplicateName = false;
        if (this.hasColumnName(name)) {
            duplicateName = true;
        }
        final Column col = new ColumnImpl(this, storedJavaType, name, colmd);
        if (duplicateName && colmd != null) {
            final Column existingCol = this.columnsByName.get(name);
            MetaData md;
            for (md = existingCol.getColumnMetaData().getParent(); !(md instanceof AbstractClassMetaData); md = md.getParent()) {
                if (md == null) {
                    throw new NucleusUserException(AbstractTable.LOCALISER.msg("057043", name.getIdentifierName(), this.getDatastoreIdentifierFullyQualified(), colmd.toString()));
                }
            }
            MetaData dupMd = colmd.getParent();
            while (!(dupMd instanceof AbstractClassMetaData)) {
                dupMd = dupMd.getParent();
                if (dupMd == null) {
                    throw new NucleusUserException(AbstractTable.LOCALISER.msg("057044", name.getIdentifierName(), this.getDatastoreIdentifierFullyQualified(), colmd.toString()));
                }
            }
            final boolean reuseColumns = this.storeMgr.getBooleanProperty("datanucleus.rdbms.allowColumnReuse");
            if (!reuseColumns) {
                if (((AbstractClassMetaData)md).getFullClassName().equals(((AbstractClassMetaData)dupMd).getFullClassName())) {
                    throw new DuplicateColumnException(this.toString(), existingCol, col);
                }
                if (mapping != null && !mapping.getClass().isAssignableFrom(existingCol.getJavaTypeMapping().getClass()) && !existingCol.getJavaTypeMapping().getClass().isAssignableFrom(mapping.getClass())) {
                    throw new DuplicateColumnException(this.toString(), existingCol, col);
                }
            }
            else if (mapping != null && mapping.getMemberMetaData() != null) {
                NucleusLogger.DATASTORE_SCHEMA.warn("Column " + existingCol + " has already been defined but needing to reuse it for " + mapping.getMemberMetaData().getFullFieldName());
            }
            else {
                NucleusLogger.DATASTORE_SCHEMA.warn("Column " + existingCol + " has already been defined but needing to reuse it");
            }
            Class fieldStoredJavaTypeClass = null;
            Class existingColStoredJavaTypeClass = null;
            try {
                final ClassLoaderResolver clr = this.storeMgr.getNucleusContext().getClassLoaderResolver(null);
                fieldStoredJavaTypeClass = clr.classForName(storedJavaType);
                existingColStoredJavaTypeClass = clr.classForName(col.getStoredJavaType());
            }
            catch (RuntimeException ex) {}
            if (fieldStoredJavaTypeClass != null && existingColStoredJavaTypeClass != null && !fieldStoredJavaTypeClass.isAssignableFrom(existingColStoredJavaTypeClass) && !existingColStoredJavaTypeClass.isAssignableFrom(fieldStoredJavaTypeClass)) {
                throw new DuplicateColumnException(this.toString(), existingCol, col);
            }
        }
        if (!duplicateName) {
            this.addColumnInternal(col);
        }
        if (this.isInitialized()) {
            this.state = 3;
        }
        return col;
    }
    
    @Override
    public boolean hasColumn(final DatastoreIdentifier identifier) {
        return this.hasColumnName(identifier);
    }
    
    @Override
    public Column getColumn(final DatastoreIdentifier identifier) {
        return this.columnsByName.get(identifier);
    }
    
    @Override
    public Column[] getColumns() {
        return this.columns.toArray(new Column[this.columns.size()]);
    }
    
    @Override
    public boolean create(final Connection conn) throws SQLException {
        this.assertIsInitialized();
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(AbstractTable.LOCALISER.msg("057029", this));
        }
        final List createStmts = this.getSQLCreateStatements(null);
        this.executeDdlStatementList(createStmts, conn);
        return !createStmts.isEmpty();
    }
    
    @Override
    public void drop(final Connection conn) throws SQLException {
        this.assertIsInitialized();
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(AbstractTable.LOCALISER.msg("057030", this));
        }
        this.executeDdlStatementList(this.getSQLDropStatements(), conn);
    }
    
    @Override
    public boolean exists(final Connection conn, final boolean auto_create) throws SQLException {
        this.assertIsInitialized();
        final String type = ((RDBMSSchemaHandler)this.storeMgr.getSchemaHandler()).getTableType(conn, this);
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            if (type == null) {
                NucleusLogger.DATASTORE_SCHEMA.debug("Check of existence of " + this + " returned no table");
            }
            else {
                NucleusLogger.DATASTORE_SCHEMA.debug("Check of existence of " + this + " returned table type of " + type);
            }
        }
        if (type != null && (!this.allowDDLOutput() || this.storeMgr.getDdlWriter() == null || !this.storeMgr.getCompleteDDL())) {
            this.existsInDatastore = Boolean.TRUE;
            return false;
        }
        if (!auto_create) {
            this.existsInDatastore = Boolean.FALSE;
            throw new MissingTableException(this.getCatalogName(), this.getSchemaName(), this.toString());
        }
        final boolean created = this.create(conn);
        final String tableType = ((RDBMSSchemaHandler)this.storeMgr.getSchemaHandler()).getTableType(conn, this);
        if (this.storeMgr.getDdlWriter() == null || tableType != null) {
            this.existsInDatastore = Boolean.TRUE;
        }
        this.state = 4;
        return created;
    }
    
    @Override
    public final boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractTable)) {
            return false;
        }
        final AbstractTable t = (AbstractTable)obj;
        return this.getClass().equals(t.getClass()) && this.identifier.equals(t.identifier) && this.storeMgr.equals(t.storeMgr);
    }
    
    @Override
    public final int hashCode() {
        return this.hashCode;
    }
    
    @Override
    public final String toString() {
        if (this.fullyQualifiedName != null) {
            return this.fullyQualifiedName;
        }
        return this.fullyQualifiedName = this.identifier.getFullyQualifiedName(false);
    }
    
    public DatastoreIdentifier getDatastoreIdentifierFullyQualified() {
        String catalog = this.identifier.getCatalogName();
        if (catalog != null) {
            catalog = catalog.replace(this.dba.getIdentifierQuoteString(), "");
        }
        String schema = this.identifier.getSchemaName();
        if (schema != null) {
            schema = schema.replace(this.dba.getIdentifierQuoteString(), "");
        }
        String table = this.identifier.getIdentifierName();
        table = table.replace(this.dba.getIdentifierQuoteString(), "");
        final DatastoreIdentifier di = this.storeMgr.getIdentifierFactory().newTableIdentifier(table);
        di.setCatalogName(catalog);
        di.setSchemaName(schema);
        return di;
    }
    
    protected synchronized void addColumnInternal(final Column col) {
        final DatastoreIdentifier colName = col.getIdentifier();
        this.columns.add(col);
        this.columnsByName.put(colName, col);
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(AbstractTable.LOCALISER.msg("057034", col));
        }
    }
    
    protected boolean hasColumnName(final DatastoreIdentifier colName) {
        return this.columnsByName.get(colName) != null;
    }
    
    protected abstract List getSQLCreateStatements(final Properties p0);
    
    protected abstract List getSQLDropStatements();
    
    protected void assertIsPKUninitialized() {
        if (this.isPKInitialized()) {
            throw new IllegalStateException(AbstractTable.LOCALISER.msg("057000", this));
        }
    }
    
    protected void assertIsUninitialized() {
        if (this.isInitialized()) {
            throw new IllegalStateException(AbstractTable.LOCALISER.msg("057000", this));
        }
    }
    
    protected void assertIsInitialized() {
        if (!this.isInitialized()) {
            throw new IllegalStateException(AbstractTable.LOCALISER.msg("057001", this));
        }
    }
    
    protected void assertIsInitializedModified() {
        if (!this.isInitializedModified()) {
            throw new IllegalStateException(AbstractTable.LOCALISER.msg("RDBMS.Table.UnmodifiedError", this));
        }
    }
    
    protected void assertIsPKInitialized() {
        if (!this.isPKInitialized()) {
            throw new IllegalStateException(AbstractTable.LOCALISER.msg("057001", this));
        }
    }
    
    protected void assertIsValidated() {
        if (!this.isValidated()) {
            throw new IllegalStateException(AbstractTable.LOCALISER.msg("057002", this));
        }
    }
    
    protected boolean allowDDLOutput() {
        return true;
    }
    
    protected void executeDdlStatementList(final List stmts, final Connection conn) throws SQLException {
        final Statement stmt = conn.createStatement();
        String stmtText = null;
        try {
            final Iterator i = stmts.iterator();
            while (i.hasNext()) {
                stmtText = i.next();
                this.executeDdlStatement(stmt, stmtText);
            }
        }
        catch (SQLException sqe) {
            NucleusLogger.DATASTORE.error(AbstractTable.LOCALISER.msg("057028", stmtText, sqe));
            throw sqe;
        }
        finally {
            stmt.close();
        }
    }
    
    protected void executeDdlStatement(final Statement stmt, final String stmtText) throws SQLException {
        final Writer ddlWriter = this.storeMgr.getDdlWriter();
        if (ddlWriter != null && this.allowDDLOutput()) {
            try {
                if (!this.storeMgr.hasWrittenDdlStatement(stmtText)) {
                    ddlWriter.write(stmtText + ";\n\n");
                    this.storeMgr.addWrittenDdlStatement(stmtText);
                }
            }
            catch (IOException e) {
                NucleusLogger.DATASTORE_SCHEMA.error("error writing DDL into file for table " + this.toString() + " and statement=" + stmtText, e);
            }
        }
        else {
            if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                NucleusLogger.DATASTORE_SCHEMA.debug(stmtText);
            }
            final long startTime = System.currentTimeMillis();
            stmt.execute(stmtText);
            if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                NucleusLogger.DATASTORE_SCHEMA.debug(AbstractTable.LOCALISER.msg("045000", System.currentTimeMillis() - startTime));
            }
        }
        JDBCUtils.logWarnings(stmt);
    }
    
    protected boolean tableExistsInDatastore(final Connection conn) throws SQLException {
        if (this.existsInDatastore == null) {
            try {
                this.exists(conn, false);
            }
            catch (MissingTableException ex) {}
        }
        return this.existsInDatastore;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
