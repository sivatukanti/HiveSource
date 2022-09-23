// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.mapping.MappingHelper;
import org.datanucleus.store.scostore.MapStore;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.mapping.java.SerialisedMapping;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.sql.UnionStatementGenerator;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.Transaction;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import java.sql.SQLException;
import org.datanucleus.store.rdbms.JDBCUtils;
import org.datanucleus.FetchPlan;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.table.JoinTable;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedKeyPCMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedPCMapping;
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import java.util.Collection;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.metadata.MapMetaData;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.ExecutionContext;
import java.util.Iterator;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.util.NoSuchElementException;
import java.util.HashSet;
import java.util.Map;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.MapTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.scostore.SetStore;
import org.datanucleus.store.rdbms.query.StatementParameterMapping;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;

public class JoinMapStore extends AbstractMapStore
{
    private String putStmt;
    private String updateStmt;
    private String removeStmt;
    private String clearStmt;
    private String getStmtLocked;
    private String getStmtUnlocked;
    private StatementClassMapping getMappingDef;
    private StatementParameterMapping getMappingParams;
    private SetStore keySetStore;
    private SetStore valueSetStore;
    private SetStore entrySetStore;
    protected final JavaTypeMapping adapterMapping;
    
    public JoinMapStore(final MapTable mapTable, final ClassLoaderResolver clr) {
        super(mapTable.getStoreManager(), clr);
        this.getStmtLocked = null;
        this.getStmtUnlocked = null;
        this.getMappingDef = null;
        this.getMappingParams = null;
        this.keySetStore = null;
        this.valueSetStore = null;
        this.entrySetStore = null;
        this.mapTable = mapTable;
        this.setOwner(mapTable.getOwnerMemberMetaData());
        this.ownerMapping = mapTable.getOwnerMapping();
        this.keyMapping = mapTable.getKeyMapping();
        this.valueMapping = mapTable.getValueMapping();
        this.adapterMapping = mapTable.getOrderMapping();
        this.keyType = mapTable.getKeyType();
        this.keysAreEmbedded = mapTable.isEmbeddedKey();
        this.keysAreSerialised = mapTable.isSerialisedKey();
        this.valueType = mapTable.getValueType();
        this.valuesAreEmbedded = mapTable.isEmbeddedValue();
        this.valuesAreSerialised = mapTable.isSerialisedValue();
        final Class key_class = clr.classForName(this.keyType);
        this.kmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(key_class, clr);
        final Class value_class = clr.classForName(this.valueType);
        if (ClassUtils.isReferenceType(value_class)) {
            NucleusLogger.PERSISTENCE.warn(JoinMapStore.LOCALISER.msg("056066", this.ownerMemberMetaData.getFullFieldName(), value_class.getName()));
            this.vmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForImplementationOfReference(value_class, null, clr);
            if (this.vmd != null) {
                this.valueType = value_class.getName();
                this.valueTable = this.storeMgr.getDatastoreClass(this.vmd.getFullClassName(), clr);
            }
        }
        else {
            this.vmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(value_class, clr);
            if (this.vmd != null) {
                this.valueType = this.vmd.getFullClassName();
                if (this.valuesAreEmbedded) {
                    this.valueTable = null;
                }
                else {
                    this.valueTable = this.storeMgr.getDatastoreClass(this.valueType, clr);
                }
            }
        }
        this.initialise();
        this.putStmt = this.getPutStmt();
        this.updateStmt = this.getUpdateStmt();
        this.removeStmt = this.getRemoveStmt();
        this.clearStmt = this.getClearStmt();
    }
    
    @Override
    public void putAll(final ObjectProvider op, final Map m) {
        if (m == null || m.size() == 0) {
            return;
        }
        final HashSet puts = new HashSet();
        final HashSet updates = new HashSet();
        for (final Map.Entry e : m.entrySet()) {
            final Object key = e.getKey();
            final Object value = e.getValue();
            this.validateKeyForWriting(op, key);
            this.validateValueForWriting(op, value);
            try {
                final Object oldValue = this.getValue(op, key);
                if (oldValue == value) {
                    continue;
                }
                updates.add(e);
            }
            catch (NoSuchElementException nsee) {
                puts.add(e);
            }
        }
        final boolean batched = this.allowsBatching();
        if (puts.size() > 0) {
            try {
                final ExecutionContext ec = op.getExecutionContext();
                final ManagedConnection mconn = this.storeMgr.getConnection(ec);
                try {
                    final Iterator iter = puts.iterator();
                    while (iter.hasNext()) {
                        final Map.Entry entry = iter.next();
                        this.internalPut(op, mconn, batched, entry.getKey(), entry.getValue(), !iter.hasNext());
                    }
                }
                finally {
                    mconn.release();
                }
            }
            catch (MappedDatastoreException e2) {
                throw new NucleusDataStoreException(JoinMapStore.LOCALISER.msg("056016", e2.getMessage()), e2);
            }
        }
        if (updates.size() > 0) {
            try {
                final ExecutionContext ec = op.getExecutionContext();
                final ManagedConnection mconn = this.storeMgr.getConnection(ec);
                try {
                    final Iterator iter = updates.iterator();
                    while (iter.hasNext()) {
                        final Map.Entry entry = iter.next();
                        this.internalUpdate(op, mconn, batched, entry.getKey(), entry.getValue(), !iter.hasNext());
                    }
                }
                finally {
                    mconn.release();
                }
            }
            catch (MappedDatastoreException mde) {
                throw new NucleusDataStoreException(JoinMapStore.LOCALISER.msg("056016", mde.getMessage()), mde);
            }
        }
    }
    
    @Override
    public Object put(final ObjectProvider op, final Object key, final Object value) {
        this.validateKeyForWriting(op, key);
        this.validateValueForWriting(op, value);
        boolean exists = false;
        Object oldValue;
        try {
            oldValue = this.getValue(op, key);
            exists = true;
        }
        catch (NoSuchElementException e2) {
            oldValue = null;
            exists = false;
        }
        if (oldValue != value) {
            try {
                final ExecutionContext ec = op.getExecutionContext();
                final ManagedConnection mconn = this.storeMgr.getConnection(ec);
                try {
                    if (exists) {
                        this.internalUpdate(op, mconn, false, key, value, true);
                    }
                    else {
                        this.internalPut(op, mconn, false, key, value, true);
                    }
                }
                finally {
                    mconn.release();
                }
            }
            catch (MappedDatastoreException e) {
                throw new NucleusDataStoreException(JoinMapStore.LOCALISER.msg("056016", e.getMessage()), e);
            }
        }
        final MapMetaData mapmd = this.ownerMemberMetaData.getMap();
        if (mapmd.isDependentValue() && !mapmd.isEmbeddedValue() && oldValue != null && !this.containsValue(op, oldValue)) {
            op.getExecutionContext().deleteObjectInternal(oldValue);
        }
        return oldValue;
    }
    
    @Override
    public Object remove(final ObjectProvider op, final Object key) {
        if (!this.validateKeyForReading(op, key)) {
            return null;
        }
        Object oldValue;
        boolean exists;
        try {
            oldValue = this.getValue(op, key);
            exists = true;
        }
        catch (NoSuchElementException e) {
            oldValue = null;
            exists = false;
        }
        final ExecutionContext ec = op.getExecutionContext();
        if (exists) {
            this.removeInternal(op, key);
        }
        final MapMetaData mapmd = this.ownerMemberMetaData.getMap();
        final ApiAdapter api = ec.getApiAdapter();
        if (mapmd.isDependentKey() && !mapmd.isEmbeddedKey() && api.isPersistable(key)) {
            ec.deleteObjectInternal(key);
        }
        if (mapmd.isDependentValue() && !mapmd.isEmbeddedValue() && api.isPersistable(oldValue) && !this.containsValue(op, oldValue)) {
            ec.deleteObjectInternal(oldValue);
        }
        return oldValue;
    }
    
    @Override
    public Object remove(final ObjectProvider op, final Object key, final Object oldValue) {
        if (!this.validateKeyForReading(op, key)) {
            return null;
        }
        final ExecutionContext ec = op.getExecutionContext();
        this.removeInternal(op, key);
        final MapMetaData mapmd = this.ownerMemberMetaData.getMap();
        final ApiAdapter api = ec.getApiAdapter();
        if (mapmd.isDependentKey() && !mapmd.isEmbeddedKey() && api.isPersistable(key)) {
            ec.deleteObjectInternal(key);
        }
        if (mapmd.isDependentValue() && !mapmd.isEmbeddedValue() && api.isPersistable(oldValue) && !this.containsValue(op, oldValue)) {
            ec.deleteObjectInternal(oldValue);
        }
        return oldValue;
    }
    
    @Override
    public void clear(final ObjectProvider ownerOP) {
        Collection dependentElements = null;
        if (this.ownerMemberMetaData.getMap().isDependentKey() || this.ownerMemberMetaData.getMap().isDependentValue()) {
            dependentElements = new HashSet();
            final ApiAdapter api = ownerOP.getExecutionContext().getApiAdapter();
            final Iterator iter = this.entrySetStore().iterator(ownerOP);
            while (iter.hasNext()) {
                final Map.Entry entry = iter.next();
                final MapMetaData mapmd = this.ownerMemberMetaData.getMap();
                if (api.isPersistable(entry.getKey()) && mapmd.isDependentKey() && !mapmd.isEmbeddedKey()) {
                    dependentElements.add(entry.getKey());
                }
                if (api.isPersistable(entry.getValue()) && mapmd.isDependentValue() && !mapmd.isEmbeddedValue()) {
                    dependentElements.add(entry.getValue());
                }
            }
        }
        this.clearInternal(ownerOP);
        if (dependentElements != null && dependentElements.size() > 0) {
            ownerOP.getExecutionContext().deleteObjects(dependentElements.toArray());
        }
    }
    
    @Override
    public synchronized SetStore keySetStore() {
        if (this.keySetStore == null) {
            this.keySetStore = this.newMapKeySetStore();
        }
        return this.keySetStore;
    }
    
    @Override
    public synchronized SetStore valueSetStore() {
        if (this.valueSetStore == null) {
            this.valueSetStore = this.newMapValueSetStore();
        }
        return this.valueSetStore;
    }
    
    @Override
    public synchronized SetStore entrySetStore() {
        if (this.entrySetStore == null) {
            this.entrySetStore = this.newMapEntrySetStore();
        }
        return this.entrySetStore;
    }
    
    public JavaTypeMapping getAdapterMapping() {
        return this.adapterMapping;
    }
    
    private String getPutStmt() {
        final StringBuffer stmt = new StringBuffer("INSERT INTO ");
        stmt.append(this.mapTable.toString());
        stmt.append(" (");
        for (int i = 0; i < this.valueMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(this.valueMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
        }
        for (int i = 0; i < this.ownerMapping.getNumberOfDatastoreMappings(); ++i) {
            stmt.append(",");
            stmt.append(this.ownerMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
        }
        if (this.adapterMapping != null) {
            for (int i = 0; i < this.adapterMapping.getNumberOfDatastoreMappings(); ++i) {
                stmt.append(",");
                stmt.append(this.adapterMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
            }
        }
        for (int i = 0; i < this.keyMapping.getNumberOfDatastoreMappings(); ++i) {
            stmt.append(",");
            stmt.append(this.keyMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
        }
        stmt.append(") VALUES (");
        for (int i = 0; i < this.valueMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(((AbstractDatastoreMapping)this.valueMapping.getDatastoreMapping(i)).getInsertionInputParameter());
        }
        for (int i = 0; i < this.ownerMapping.getNumberOfDatastoreMappings(); ++i) {
            stmt.append(",");
            stmt.append(((AbstractDatastoreMapping)this.ownerMapping.getDatastoreMapping(i)).getInsertionInputParameter());
        }
        if (this.adapterMapping != null) {
            for (int i = 0; i < this.adapterMapping.getNumberOfDatastoreMappings(); ++i) {
                stmt.append(",");
                stmt.append(((AbstractDatastoreMapping)this.adapterMapping.getDatastoreMapping(i)).getInsertionInputParameter());
            }
        }
        for (int i = 0; i < this.keyMapping.getNumberOfDatastoreMappings(); ++i) {
            stmt.append(",");
            stmt.append(((AbstractDatastoreMapping)this.keyMapping.getDatastoreMapping(i)).getInsertionInputParameter());
        }
        stmt.append(") ");
        return stmt.toString();
    }
    
    private String getUpdateStmt() {
        final StringBuffer stmt = new StringBuffer("UPDATE ");
        stmt.append(this.mapTable.toString());
        stmt.append(" SET ");
        for (int i = 0; i < this.valueMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(this.valueMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
            stmt.append(" = ");
            stmt.append(((AbstractDatastoreMapping)this.valueMapping.getDatastoreMapping(i)).getUpdateInputParameter());
        }
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
        BackingStoreHelper.appendWhereClauseForMapping(stmt, this.keyMapping, null, false);
        return stmt.toString();
    }
    
    private String getRemoveStmt() {
        final StringBuffer stmt = new StringBuffer("DELETE FROM ");
        stmt.append(this.mapTable.toString());
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
        BackingStoreHelper.appendWhereClauseForMapping(stmt, this.keyMapping, null, false);
        return stmt.toString();
    }
    
    private String getClearStmt() {
        final StringBuffer stmt = new StringBuffer("DELETE FROM ");
        stmt.append(this.mapTable.toString());
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
        return stmt.toString();
    }
    
    @Override
    protected Object getValue(final ObjectProvider ownerOP, final Object key) throws NoSuchElementException {
        if (!this.validateKeyForReading(ownerOP, key)) {
            return null;
        }
        final ExecutionContext ec = ownerOP.getExecutionContext();
        if (this.getStmtLocked == null) {
            synchronized (this) {
                final SQLStatement sqlStmt = this.getSQLStatementForGet(ownerOP);
                this.getStmtUnlocked = sqlStmt.getSelectStatement().toSQL();
                sqlStmt.addExtension("lock-for-update", true);
                this.getStmtLocked = sqlStmt.getSelectStatement().toSQL();
            }
        }
        final Transaction tx = ec.getTransaction();
        final String stmt = (tx.getSerializeRead() != null && tx.getSerializeRead()) ? this.getStmtLocked : this.getStmtUnlocked;
        Object value = null;
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, stmt);
                final StatementMappingIndex ownerIdx = this.getMappingParams.getMappingForParameter("owner");
                for (int numParams = ownerIdx.getNumberOfParameterOccurrences(), paramInstance = 0; paramInstance < numParams; ++paramInstance) {
                    ownerIdx.getMapping().setObject(ec, ps, ownerIdx.getParameterPositionsForOccurrence(paramInstance), ownerOP.getObject());
                }
                final StatementMappingIndex keyIdx = this.getMappingParams.getMappingForParameter("key");
                for (int numParams = keyIdx.getNumberOfParameterOccurrences(), paramInstance2 = 0; paramInstance2 < numParams; ++paramInstance2) {
                    keyIdx.getMapping().setObject(ec, ps, keyIdx.getParameterPositionsForOccurrence(paramInstance2), key);
                }
                try {
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, stmt, ps);
                    try {
                        final boolean found = rs.next();
                        if (!found) {
                            throw new NoSuchElementException();
                        }
                        if (this.valuesAreEmbedded || this.valuesAreSerialised) {
                            final int[] param = new int[this.valueMapping.getNumberOfDatastoreMappings()];
                            for (int i = 0; i < param.length; ++i) {
                                param[i] = i + 1;
                            }
                            if (this.valueMapping instanceof SerialisedPCMapping || this.valueMapping instanceof SerialisedReferenceMapping || this.valueMapping instanceof EmbeddedKeyPCMapping) {
                                final int ownerFieldNumber = ((JoinTable)this.mapTable).getOwnerMemberMetaData().getAbsoluteFieldNumber();
                                value = this.valueMapping.getObject(ec, rs, param, ownerOP, ownerFieldNumber);
                            }
                            else {
                                value = this.valueMapping.getObject(ec, rs, param);
                            }
                        }
                        else if (this.valueMapping instanceof ReferenceMapping) {
                            final int[] param = new int[this.valueMapping.getNumberOfDatastoreMappings()];
                            for (int i = 0; i < param.length; ++i) {
                                param[i] = i + 1;
                            }
                            value = this.valueMapping.getObject(ec, rs, param);
                        }
                        else {
                            final ResultObjectFactory rof = this.storeMgr.newResultObjectFactory(this.vmd, this.getMappingDef, false, null, this.clr.classForName(this.valueType));
                            value = rof.getObject(ec, rs);
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
            throw new NucleusDataStoreException(JoinMapStore.LOCALISER.msg("056014", stmt), e);
        }
        return value;
    }
    
    protected SQLStatement getSQLStatementForGet(final ObjectProvider ownerOP) {
        SQLStatement sqlStmt = null;
        final ClassLoaderResolver clr = ownerOP.getExecutionContext().getClassLoaderResolver();
        Class valueCls = clr.classForName(this.valueType);
        if (this.valuesAreEmbedded || this.valuesAreSerialised) {
            sqlStmt = new SQLStatement(this.storeMgr, this.mapTable, null, null);
            sqlStmt.setClassLoaderResolver(clr);
            sqlStmt.select(sqlStmt.getPrimaryTable(), this.valueMapping, null);
        }
        else {
            this.getMappingDef = new StatementClassMapping();
            if (!this.vmd.getFullClassName().equals(valueCls.getName())) {
                valueCls = clr.classForName(this.vmd.getFullClassName());
            }
            final UnionStatementGenerator stmtGen = new UnionStatementGenerator(this.storeMgr, clr, valueCls, true, null, null, this.mapTable, null, this.valueMapping);
            stmtGen.setOption("selectNucleusType");
            this.getMappingDef.setNucleusTypeColumnName("NUCLEUS_TYPE");
            sqlStmt = stmtGen.getStatement();
            final SQLTable valueSqlTbl = sqlStmt.getTable(this.valueTable, sqlStmt.getPrimaryTable().getGroupName());
            SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, this.getMappingDef, ownerOP.getExecutionContext().getFetchPlan(), valueSqlTbl, this.vmd, 0);
        }
        final SQLExpressionFactory exprFactory = this.storeMgr.getSQLExpressionFactory();
        final SQLTable ownerSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStmt, sqlStmt.getPrimaryTable(), this.ownerMapping);
        final SQLExpression ownerExpr = exprFactory.newExpression(sqlStmt, ownerSqlTbl, this.ownerMapping);
        final SQLExpression ownerVal = exprFactory.newLiteralParameter(sqlStmt, this.ownerMapping, null, "OWNER");
        sqlStmt.whereAnd(ownerExpr.eq(ownerVal), true);
        if (this.keyMapping instanceof SerialisedMapping) {
            final SQLExpression keyExpr = exprFactory.newExpression(sqlStmt, sqlStmt.getPrimaryTable(), this.keyMapping);
            final SQLExpression keyVal = exprFactory.newLiteralParameter(sqlStmt, this.keyMapping, null, "KEY");
            sqlStmt.whereAnd(new BooleanExpression(keyExpr, Expression.OP_LIKE, keyVal), true);
        }
        else {
            final SQLExpression keyExpr = exprFactory.newExpression(sqlStmt, sqlStmt.getPrimaryTable(), this.keyMapping);
            final SQLExpression keyVal = exprFactory.newLiteralParameter(sqlStmt, this.keyMapping, null, "KEY");
            sqlStmt.whereAnd(keyExpr.eq(keyVal), true);
        }
        int inputParamNum = 1;
        final StatementMappingIndex ownerIdx = new StatementMappingIndex(this.ownerMapping);
        final StatementMappingIndex keyIdx = new StatementMappingIndex(this.keyMapping);
        if (sqlStmt.getNumberOfUnions() > 0) {
            for (int j = 0; j < sqlStmt.getNumberOfUnions() + 1; ++j) {
                final int[] ownerPositions = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
                for (int k = 0; k < ownerPositions.length; ++k) {
                    ownerPositions[k] = inputParamNum++;
                }
                ownerIdx.addParameterOccurrence(ownerPositions);
                final int[] keyPositions = new int[this.keyMapping.getNumberOfDatastoreMappings()];
                for (int i = 0; i < keyPositions.length; ++i) {
                    keyPositions[i] = inputParamNum++;
                }
                keyIdx.addParameterOccurrence(keyPositions);
            }
        }
        else {
            final int[] ownerPositions2 = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
            for (int l = 0; l < ownerPositions2.length; ++l) {
                ownerPositions2[l] = inputParamNum++;
            }
            ownerIdx.addParameterOccurrence(ownerPositions2);
            final int[] keyPositions2 = new int[this.keyMapping.getNumberOfDatastoreMappings()];
            for (int k = 0; k < keyPositions2.length; ++k) {
                keyPositions2[k] = inputParamNum++;
            }
            keyIdx.addParameterOccurrence(keyPositions2);
        }
        (this.getMappingParams = new StatementParameterMapping()).addMappingForParameter("owner", ownerIdx);
        this.getMappingParams.addMappingForParameter("key", keyIdx);
        return sqlStmt;
    }
    
    protected void clearInternal(final ObjectProvider ownerOP) {
        try {
            final ExecutionContext ec = ownerOP.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, this.clearStmt, false);
                try {
                    final int jdbcPosition = 1;
                    BackingStoreHelper.populateOwnerInStatement(ownerOP, ec, ps, jdbcPosition, this);
                    sqlControl.executeStatementUpdate(ec, mconn, this.clearStmt, ps, true);
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
            throw new NucleusDataStoreException(JoinMapStore.LOCALISER.msg("056013", this.clearStmt), e);
        }
    }
    
    protected void removeInternal(final ObjectProvider op, final Object key) {
        final ExecutionContext ec = op.getExecutionContext();
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, this.removeStmt, false);
                try {
                    int jdbcPosition = 1;
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    BackingStoreHelper.populateKeyInStatement(ec, ps, key, jdbcPosition, this.keyMapping);
                    sqlControl.executeStatementUpdate(ec, mconn, this.removeStmt, ps, true);
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
            throw new NucleusDataStoreException(JoinMapStore.LOCALISER.msg("056012", this.removeStmt), e);
        }
    }
    
    protected SetStore newMapKeySetStore() {
        return new MapKeySetStore((MapTable)this.mapTable, this, this.clr);
    }
    
    protected SetStore newMapValueSetStore() {
        return new MapValueSetStore((MapTable)this.mapTable, this, this.clr);
    }
    
    protected SetStore newMapEntrySetStore() {
        return new MapEntrySetStore((MapTable)this.mapTable, this, this.clr);
    }
    
    protected void internalUpdate(final ObjectProvider ownerOP, final ManagedConnection conn, final boolean batched, final Object key, final Object value, final boolean executeNow) throws MappedDatastoreException {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        final SQLController sqlControl = this.storeMgr.getSQLController();
        try {
            final PreparedStatement ps = sqlControl.getStatementForUpdate(conn, this.updateStmt, false);
            try {
                int jdbcPosition = 1;
                if (this.valueMapping != null) {
                    jdbcPosition = BackingStoreHelper.populateValueInStatement(ec, ps, value, jdbcPosition, this.valueMapping);
                }
                else {
                    jdbcPosition = BackingStoreHelper.populateEmbeddedValueFieldsInStatement(ownerOP, value, ps, jdbcPosition, (JoinTable)this.mapTable, this);
                }
                jdbcPosition = BackingStoreHelper.populateOwnerInStatement(ownerOP, ec, ps, jdbcPosition, this);
                jdbcPosition = BackingStoreHelper.populateKeyInStatement(ec, ps, key, jdbcPosition, this.keyMapping);
                if (batched) {
                    ps.addBatch();
                }
                else {
                    sqlControl.executeStatementUpdate(ec, conn, this.updateStmt, ps, true);
                }
            }
            finally {
                sqlControl.closeStatement(conn, ps);
            }
        }
        catch (SQLException e) {
            throw new MappedDatastoreException(this.getUpdateStmt(), e);
        }
    }
    
    protected int[] internalPut(final ObjectProvider ownerOP, final ManagedConnection conn, final boolean batched, final Object key, final Object value, final boolean executeNow) throws MappedDatastoreException {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        final SQLController sqlControl = this.storeMgr.getSQLController();
        try {
            final PreparedStatement ps = sqlControl.getStatementForUpdate(conn, this.putStmt, false);
            try {
                int jdbcPosition = 1;
                if (this.valueMapping != null) {
                    jdbcPosition = BackingStoreHelper.populateValueInStatement(ec, ps, value, jdbcPosition, this.valueMapping);
                }
                else {
                    jdbcPosition = BackingStoreHelper.populateEmbeddedValueFieldsInStatement(ownerOP, value, ps, jdbcPosition, (JoinTable)this.mapTable, this);
                }
                jdbcPosition = BackingStoreHelper.populateOwnerInStatement(ownerOP, ec, ps, jdbcPosition, this);
                if (this.adapterMapping != null) {
                    final long nextIDAdapter = this.getNextIDForAdapterColumn(ownerOP);
                    this.adapterMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, this.adapterMapping), nextIDAdapter);
                    jdbcPosition += this.adapterMapping.getNumberOfDatastoreMappings();
                }
                jdbcPosition = BackingStoreHelper.populateKeyInStatement(ec, ps, key, jdbcPosition, this.keyMapping);
                return sqlControl.executeStatementUpdate(ec, conn, this.putStmt, ps, true);
            }
            finally {
                sqlControl.closeStatement(conn, ps);
            }
        }
        catch (SQLException e) {
            throw new MappedDatastoreException(this.getPutStmt(), e);
        }
    }
    
    private int getNextIDForAdapterColumn(final ObjectProvider op) {
        int nextID;
        try {
            final ExecutionContext ec = op.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final String stmt = this.getMaxAdapterColumnIdStmt();
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, stmt);
                try {
                    final int jdbcPosition = 1;
                    BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, stmt, ps);
                    try {
                        if (!rs.next()) {
                            nextID = 1;
                        }
                        else {
                            nextID = rs.getInt(1) + 1;
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
            throw new NucleusDataStoreException(JoinMapStore.LOCALISER.msg("056020", this.getMaxAdapterColumnIdStmt()), e);
        }
        return nextID;
    }
    
    private String getMaxAdapterColumnIdStmt() {
        final StringBuffer stmt = new StringBuffer("SELECT MAX(" + this.adapterMapping.getDatastoreMapping(0).getColumn().getIdentifier().toString() + ")");
        stmt.append(" FROM ");
        stmt.append(this.mapTable.toString());
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
        return stmt.toString();
    }
}
