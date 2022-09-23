// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import org.datanucleus.util.StringUtils;
import org.datanucleus.exceptions.ClassNotPersistableException;
import java.sql.ResultSetMetaData;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.metadata.IdentityType;
import java.util.HashSet;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.metadata.FieldPersistenceModifier;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.query.QueryInterruptedException;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.query.QueryResult;
import org.datanucleus.store.connection.ManagedConnectionResourceListener;
import java.util.Collection;
import org.datanucleus.store.query.Query;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.NucleusLogger;
import java.util.Map;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.query.AbstractSQLQuery;

public final class SQLQuery extends AbstractSQLQuery
{
    protected static final Localiser LOCALISER_RDBMS;
    protected transient boolean isCompiled;
    protected transient StatementMappingIndex[] stmtMappings;
    
    public SQLQuery(final StoreManager storeMgr, final ExecutionContext ec, final SQLQuery query) {
        super(storeMgr, ec, query);
        this.isCompiled = false;
    }
    
    public SQLQuery(final StoreManager storeMgr, final ExecutionContext ec) {
        super(storeMgr, ec, (String)null);
        this.isCompiled = false;
    }
    
    public SQLQuery(final StoreManager storeMgr, final ExecutionContext ec, final String queryString) {
        super(storeMgr, ec, queryString);
        this.isCompiled = false;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof SQLQuery && super.equals(obj) && this.inputSQL.equals(((SQLQuery)obj).inputSQL));
    }
    
    @Override
    protected void discardCompiled() {
        this.isCompiled = false;
        this.stmtMappings = null;
        super.discardCompiled();
    }
    
    @Override
    protected boolean isCompiled() {
        return this.isCompiled;
    }
    
    @Override
    public boolean processesRangeInDatastoreQuery() {
        return true;
    }
    
    public void compileInternal(final Map parameterValues) {
        if (this.isCompiled) {
            return;
        }
        this.compiledSQL = this.generateQueryStatement();
        if (NucleusLogger.QUERY.isDebugEnabled()) {
            NucleusLogger.QUERY.debug(SQLQuery.LOCALISER.msg("059012", this.compiledSQL));
        }
        this.isCompiled = true;
    }
    
    @Override
    protected Object performExecute(final Map parameters) {
        if (parameters.size() != ((this.parameterNames != null) ? this.parameterNames.length : 0)) {
            throw new NucleusUserException(SQLQuery.LOCALISER_RDBMS.msg("059019", "" + this.parameterNames.length, "" + parameters.size()));
        }
        Label_0280: {
            if (this.type != 2) {
                if (this.type != 1) {
                    break Label_0280;
                }
            }
            try {
                final RDBMSStoreManager storeMgr = (RDBMSStoreManager)this.getStoreManager();
                final ManagedConnection mconn = storeMgr.getConnection(this.ec);
                final SQLController sqlControl = storeMgr.getSQLController();
                try {
                    final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, this.compiledSQL, false);
                    try {
                        for (int i = 0; i < parameters.size(); ++i) {
                            final Object obj = parameters.get(i + 1);
                            ps.setObject(i + 1, obj);
                        }
                        final int[] rcs = sqlControl.executeStatementUpdate(this.ec, mconn, this.compiledSQL, ps, true);
                        return rcs[0];
                    }
                    finally {
                        sqlControl.closeStatement(mconn, ps);
                    }
                }
                finally {
                    mconn.release();
                }
            }
            catch (SQLException e) {
                throw new NucleusDataStoreException(SQLQuery.LOCALISER.msg("059025", this.compiledSQL), e);
            }
        }
        AbstractRDBMSQueryResult qr = null;
        try {
            final RDBMSStoreManager storeMgr2 = (RDBMSStoreManager)this.getStoreManager();
            final ManagedConnection mconn2 = storeMgr2.getConnection(this.ec);
            final SQLController sqlControl2 = storeMgr2.getSQLController();
            try {
                final PreparedStatement ps2 = RDBMSQueryUtils.getPreparedStatementForQuery(mconn2, this.compiledSQL, this);
                try {
                    for (int j = 0; j < parameters.size(); ++j) {
                        final Object obj2 = parameters.get(j + 1);
                        ps2.setObject(j + 1, obj2);
                    }
                    RDBMSQueryUtils.prepareStatementForExecution(ps2, this, false);
                    final ResultSet rs = sqlControl2.executeStatementQuery(this.ec, mconn2, this.compiledSQL, ps2);
                    try {
                        ResultObjectFactory rof = null;
                        if (this.resultMetaData != null) {
                            rof = new ResultMetaDataROF(storeMgr2, this.resultMetaData);
                        }
                        else if (this.resultClass != null || this.candidateClass == null) {
                            rof = RDBMSQueryUtils.getResultObjectFactoryForNoCandidateClass(storeMgr2, rs, this.resultClass);
                        }
                        else {
                            rof = this.getResultObjectFactoryForCandidateClass(rs);
                        }
                        final String resultSetType = RDBMSQueryUtils.getResultSetTypeForQuery(this);
                        if (resultSetType.equals("scroll-insensitive") || resultSetType.equals("scroll-sensitive")) {
                            qr = new ScrollableQueryResult(this, rof, rs, null);
                        }
                        else {
                            qr = new ForwardQueryResult(this, rof, rs, null);
                        }
                        qr.initialise();
                        final QueryResult qr2 = qr;
                        final ManagedConnection mconn3 = mconn2;
                        mconn2.addListener(new ManagedConnectionResourceListener() {
                            @Override
                            public void transactionFlushed() {
                            }
                            
                            @Override
                            public void transactionPreClose() {
                                qr2.disconnect();
                            }
                            
                            @Override
                            public void managedConnectionPreClose() {
                                if (!SQLQuery.this.ec.getTransaction().isActive()) {
                                    qr2.disconnect();
                                }
                            }
                            
                            @Override
                            public void managedConnectionPostClose() {
                            }
                            
                            @Override
                            public void resourcePostClose() {
                                mconn3.removeListener(this);
                            }
                        });
                    }
                    finally {
                        if (qr == null) {
                            rs.close();
                        }
                    }
                }
                catch (QueryInterruptedException qie) {
                    ps2.cancel();
                    throw qie;
                }
                finally {
                    if (qr == null) {
                        sqlControl2.closeStatement(mconn2, ps2);
                    }
                }
            }
            finally {
                mconn2.release();
            }
        }
        catch (SQLException e2) {
            throw new NucleusDataStoreException(SQLQuery.LOCALISER.msg("059025", this.compiledSQL), e2);
        }
        return qr;
    }
    
    @Override
    protected void assertSupportsCancel() {
    }
    
    protected ResultObjectFactory getResultObjectFactoryForCandidateClass(final ResultSet rs) throws SQLException {
        final ClassLoaderResolver clr = this.ec.getClassLoaderResolver();
        final RDBMSStoreManager storeMgr = (RDBMSStoreManager)this.getStoreManager();
        final DatastoreAdapter dba = storeMgr.getDatastoreAdapter();
        final AbstractClassMetaData candidateCmd = this.ec.getMetaDataManager().getMetaDataForClass(this.candidateClass, clr);
        final int fieldCount = candidateCmd.getNoOfManagedMembers() + candidateCmd.getNoOfInheritedManagedMembers();
        final Map columnFieldNumberMap = new HashMap();
        this.stmtMappings = new StatementMappingIndex[fieldCount];
        final DatastoreClass tbl = storeMgr.getDatastoreClass(this.candidateClass.getName(), clr);
        for (int fieldNumber = 0; fieldNumber < fieldCount; ++fieldNumber) {
            final AbstractMemberMetaData fmd = candidateCmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
            final String fieldName = fmd.getName();
            final Class fieldType = fmd.getType();
            JavaTypeMapping m = null;
            if (fmd.getPersistenceModifier() != FieldPersistenceModifier.NONE) {
                if (tbl != null) {
                    m = tbl.getMemberMapping(fmd);
                }
                else {
                    m = storeMgr.getMappingManager().getMappingWithDatastoreMapping(fieldType, false, false, clr);
                }
                if (m.includeInFetchStatement()) {
                    String columnName = null;
                    if (fmd.getColumnMetaData() != null && fmd.getColumnMetaData().length > 0) {
                        for (int colNum = 0; colNum < fmd.getColumnMetaData().length; ++colNum) {
                            columnName = fmd.getColumnMetaData()[colNum].getName();
                            columnFieldNumberMap.put(columnName, fieldNumber);
                        }
                    }
                    else {
                        columnName = storeMgr.getIdentifierFactory().newColumnIdentifier(fieldName, this.ec.getNucleusContext().getTypeManager().isDefaultEmbeddedType(fieldType), 0).getIdentifierName();
                        columnFieldNumberMap.put(columnName, fieldNumber);
                    }
                }
            }
            this.stmtMappings[fieldNumber] = new StatementMappingIndex(m);
        }
        if (columnFieldNumberMap.size() == 0) {
            throw new NucleusUserException(SQLQuery.LOCALISER.msg("059030", this.candidateClass.getName())).setFatal();
        }
        DatastoreClass table = storeMgr.getDatastoreClass(this.candidateClass.getName(), clr);
        if (table == null) {
            final AbstractClassMetaData[] cmds = storeMgr.getClassesManagingTableForClass(candidateCmd, clr);
            if (cmds == null || cmds.length != 1) {
                throw new NucleusUserException("SQL query specified with class " + this.candidateClass.getName() + " but this doesn't have its own table, or is mapped to multiple tables. Unsupported");
            }
            table = storeMgr.getDatastoreClass(cmds[0].getFullClassName(), clr);
        }
        final PersistableMapping idMapping = (PersistableMapping)table.getIdMapping();
        final String[] idColNames = new String[idMapping.getNumberOfDatastoreMappings()];
        for (int i = 0; i < idMapping.getNumberOfDatastoreMappings(); ++i) {
            final DatastoreMapping j = idMapping.getDatastoreMapping(i);
            idColNames[i] = j.getColumn().getIdentifier().toString();
        }
        final String discrimColName = (table.getDiscriminatorMapping(false) != null) ? table.getDiscriminatorMapping(false).getDatastoreMapping(0).getColumn().getIdentifier().toString() : null;
        final String versionColName = (table.getVersionMapping(false) != null) ? table.getVersionMapping(false).getDatastoreMapping(0).getColumn().getIdentifier().toString() : null;
        final ResultSetMetaData rsmd = rs.getMetaData();
        final HashSet remainingColumnNames = new HashSet(columnFieldNumberMap.size());
        final int colCount = rsmd.getColumnCount();
        int[] datastoreIndex = null;
        int[] versionIndex = null;
        int[] discrimIndex = null;
        final int[] matchedFieldNumbers = new int[colCount];
        int fieldNumberPosition = 0;
        for (int colNum2 = 1; colNum2 <= colCount; ++colNum2) {
            final String colName = rsmd.getColumnName(colNum2);
            int fieldNumber2 = -1;
            Integer fieldNum = columnFieldNumberMap.get(colName);
            if (fieldNum == null) {
                fieldNum = columnFieldNumberMap.get(colName.toLowerCase());
                if (fieldNum == null) {
                    fieldNum = columnFieldNumberMap.get(colName.toUpperCase());
                }
            }
            if (fieldNum != null) {
                fieldNumber2 = fieldNum;
            }
            if (fieldNumber2 >= 0) {
                int[] exprIndices = null;
                if (this.stmtMappings[fieldNumber2].getColumnPositions() != null) {
                    exprIndices = new int[this.stmtMappings[fieldNumber2].getColumnPositions().length + 1];
                    for (int k = 0; k < this.stmtMappings[fieldNumber2].getColumnPositions().length; ++k) {
                        exprIndices[k] = this.stmtMappings[fieldNumber2].getColumnPositions()[k];
                    }
                    exprIndices[exprIndices.length - 1] = colNum2;
                }
                else {
                    exprIndices = new int[] { colNum2 };
                }
                this.stmtMappings[fieldNumber2].setColumnPositions(exprIndices);
                remainingColumnNames.remove(colName);
                matchedFieldNumbers[fieldNumberPosition++] = fieldNumber2;
            }
            if (discrimColName != null && colName.equals(discrimColName)) {
                discrimIndex = new int[] { colNum2 };
            }
            if (versionColName != null && colName.equals(versionColName)) {
                versionIndex = new int[] { colNum2 };
            }
            if (candidateCmd.getIdentityType() == IdentityType.DATASTORE && columnNamesAreTheSame(dba, idColNames[0], colName)) {
                datastoreIndex = new int[] { colNum2 };
            }
        }
        final int[] fieldNumbers = new int[fieldNumberPosition];
        for (int l = 0; l < fieldNumberPosition; ++l) {
            fieldNumbers[l] = matchedFieldNumbers[l];
        }
        final StatementClassMapping mappingDefinition = new StatementClassMapping();
        for (int i2 = 0; i2 < fieldNumbers.length; ++i2) {
            mappingDefinition.addMappingForMember(fieldNumbers[i2], this.stmtMappings[fieldNumbers[i2]]);
        }
        if (datastoreIndex != null) {
            final StatementMappingIndex datastoreMappingIdx = new StatementMappingIndex(table.getDatastoreObjectIdMapping());
            datastoreMappingIdx.setColumnPositions(datastoreIndex);
            mappingDefinition.addMappingForMember(-1, datastoreMappingIdx);
        }
        if (discrimIndex != null) {
            final StatementMappingIndex discrimMappingIdx = new StatementMappingIndex(table.getDiscriminatorMapping(true));
            discrimMappingIdx.setColumnPositions(discrimIndex);
            mappingDefinition.addMappingForMember(-3, discrimMappingIdx);
        }
        if (versionIndex != null) {
            final StatementMappingIndex versionMappingIdx = new StatementMappingIndex(table.getVersionMapping(true));
            versionMappingIdx.setColumnPositions(versionIndex);
            mappingDefinition.addMappingForMember(-2, versionMappingIdx);
        }
        return storeMgr.newResultObjectFactory(candidateCmd, mappingDefinition, this.ignoreCache, this.getFetchPlan(), this.getCandidateClass());
    }
    
    public static boolean columnNamesAreTheSame(final DatastoreAdapter dba, final String name1, final String name2) {
        return name1.equalsIgnoreCase(name2) || name1.equalsIgnoreCase(dba.getIdentifierQuoteString() + name2 + dba.getIdentifierQuoteString());
    }
    
    protected String generateQueryStatement() {
        final String compiledSQL = this.getInputSQL();
        if (this.candidateClass != null && this.getType() == 0) {
            final RDBMSStoreManager storeMgr = (RDBMSStoreManager)this.getStoreManager();
            final ClassLoaderResolver clr = this.ec.getClassLoaderResolver();
            final AbstractClassMetaData cmd = this.ec.getMetaDataManager().getMetaDataForClass(this.candidateClass, clr);
            if (cmd == null) {
                throw new ClassNotPersistableException(this.candidateClass.getName());
            }
            if (cmd.getPersistenceCapableSuperclass() != null) {}
            if (this.getResultClass() == null) {
                String selections = this.stripComments(compiledSQL.trim()).substring(7);
                int fromStart = selections.indexOf("FROM");
                if (fromStart == -1) {
                    fromStart = selections.indexOf("from");
                }
                selections = selections.substring(0, fromStart).trim();
                final String[] selectedColumns = StringUtils.split(selections, ",");
                if (selectedColumns == null || selectedColumns.length == 0) {
                    throw new NucleusUserException(SQLQuery.LOCALISER_RDBMS.msg("059003", compiledSQL));
                }
                if (selectedColumns.length == 1 && selectedColumns[0].trim().equals("*")) {
                    return compiledSQL;
                }
                final DatastoreClass table = storeMgr.getDatastoreClass(this.candidateClass.getName(), clr);
                final PersistableMapping idMapping = (PersistableMapping)table.getIdMapping();
                final String[] idColNames = new String[idMapping.getNumberOfDatastoreMappings()];
                final boolean[] idColMissing = new boolean[idMapping.getNumberOfDatastoreMappings()];
                for (int i = 0; i < idMapping.getNumberOfDatastoreMappings(); ++i) {
                    final DatastoreMapping m = idMapping.getDatastoreMapping(i);
                    idColNames[i] = m.getColumn().getIdentifier().toString();
                    idColMissing[i] = true;
                }
                final String discriminatorColName = (table.getDiscriminatorMapping(false) != null) ? table.getDiscriminatorMapping(false).getDatastoreMapping(0).getColumn().getIdentifier().toString() : null;
                final String versionColName = (table.getVersionMapping(false) != null) ? table.getVersionMapping(false).getDatastoreMapping(0).getColumn().getIdentifier().toString() : null;
                boolean discrimMissing = discriminatorColName != null;
                boolean versionMissing = true;
                if (versionColName == null) {
                    versionMissing = false;
                }
                final DatastoreAdapter dba = storeMgr.getDatastoreAdapter();
                final AbstractClassMetaData candidateCmd = this.ec.getMetaDataManager().getMetaDataForClass(this.candidateClass, clr);
                for (int j = 0; j < selectedColumns.length; ++j) {
                    String colName = selectedColumns[j].trim();
                    if (colName.indexOf(" AS ") > 0) {
                        colName = colName.substring(colName.indexOf(" AS ") + 4).trim();
                    }
                    else if (colName.indexOf(" as ") > 0) {
                        colName = colName.substring(colName.indexOf(" as ") + 4).trim();
                    }
                    if (candidateCmd.getIdentityType() == IdentityType.DATASTORE) {
                        if (columnNamesAreTheSame(dba, idColNames[0], colName)) {
                            idColMissing[0] = false;
                        }
                    }
                    else if (candidateCmd.getIdentityType() == IdentityType.APPLICATION) {
                        for (int k = 0; k < idColNames.length; ++k) {
                            if (columnNamesAreTheSame(dba, idColNames[k], colName)) {
                                idColMissing[k] = false;
                            }
                        }
                    }
                    if (discrimMissing && columnNamesAreTheSame(dba, discriminatorColName, colName)) {
                        discrimMissing = false;
                    }
                    else if (versionMissing && columnNamesAreTheSame(dba, versionColName, colName)) {
                        versionMissing = false;
                    }
                }
                if (discrimMissing) {
                    throw new NucleusUserException(SQLQuery.LOCALISER_RDBMS.msg("059014", compiledSQL, this.candidateClass.getName(), discriminatorColName));
                }
                if (versionMissing) {
                    throw new NucleusUserException(SQLQuery.LOCALISER_RDBMS.msg("059015", compiledSQL, this.candidateClass.getName(), versionColName));
                }
                for (int j = 0; j < idColMissing.length; ++j) {
                    if (idColMissing[j]) {
                        throw new NucleusUserException(SQLQuery.LOCALISER_RDBMS.msg("059013", compiledSQL, this.candidateClass.getName(), idColNames[j]));
                    }
                }
            }
        }
        return compiledSQL;
    }
    
    private String stripComments(final String sql) {
        return sql.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", "");
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
