// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.UnionStatementGenerator;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.metadata.MapMetaData;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.DiscriminatorStatementGenerator;
import org.datanucleus.metadata.DiscriminatorStrategy;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.Transaction;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.FetchPlan;
import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import java.util.Iterator;
import java.util.Collection;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.MapTable;
import org.datanucleus.store.rdbms.query.StatementParameterMapping;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.store.scostore.MapStore;

class MapKeySetStore extends AbstractSetStore
{
    protected final MapStore mapStore;
    private String iteratorStmtLocked;
    private String iteratorStmtUnlocked;
    private StatementClassMapping iteratorMappingDef;
    private StatementParameterMapping iteratorMappingParams;
    
    MapKeySetStore(final MapTable mapTable, final MapStore mapStore, final ClassLoaderResolver clr) {
        super(mapTable.getStoreManager(), clr);
        this.iteratorStmtLocked = null;
        this.iteratorStmtUnlocked = null;
        this.iteratorMappingDef = null;
        this.iteratorMappingParams = null;
        this.mapStore = mapStore;
        this.containerTable = mapTable;
        this.ownerMemberMetaData = mapTable.getOwnerMemberMetaData();
        this.ownerMapping = mapTable.getOwnerMapping();
        this.elementMapping = mapTable.getKeyMapping();
        this.initialize(clr);
    }
    
    MapKeySetStore(final Table mapTable, final MapStore mapStore, final ClassLoaderResolver clr, final JavaTypeMapping ownerMapping, final JavaTypeMapping keyMapping, final AbstractMemberMetaData ownerMmd) {
        super(mapTable.getStoreManager(), clr);
        this.iteratorStmtLocked = null;
        this.iteratorStmtUnlocked = null;
        this.iteratorMappingDef = null;
        this.iteratorMappingParams = null;
        this.mapStore = mapStore;
        this.containerTable = mapTable;
        this.ownerMemberMetaData = ownerMmd;
        this.ownerMapping = ownerMapping;
        this.elementMapping = keyMapping;
        this.initialize(clr);
    }
    
    private void initialize(final ClassLoaderResolver clr) {
        this.elementType = this.elementMapping.getType();
        this.elementsAreEmbedded = this.isEmbeddedMapping(this.elementMapping);
        this.elementsAreSerialised = this.isEmbeddedMapping(this.elementMapping);
        final Class element_class = clr.classForName(this.elementType);
        if (ClassUtils.isReferenceType(element_class)) {
            this.emd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForImplementationOfReference(element_class, null, clr);
        }
        else {
            this.emd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(element_class, clr);
        }
        if (this.emd != null) {
            this.elementType = this.emd.getFullClassName();
            this.elementInfo = this.getElementInformationForClass();
        }
    }
    
    @Override
    public boolean add(final ObjectProvider op, final Object element, final int size) {
        throw new UnsupportedOperationException("Cannot add to a map through its key set");
    }
    
    @Override
    public boolean addAll(final ObjectProvider op, final Collection elements, final int size) {
        throw new UnsupportedOperationException("Cannot add to a map through its key set");
    }
    
    @Override
    public boolean remove(final ObjectProvider op, final Object element, final int size, final boolean allowDependentField) {
        if (!this.canRemove()) {
            throw new UnsupportedOperationException("Cannot remove from an inverse map through its key set");
        }
        return super.remove(op, element, size, allowDependentField);
    }
    
    @Override
    public boolean removeAll(final ObjectProvider op, final Collection elements, final int size) {
        if (!this.canRemove()) {
            throw new UnsupportedOperationException("Cannot remove from an inverse map through its key set");
        }
        return super.removeAll(op, elements, size);
    }
    
    @Override
    public void clear(final ObjectProvider op) {
        if (!this.canClear()) {
            throw new UnsupportedOperationException("Cannot clear an inverse map through its key set");
        }
        super.clear(op);
    }
    
    protected boolean canRemove() {
        return false;
    }
    
    protected boolean canClear() {
        return false;
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
            throw new NucleusDataStoreException(MapKeySetStore.LOCALISER.msg("056006", stmt), e);
        }
        catch (MappedDatastoreException e2) {
            throw new NucleusDataStoreException(MapKeySetStore.LOCALISER.msg("056006", stmt), e2);
        }
    }
    
    protected SQLStatement getSQLStatementForIterator(final ObjectProvider ownerOP) {
        SQLStatement sqlStmt = null;
        final ClassLoaderResolver clr = ownerOP.getExecutionContext().getClassLoaderResolver();
        final Class keyCls = clr.classForName(this.elementType);
        SQLTable containerSqlTbl = null;
        final MapMetaData.MapType mapType = this.getOwnerMemberMetaData().getMap().getMapType();
        if (this.emd != null && this.emd.getDiscriminatorStrategyForTable() != null && this.emd.getDiscriminatorStrategyForTable() != DiscriminatorStrategy.NONE) {
            if (ClassUtils.isReferenceType(keyCls)) {
                final String[] clsNames = this.storeMgr.getNucleusContext().getMetaDataManager().getClassesImplementingInterface(this.elementType, clr);
                final Class[] cls = new Class[clsNames.length];
                for (int j = 0; j < clsNames.length; ++j) {
                    cls[j] = clr.classForName(clsNames[j]);
                }
                sqlStmt = new DiscriminatorStatementGenerator(this.storeMgr, clr, cls, true, (DatastoreIdentifier)null, (String)null).getStatement();
            }
            else {
                sqlStmt = new DiscriminatorStatementGenerator(this.storeMgr, clr, clr.classForName(this.elementInfo[0].getClassName()), true, null, null).getStatement();
            }
            containerSqlTbl = sqlStmt.getPrimaryTable();
            this.iterateUsingDiscriminator = true;
            if (mapType == MapMetaData.MapType.MAP_TYPE_VALUE_IN_KEY) {
                containerSqlTbl = sqlStmt.getPrimaryTable();
                SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, this.iteratorMappingDef = new StatementClassMapping(), ownerOP.getExecutionContext().getFetchPlan(), sqlStmt.getPrimaryTable(), this.emd, 0);
            }
            else {
                final JavaTypeMapping keyIdMapping = sqlStmt.getPrimaryTable().getTable().getIdMapping();
                containerSqlTbl = sqlStmt.innerJoin(sqlStmt.getPrimaryTable(), keyIdMapping, this.containerTable, null, this.elementMapping, null, null);
                SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, this.iteratorMappingDef = new StatementClassMapping(), ownerOP.getExecutionContext().getFetchPlan(), sqlStmt.getPrimaryTable(), this.emd, 0);
            }
        }
        else if (mapType == MapMetaData.MapType.MAP_TYPE_VALUE_IN_KEY) {
            this.iteratorMappingDef = new StatementClassMapping();
            final UnionStatementGenerator stmtGen = new UnionStatementGenerator(this.storeMgr, clr, keyCls, true, null, null);
            stmtGen.setOption("selectNucleusType");
            this.iteratorMappingDef.setNucleusTypeColumnName("NUCLEUS_TYPE");
            sqlStmt = stmtGen.getStatement();
            containerSqlTbl = sqlStmt.getPrimaryTable();
            SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, this.iteratorMappingDef, ownerOP.getExecutionContext().getFetchPlan(), sqlStmt.getPrimaryTable(), this.emd, 0);
        }
        else if (this.emd != null) {
            this.iteratorMappingDef = new StatementClassMapping();
            final UnionStatementGenerator stmtGen = new UnionStatementGenerator(this.storeMgr, clr, keyCls, true, null, null);
            stmtGen.setOption("selectNucleusType");
            this.iteratorMappingDef.setNucleusTypeColumnName("NUCLEUS_TYPE");
            sqlStmt = stmtGen.getStatement();
            final JavaTypeMapping keyIdMapping2 = sqlStmt.getPrimaryTable().getTable().getIdMapping();
            containerSqlTbl = sqlStmt.innerJoin(sqlStmt.getPrimaryTable(), keyIdMapping2, this.containerTable, null, this.elementMapping, null, null);
            SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, this.iteratorMappingDef, ownerOP.getExecutionContext().getFetchPlan(), sqlStmt.getPrimaryTable(), this.emd, 0);
        }
        else {
            sqlStmt = new SQLStatement(this.storeMgr, this.containerTable, null, null);
            sqlStmt.setClassLoaderResolver(clr);
            SQLTable elemSqlTblForKey;
            containerSqlTbl = (elemSqlTblForKey = sqlStmt.getPrimaryTable());
            if (this.elementMapping.getTable() != containerSqlTbl.getTable()) {
                elemSqlTblForKey = sqlStmt.getTableForDatastoreContainer(this.elementMapping.getTable());
                if (elemSqlTblForKey == null) {
                    elemSqlTblForKey = sqlStmt.innerJoin(sqlStmt.getPrimaryTable(), sqlStmt.getPrimaryTable().getTable().getIdMapping(), this.elementMapping.getTable(), null, this.elementMapping.getTable().getIdMapping(), null, null);
                }
            }
            sqlStmt.select(elemSqlTblForKey, this.elementMapping, null);
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
