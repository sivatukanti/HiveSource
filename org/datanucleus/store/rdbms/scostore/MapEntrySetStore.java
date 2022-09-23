// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.mapping.java.EmbeddedValuePCMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedPCMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedKeyPCMapping;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.Transaction;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import org.datanucleus.store.rdbms.table.JoinTable;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.ExecutionContext;
import java.sql.SQLException;
import org.datanucleus.store.rdbms.JDBCUtils;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.MapTable;
import org.datanucleus.store.rdbms.query.StatementParameterMapping;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.scostore.MapStore;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.scostore.SetStore;

class MapEntrySetStore extends BaseContainerStore implements SetStore
{
    protected Table mapTable;
    protected MapStore mapStore;
    protected JavaTypeMapping keyMapping;
    protected JavaTypeMapping valueMapping;
    private String sizeStmt;
    private String iteratorStmtLocked;
    private String iteratorStmtUnlocked;
    private StatementParameterMapping iteratorMappingParams;
    private int[] iteratorKeyResultCols;
    private int[] iteratorValueResultCols;
    
    MapEntrySetStore(final MapTable mapTable, final MapStore mapStore, final ClassLoaderResolver clr) {
        super(mapTable.getStoreManager(), clr);
        this.iteratorStmtLocked = null;
        this.iteratorStmtUnlocked = null;
        this.iteratorMappingParams = null;
        this.iteratorKeyResultCols = null;
        this.iteratorValueResultCols = null;
        this.mapTable = mapTable;
        this.mapStore = mapStore;
        this.ownerMapping = mapTable.getOwnerMapping();
        this.keyMapping = mapTable.getKeyMapping();
        this.valueMapping = mapTable.getValueMapping();
        this.ownerMemberMetaData = mapTable.getOwnerMemberMetaData();
    }
    
    MapEntrySetStore(final Table mapTable, final MapStore mapStore, final ClassLoaderResolver clr, final JavaTypeMapping ownerMapping, final JavaTypeMapping keyMapping, final JavaTypeMapping valueMapping, final AbstractMemberMetaData ownerMmd) {
        super(mapTable.getStoreManager(), clr);
        this.iteratorStmtLocked = null;
        this.iteratorStmtUnlocked = null;
        this.iteratorMappingParams = null;
        this.iteratorKeyResultCols = null;
        this.iteratorValueResultCols = null;
        this.mapTable = mapTable;
        this.mapStore = mapStore;
        this.ownerMapping = ownerMapping;
        this.keyMapping = keyMapping;
        this.valueMapping = valueMapping;
        this.ownerMemberMetaData = ownerMmd;
    }
    
    @Override
    public boolean hasOrderMapping() {
        return false;
    }
    
    @Override
    public boolean updateEmbeddedElement(final ObjectProvider sm, final Object element, final int fieldNumber, final Object value) {
        return false;
    }
    
    @Override
    public JavaTypeMapping getOwnerMapping() {
        return this.ownerMapping;
    }
    
    protected boolean validateElementType(final Object element) {
        return element instanceof Map.Entry;
    }
    
    @Override
    public void update(final ObjectProvider sm, final Collection coll) {
        this.clear(sm);
        this.addAll(sm, coll, 0);
    }
    
    @Override
    public boolean contains(final ObjectProvider sm, final Object element) {
        if (!this.validateElementType(element)) {
            return false;
        }
        final Map.Entry entry = (Map.Entry)element;
        return this.mapStore.containsKey(sm, entry.getKey());
    }
    
    @Override
    public boolean add(final ObjectProvider sm, final Object element, final int size) {
        throw new UnsupportedOperationException("Cannot add to a map through its entry set");
    }
    
    @Override
    public boolean addAll(final ObjectProvider sm, final Collection elements, final int size) {
        throw new UnsupportedOperationException("Cannot add to a map through its entry set");
    }
    
    @Override
    public boolean remove(final ObjectProvider sm, final Object element, final int size, final boolean allowDependentField) {
        if (!this.validateElementType(element)) {
            return false;
        }
        final Map.Entry entry = (Map.Entry)element;
        final Object removed = this.mapStore.remove(sm, entry.getKey());
        return (removed == null) ? (entry.getValue() == null) : removed.equals(entry.getValue());
    }
    
    @Override
    public boolean removeAll(final ObjectProvider sm, final Collection elements, final int size) {
        if (elements == null || elements.size() == 0) {
            return false;
        }
        final Iterator iter = elements.iterator();
        boolean modified = false;
        while (iter.hasNext()) {
            final Object element = iter.next();
            final Map.Entry entry = (Map.Entry)element;
            final Object removed = this.mapStore.remove(sm, entry.getKey());
            modified = ((removed == null) ? (entry.getValue() == null) : removed.equals(entry.getValue()));
        }
        return modified;
    }
    
    @Override
    public void clear(final ObjectProvider sm) {
        this.mapStore.clear(sm);
    }
    
    public MapStore getMapStore() {
        return this.mapStore;
    }
    
    public JavaTypeMapping getKeyMapping() {
        return this.keyMapping;
    }
    
    public JavaTypeMapping getValueMapping() {
        return this.valueMapping;
    }
    
    @Override
    public int size(final ObjectProvider sm) {
        final String stmt = this.getSizeStmt();
        int numRows;
        try {
            final ExecutionContext ec = sm.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, stmt);
                try {
                    final int jdbcPosition = 1;
                    BackingStoreHelper.populateOwnerInStatement(sm, ec, ps, jdbcPosition, this);
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, stmt, ps);
                    try {
                        if (!rs.next()) {
                            throw new NucleusDataStoreException("Size request returned no result row: " + stmt);
                        }
                        numRows = rs.getInt(1);
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
            throw new NucleusDataStoreException("Size request failed: " + stmt, e);
        }
        return numRows;
    }
    
    private String getSizeStmt() {
        if (this.sizeStmt == null) {
            final StringBuffer stmt = new StringBuffer("SELECT COUNT(*) FROM ");
            stmt.append(this.mapTable.toString());
            stmt.append(" WHERE ");
            BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
            if (this.keyMapping != null) {
                for (int i = 0; i < this.keyMapping.getNumberOfDatastoreMappings(); ++i) {
                    stmt.append(" AND ");
                    stmt.append(this.keyMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                    stmt.append(" IS NOT NULL");
                }
            }
            this.sizeStmt = stmt.toString();
        }
        return this.sizeStmt;
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
                        AbstractMemberMetaData ownerMemberMetaData = null;
                        if (this.mapTable instanceof JoinTable) {
                            ownerMemberMetaData = ((JoinTable)this.mapTable).getOwnerMemberMetaData();
                        }
                        return new SetIterator(ownerOP, this, ownerMemberMetaData, rs, this.iteratorKeyResultCols, this.iteratorValueResultCols) {
                            @Override
                            protected boolean next(final Object rs) throws MappedDatastoreException {
                                try {
                                    return ((ResultSet)rs).next();
                                }
                                catch (SQLException e) {
                                    throw new MappedDatastoreException("SQLException", e);
                                }
                            }
                        };
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
            throw new NucleusDataStoreException("Iteration request failed: " + stmt, e);
        }
        catch (MappedDatastoreException e2) {
            throw new NucleusDataStoreException("Iteration request failed: " + stmt, e2);
        }
    }
    
    protected SQLStatement getSQLStatementForIterator(final ObjectProvider ownerOP) {
        final SQLStatement sqlStmt = new SQLStatement(this.storeMgr, this.mapTable, null, null);
        sqlStmt.setClassLoaderResolver(this.clr);
        SQLTable entrySqlTblForKey = sqlStmt.getPrimaryTable();
        if (this.keyMapping.getTable() != sqlStmt.getPrimaryTable().getTable()) {
            entrySqlTblForKey = sqlStmt.getTableForDatastoreContainer(this.keyMapping.getTable());
            if (entrySqlTblForKey == null) {
                entrySqlTblForKey = sqlStmt.innerJoin(sqlStmt.getPrimaryTable(), sqlStmt.getPrimaryTable().getTable().getIdMapping(), this.keyMapping.getTable(), null, this.keyMapping.getTable().getIdMapping(), null, null);
            }
        }
        this.iteratorKeyResultCols = sqlStmt.select(entrySqlTblForKey, this.keyMapping, null);
        SQLTable entrySqlTblForVal = sqlStmt.getPrimaryTable();
        if (this.valueMapping.getTable() != sqlStmt.getPrimaryTable().getTable()) {
            entrySqlTblForVal = sqlStmt.getTableForDatastoreContainer(this.valueMapping.getTable());
            if (entrySqlTblForVal == null) {
                entrySqlTblForVal = sqlStmt.innerJoin(sqlStmt.getPrimaryTable(), sqlStmt.getPrimaryTable().getTable().getIdMapping(), this.valueMapping.getTable(), null, this.valueMapping.getTable().getIdMapping(), null, null);
            }
        }
        this.iteratorValueResultCols = sqlStmt.select(entrySqlTblForVal, this.valueMapping, null);
        final SQLExpressionFactory exprFactory = this.storeMgr.getSQLExpressionFactory();
        final SQLTable ownerSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStmt, sqlStmt.getPrimaryTable(), this.ownerMapping);
        final SQLExpression ownerExpr = exprFactory.newExpression(sqlStmt, ownerSqlTbl, this.ownerMapping);
        final SQLExpression ownerVal = exprFactory.newLiteralParameter(sqlStmt, this.ownerMapping, null, "OWNER");
        sqlStmt.whereAnd(ownerExpr.eq(ownerVal), true);
        final SQLExpression keyExpr = exprFactory.newExpression(sqlStmt, sqlStmt.getPrimaryTable(), this.keyMapping);
        final SQLExpression nullExpr = exprFactory.newLiteral(sqlStmt, null, null);
        sqlStmt.whereAnd(keyExpr.ne(nullExpr), true);
        int inputParamNum = 1;
        final StatementMappingIndex ownerIdx = new StatementMappingIndex(this.ownerMapping);
        if (sqlStmt.getNumberOfUnions() > 0) {
            for (int j = 0; j < sqlStmt.getNumberOfUnions() + 1; ++j) {
                final int[] paramPositions = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
                for (int k = 0; k < this.ownerMapping.getNumberOfDatastoreMappings(); ++k) {
                    paramPositions[k] = inputParamNum++;
                }
                ownerIdx.addParameterOccurrence(paramPositions);
            }
        }
        else {
            final int[] paramPositions2 = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
            for (int i = 0; i < this.ownerMapping.getNumberOfDatastoreMappings(); ++i) {
                paramPositions2[i] = inputParamNum++;
            }
            ownerIdx.addParameterOccurrence(paramPositions2);
        }
        (this.iteratorMappingParams = new StatementParameterMapping()).addMappingForParameter("owner", ownerIdx);
        return sqlStmt;
    }
    
    public abstract static class SetIterator implements Iterator
    {
        private final ObjectProvider sm;
        private final ExecutionContext ec;
        private final Iterator delegate;
        private Map.Entry lastElement;
        private final MapEntrySetStore setStore;
        
        protected SetIterator(final ObjectProvider sm, final MapEntrySetStore setStore, final AbstractMemberMetaData ownerMmd, final ResultSet rs, final int[] keyResultCols, final int[] valueResultCols) throws MappedDatastoreException {
            this.lastElement = null;
            this.sm = sm;
            this.ec = sm.getExecutionContext();
            this.setStore = setStore;
            final ArrayList results = new ArrayList();
            while (this.next(rs)) {
                Object key = null;
                Object value = null;
                int ownerFieldNum = -1;
                if (ownerMmd != null) {
                    ownerFieldNum = ownerMmd.getAbsoluteFieldNumber();
                }
                final JavaTypeMapping keyMapping = setStore.getKeyMapping();
                if (keyMapping instanceof EmbeddedKeyPCMapping || keyMapping instanceof SerialisedPCMapping || keyMapping instanceof SerialisedReferenceMapping) {
                    key = keyMapping.getObject(this.ec, rs, keyResultCols, sm, ownerFieldNum);
                }
                else {
                    key = keyMapping.getObject(this.ec, rs, keyResultCols);
                }
                final JavaTypeMapping valueMapping = setStore.getValueMapping();
                if (valueMapping instanceof EmbeddedValuePCMapping || valueMapping instanceof SerialisedPCMapping || valueMapping instanceof SerialisedReferenceMapping) {
                    value = valueMapping.getObject(this.ec, rs, valueResultCols, sm, ownerFieldNum);
                }
                else {
                    value = valueMapping.getObject(this.ec, rs, valueResultCols);
                }
                results.add(new EntryImpl(sm, key, value, setStore.getMapStore()));
            }
            this.delegate = results.iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.delegate.hasNext();
        }
        
        @Override
        public Object next() {
            return this.lastElement = this.delegate.next();
        }
        
        @Override
        public synchronized void remove() {
            if (this.lastElement == null) {
                throw new IllegalStateException("No entry to remove");
            }
            this.setStore.getMapStore().remove(this.sm, this.lastElement.getKey());
            this.delegate.remove();
            this.lastElement = null;
        }
        
        protected abstract boolean next(final Object p0) throws MappedDatastoreException;
    }
    
    private static class EntryImpl implements Map.Entry
    {
        private final ObjectProvider sm;
        private final Object key;
        private final Object value;
        private final MapStore mapStore;
        
        public EntryImpl(final ObjectProvider sm, final Object key, final Object value, final MapStore mapStore) {
            this.sm = sm;
            this.key = key;
            this.value = value;
            this.mapStore = mapStore;
        }
        
        @Override
        public int hashCode() {
            return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry e = (Map.Entry)o;
            if (this.key == null) {
                if (e.getKey() != null) {
                    return false;
                }
            }
            else if (!this.key.equals(e.getKey())) {
                return false;
            }
            if ((this.value != null) ? this.value.equals(e.getValue()) : (e.getValue() == null)) {
                return true;
            }
            return false;
        }
        
        @Override
        public Object getKey() {
            return this.key;
        }
        
        @Override
        public Object getValue() {
            return this.value;
        }
        
        @Override
        public Object setValue(final Object value) {
            return this.mapStore.put(this.sm, this.key, value);
        }
    }
}
