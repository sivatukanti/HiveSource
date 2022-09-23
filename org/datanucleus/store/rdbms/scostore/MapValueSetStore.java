// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.store.rdbms.sql.StatementGenerator;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.UnionStatementGenerator;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.metadata.MapMetaData;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.DiscriminatorStatementGenerator;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.Transaction;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import org.datanucleus.FetchPlan;
import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.ExecutionContext;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.rdbms.JDBCUtils;
import org.datanucleus.store.rdbms.mapping.MappingHelper;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.Collection;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.MapTable;
import org.datanucleus.store.rdbms.query.StatementParameterMapping;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.scostore.MapStore;

class MapValueSetStore extends AbstractSetStore
{
    protected final MapStore mapStore;
    protected final JavaTypeMapping keyMapping;
    private String findKeyStmt;
    private String iteratorStmtLocked;
    private String iteratorStmtUnlocked;
    private StatementClassMapping iteratorMappingDef;
    private StatementParameterMapping iteratorMappingParams;
    
    MapValueSetStore(final MapTable mapTable, final MapStore mapStore, final ClassLoaderResolver clr) {
        super(mapTable.getStoreManager(), clr);
        this.iteratorStmtLocked = null;
        this.iteratorStmtUnlocked = null;
        this.iteratorMappingDef = null;
        this.iteratorMappingParams = null;
        this.containerTable = mapTable;
        this.mapStore = mapStore;
        this.ownerMapping = mapTable.getOwnerMapping();
        this.keyMapping = mapTable.getKeyMapping();
        this.elementMapping = mapTable.getValueMapping();
        this.elementType = this.elementMapping.getType();
        this.ownerMemberMetaData = mapTable.getOwnerMemberMetaData();
        this.initialize(clr);
        if (this.keyMapping != null) {
            this.findKeyStmt = this.getFindKeyStmt();
        }
        else {
            this.findKeyStmt = null;
        }
    }
    
    MapValueSetStore(final DatastoreClass mapTable, final MapStore mapStore, final ClassLoaderResolver clr, final JavaTypeMapping ownerMapping, final JavaTypeMapping valueMapping, final AbstractMemberMetaData ownerMmd) {
        super(mapTable.getStoreManager(), clr);
        this.iteratorStmtLocked = null;
        this.iteratorStmtUnlocked = null;
        this.iteratorMappingDef = null;
        this.iteratorMappingParams = null;
        this.containerTable = mapTable;
        this.mapStore = mapStore;
        this.ownerMapping = ownerMapping;
        this.keyMapping = null;
        this.elementMapping = valueMapping;
        this.ownerMemberMetaData = ownerMmd;
        this.initialize(clr);
        if (this.keyMapping != null) {
            this.findKeyStmt = this.getFindKeyStmt();
        }
        else {
            this.findKeyStmt = null;
        }
    }
    
    private void initialize(final ClassLoaderResolver clr) {
        this.elementType = this.elementMapping.getType();
        this.elementsAreEmbedded = this.isEmbeddedMapping(this.elementMapping);
        this.elementsAreSerialised = this.isEmbeddedMapping(this.elementMapping);
        final Class valueCls = clr.classForName(this.elementType);
        if (ClassUtils.isReferenceType(valueCls)) {
            this.emd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForImplementationOfReference(valueCls, null, clr);
        }
        else {
            this.emd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(valueCls, clr);
        }
        if (this.emd != null) {
            this.elementType = this.emd.getFullClassName();
            this.elementInfo = this.getElementInformationForClass();
        }
    }
    
    @Override
    public boolean add(final ObjectProvider op, final Object element, final int size) {
        throw new UnsupportedOperationException("Cannot add to a map through its values collection");
    }
    
    @Override
    public boolean addAll(final ObjectProvider op, final Collection elements, final int size) {
        throw new UnsupportedOperationException("Cannot add to a map through its values collection");
    }
    
    @Override
    public boolean remove(final ObjectProvider op, final Object element, final int size, final boolean allowDependentField) {
        return this.validateElementForReading(op, element) && this.remove(op, element);
    }
    
    @Override
    public boolean removeAll(final ObjectProvider op, final Collection elements, final int size) {
        throw new NucleusUserException("Cannot remove values from a map through its values collection");
    }
    
    @Override
    public void clear(final ObjectProvider op) {
        if (this.canClear()) {
            throw new NucleusUserException("Cannot clear a map through its values collection");
        }
        super.clear(op);
    }
    
    protected boolean canClear() {
        return false;
    }
    
    protected boolean remove(final ObjectProvider op, final Object element) {
        if (this.findKeyStmt == null) {
            throw new UnsupportedOperationException("Cannot remove from a map through its values collection");
        }
        Object key = null;
        boolean keyExists = false;
        final ExecutionContext ec = op.getExecutionContext();
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, this.findKeyStmt);
                try {
                    int jdbcPosition = 1;
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    BackingStoreHelper.populateElementInStatement(ec, ps, element, jdbcPosition, this.elementMapping);
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, this.findKeyStmt, ps);
                    try {
                        if (rs.next()) {
                            key = this.keyMapping.getObject(ec, rs, MappingHelper.getMappingIndices(1, this.keyMapping));
                            keyExists = true;
                        }
                        JDBCUtils.logWarnings(rs);
                    }
                    finally {
                        rs.close();
                    }
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
            throw new NucleusDataStoreException("Request failed to check if set contains an element: " + this.findKeyStmt, e);
        }
        if (keyExists) {
            this.mapStore.remove(op, key);
            return true;
        }
        return false;
    }
    
    private String getFindKeyStmt() {
        final StringBuffer stmt = new StringBuffer("SELECT ");
        for (int i = 0; i < this.keyMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(this.keyMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
        }
        stmt.append(" FROM ");
        stmt.append(this.containerTable.toString());
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
        BackingStoreHelper.appendWhereClauseForMapping(stmt, this.elementMapping, null, false);
        return stmt.toString();
    }
    
    @Override
    public Iterator iterator(final ObjectProvider ownerOP) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        if (this.iteratorStmtLocked == null) {
            synchronized (this) {
                final SQLStatement sqlStmt = this.getSQLStatementForIterator(ownerOP);
                this.iteratorStmtUnlocked = sqlStmt.getSelectStatement().toSQL();
                sqlStmt.addExtension("lock-for-update", true);
                this.iteratorStmtLocked = sqlStmt.getSelectStatement().toSQL();
            }
        }
        final Transaction tx = ec.getTransaction();
        final String stmt = (tx.getSerializeRead() != null && tx.getSerializeRead()) ? this.iteratorStmtLocked : this.iteratorStmtUnlocked;
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, stmt);
                final StatementMappingIndex ownerIdx = this.iteratorMappingParams.getMappingForParameter("owner");
                for (int numParams = ownerIdx.getNumberOfParameterOccurrences(), paramInstance = 0; paramInstance < numParams; ++paramInstance) {
                    ownerIdx.getMapping().setObject(ec, ps, ownerIdx.getParameterPositionsForOccurrence(paramInstance), ownerOP.getObject());
                }
                try {
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, stmt, ps);
                    try {
                        ResultObjectFactory rof = null;
                        if (this.elementsAreEmbedded || this.elementsAreSerialised) {
                            return new SetStoreIterator(ownerOP, rs, null, this);
                        }
                        rof = this.storeMgr.newResultObjectFactory(this.emd, this.iteratorMappingDef, false, null, this.clr.classForName(this.elementType));
                        return new SetStoreIterator(ownerOP, rs, rof, this);
                    }
                    finally {
                        rs.close();
                    }
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
            throw new NucleusDataStoreException(MapValueSetStore.LOCALISER.msg("056006", stmt), e);
        }
        catch (MappedDatastoreException e2) {
            throw new NucleusDataStoreException(MapValueSetStore.LOCALISER.msg("056006", stmt), e2);
        }
    }
    
    protected SQLStatement getSQLStatementForIterator(final ObjectProvider ownerOP) {
        SQLStatement sqlStmt = null;
        final ClassLoaderResolver clr = ownerOP.getExecutionContext().getClassLoaderResolver();
        final Class valueCls = clr.classForName(this.elementType);
        SQLTable containerSqlTbl = null;
        final MapMetaData.MapType mapType = this.getOwnerMemberMetaData().getMap().getMapType();
        if (this.emd != null && this.emd.getDiscriminatorStrategyForTable() != null && this.emd.getDiscriminatorStrategyForTable() != DiscriminatorStrategy.NONE) {
            if (ClassUtils.isReferenceType(valueCls)) {
                final String[] clsNames = this.storeMgr.getNucleusContext().getMetaDataManager().getClassesImplementingInterface(this.elementType, clr);
                final Class[] cls = new Class[clsNames.length];
                for (int j = 0; j < clsNames.length; ++j) {
                    cls[j] = clr.classForName(clsNames[j]);
                }
                final StatementGenerator stmtGen = new DiscriminatorStatementGenerator(this.storeMgr, clr, cls, true, (DatastoreIdentifier)null, (String)null);
                sqlStmt = stmtGen.getStatement();
            }
            else {
                final StatementGenerator stmtGen2 = new DiscriminatorStatementGenerator(this.storeMgr, clr, valueCls, true, null, null);
                sqlStmt = stmtGen2.getStatement();
            }
            this.iterateUsingDiscriminator = true;
            if (mapType == MapMetaData.MapType.MAP_TYPE_VALUE_IN_KEY) {
                final JavaTypeMapping valueIdMapping = sqlStmt.getPrimaryTable().getTable().getIdMapping();
                containerSqlTbl = sqlStmt.innerJoin(sqlStmt.getPrimaryTable(), valueIdMapping, this.containerTable, null, this.elementMapping, null, null);
                SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, this.iteratorMappingDef = new StatementClassMapping(), ownerOP.getExecutionContext().getFetchPlan(), sqlStmt.getPrimaryTable(), this.emd, 0);
            }
            else if (mapType == MapMetaData.MapType.MAP_TYPE_KEY_IN_VALUE) {
                containerSqlTbl = sqlStmt.getPrimaryTable();
                SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, this.iteratorMappingDef = new StatementClassMapping(), ownerOP.getExecutionContext().getFetchPlan(), sqlStmt.getPrimaryTable(), this.emd, 0);
            }
            else {
                final JavaTypeMapping valueIdMapping = sqlStmt.getPrimaryTable().getTable().getIdMapping();
                containerSqlTbl = sqlStmt.innerJoin(sqlStmt.getPrimaryTable(), valueIdMapping, this.containerTable, null, this.elementMapping, null, null);
                SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, this.iteratorMappingDef = new StatementClassMapping(), ownerOP.getExecutionContext().getFetchPlan(), sqlStmt.getPrimaryTable(), this.emd, 0);
            }
        }
        else if (mapType == MapMetaData.MapType.MAP_TYPE_VALUE_IN_KEY) {
            if (this.emd != null) {
                this.iteratorMappingDef = new StatementClassMapping();
                final UnionStatementGenerator stmtGen3 = new UnionStatementGenerator(this.storeMgr, clr, valueCls, true, null, null);
                stmtGen3.setOption("selectNucleusType");
                this.iteratorMappingDef.setNucleusTypeColumnName("NUCLEUS_TYPE");
                sqlStmt = stmtGen3.getStatement();
                final JavaTypeMapping valueIdMapping2 = sqlStmt.getPrimaryTable().getTable().getIdMapping();
                containerSqlTbl = sqlStmt.innerJoin(sqlStmt.getPrimaryTable(), valueIdMapping2, this.containerTable, null, this.elementMapping, null, null);
                SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, this.iteratorMappingDef, ownerOP.getExecutionContext().getFetchPlan(), sqlStmt.getPrimaryTable(), this.emd, 0);
            }
            else {
                sqlStmt = new SQLStatement(this.storeMgr, this.containerTable, null, null);
                sqlStmt.setClassLoaderResolver(clr);
                SQLTable elemSqlTblForValue;
                containerSqlTbl = (elemSqlTblForValue = sqlStmt.getPrimaryTable());
                if (this.elementMapping.getTable() != containerSqlTbl.getTable()) {
                    elemSqlTblForValue = sqlStmt.getTableForDatastoreContainer(this.elementMapping.getTable());
                    if (elemSqlTblForValue == null) {
                        elemSqlTblForValue = sqlStmt.innerJoin(sqlStmt.getPrimaryTable(), sqlStmt.getPrimaryTable().getTable().getIdMapping(), this.elementMapping.getTable(), null, this.elementMapping.getTable().getIdMapping(), null, null);
                    }
                }
                sqlStmt.select(elemSqlTblForValue, this.elementMapping, null);
            }
        }
        else if (mapType == MapMetaData.MapType.MAP_TYPE_KEY_IN_VALUE) {
            this.iteratorMappingDef = new StatementClassMapping();
            final UnionStatementGenerator stmtGen3 = new UnionStatementGenerator(this.storeMgr, clr, valueCls, true, null, null);
            stmtGen3.setOption("selectNucleusType");
            this.iteratorMappingDef.setNucleusTypeColumnName("NUCLEUS_TYPE");
            sqlStmt = stmtGen3.getStatement();
            containerSqlTbl = sqlStmt.getPrimaryTable();
            SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, this.iteratorMappingDef, ownerOP.getExecutionContext().getFetchPlan(), sqlStmt.getPrimaryTable(), this.emd, 0);
        }
        else if (this.emd != null) {
            this.iteratorMappingDef = new StatementClassMapping();
            final UnionStatementGenerator stmtGen3 = new UnionStatementGenerator(this.storeMgr, clr, valueCls, true, null, null);
            stmtGen3.setOption("selectNucleusType");
            this.iteratorMappingDef.setNucleusTypeColumnName("NUCLEUS_TYPE");
            sqlStmt = stmtGen3.getStatement();
            final JavaTypeMapping valueIdMapping2 = sqlStmt.getPrimaryTable().getTable().getIdMapping();
            containerSqlTbl = sqlStmt.innerJoin(sqlStmt.getPrimaryTable(), valueIdMapping2, this.containerTable, null, this.elementMapping, null, null);
            SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, this.iteratorMappingDef, ownerOP.getExecutionContext().getFetchPlan(), sqlStmt.getPrimaryTable(), this.emd, 0);
        }
        else {
            sqlStmt = new SQLStatement(this.storeMgr, this.containerTable, null, null);
            containerSqlTbl = sqlStmt.getPrimaryTable();
            sqlStmt.select(sqlStmt.getPrimaryTable(), this.elementMapping, null);
        }
        final SQLExpressionFactory exprFactory = this.storeMgr.getSQLExpressionFactory();
        final SQLTable ownerSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStmt, containerSqlTbl, this.ownerMapping);
        final SQLExpression ownerExpr = exprFactory.newExpression(sqlStmt, ownerSqlTbl, this.ownerMapping);
        final SQLExpression ownerVal = exprFactory.newLiteralParameter(sqlStmt, this.ownerMapping, null, "OWNER");
        sqlStmt.whereAnd(ownerExpr.eq(ownerVal), true);
        int inputParamNum = 1;
        final StatementMappingIndex ownerIdx = new StatementMappingIndex(this.ownerMapping);
        if (sqlStmt.getNumberOfUnions() > 0) {
            for (int i = 0; i < sqlStmt.getNumberOfUnions() + 1; ++i) {
                final int[] paramPositions = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
                for (int k = 0; k < this.ownerMapping.getNumberOfDatastoreMappings(); ++k) {
                    paramPositions[k] = inputParamNum++;
                }
                ownerIdx.addParameterOccurrence(paramPositions);
            }
        }
        else {
            final int[] paramPositions2 = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
            for (int l = 0; l < this.ownerMapping.getNumberOfDatastoreMappings(); ++l) {
                paramPositions2[l] = inputParamNum++;
            }
            ownerIdx.addParameterOccurrence(paramPositions2);
        }
        (this.iteratorMappingParams = new StatementParameterMapping()).addMappingForParameter("owner", ownerIdx);
        return sqlStmt;
    }
}
