// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.StatementGenerator;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.store.scostore.Store;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.sql.UnionStatementGenerator;
import org.datanucleus.store.rdbms.sql.DiscriminatorStatementGenerator;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.FetchPlan;
import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.store.rdbms.JDBCUtils;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.exceptions.NotYetFlushedException;
import org.datanucleus.store.rdbms.fieldmanager.DynamicSchemaFieldManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.StringUtils;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.store.FieldValues;
import org.datanucleus.ExecutionContext;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.types.SCOMtoN;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collection;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.CollectionTable;
import org.datanucleus.metadata.AbstractMemberMetaData;

public class JoinSetStore extends AbstractSetStore
{
    protected String locateStmt;
    protected String maxOrderColumnIdStmt;
    
    public JoinSetStore(final AbstractMemberMetaData mmd, final CollectionTable joinTable, final ClassLoaderResolver clr) {
        super(joinTable.getStoreManager(), clr);
        this.containerTable = joinTable;
        this.setOwner(mmd);
        this.ownerMapping = joinTable.getOwnerMapping();
        this.elementMapping = joinTable.getElementMapping();
        this.orderMapping = joinTable.getOrderMapping();
        this.relationDiscriminatorMapping = joinTable.getRelationDiscriminatorMapping();
        this.relationDiscriminatorValue = joinTable.getRelationDiscriminatorValue();
        this.elementType = mmd.getCollection().getElementType();
        this.elementsAreEmbedded = joinTable.isEmbeddedElement();
        this.elementsAreSerialised = joinTable.isSerialisedElement();
        if (this.elementsAreSerialised) {
            this.elementInfo = null;
        }
        else {
            final Class element_class = clr.classForName(this.elementType);
            if (ClassUtils.isReferenceType(element_class)) {
                final String[] implNames = MetaDataUtils.getInstance().getImplementationNamesForReferenceField(this.ownerMemberMetaData, 3, clr, this.storeMgr.getMetaDataManager());
                this.elementInfo = new ElementInfo[implNames.length];
                for (int i = 0; i < implNames.length; ++i) {
                    final DatastoreClass table = this.storeMgr.getDatastoreClass(implNames[i], clr);
                    final AbstractClassMetaData cmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(implNames[i], clr);
                    this.elementInfo[i] = new ElementInfo(cmd, table);
                }
            }
            else {
                this.emd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(element_class, clr);
                if (this.emd != null && !this.elementsAreEmbedded) {
                    this.elementInfo = this.getElementInformationForClass();
                }
                else {
                    this.elementInfo = null;
                }
            }
        }
    }
    
    @Override
    public void update(final ObjectProvider op, final Collection coll) {
        if (coll == null || coll.isEmpty()) {
            this.clear(op);
            return;
        }
        if (this.ownerMemberMetaData.getCollection().isSerializedElement() || this.ownerMemberMetaData.getCollection().isEmbeddedElement()) {
            this.clear(op);
            this.addAll(op, coll, 0);
            return;
        }
        final Iterator elemIter = this.iterator(op);
        final Collection existing = new HashSet();
        while (elemIter.hasNext()) {
            final Object elem = elemIter.next();
            if (!coll.contains(elem)) {
                this.remove(op, elem, -1, true);
            }
            else {
                existing.add(elem);
            }
        }
        if (existing.size() != coll.size()) {
            for (final Object elem2 : coll) {
                if (!existing.contains(elem2)) {
                    this.add(op, elem2, 0);
                }
            }
        }
    }
    
    @Override
    public boolean removeAll(final ObjectProvider op, final Collection elements, final int size) {
        if (elements == null || elements.size() == 0) {
            return false;
        }
        final boolean modified = this.removeAllInternal(op, elements, size);
        boolean dependent = this.ownerMemberMetaData.getCollection().isDependentElement();
        if (this.ownerMemberMetaData.isCascadeRemoveOrphans()) {
            dependent = true;
        }
        if (dependent) {
            op.getExecutionContext().deleteObjects(elements.toArray());
        }
        return modified;
    }
    
    private boolean elementAlreadyContainsOwnerInMtoN(final ObjectProvider ownerOP, final Object element) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        Object elementColl = null;
        final ObjectProvider elementSM = ec.findObjectProvider(element);
        if (elementSM != null) {
            final AbstractMemberMetaData[] relatedMmds = this.ownerMemberMetaData.getRelatedMemberMetaData(ec.getClassLoaderResolver());
            elementColl = elementSM.provideField(relatedMmds[0].getAbsoluteFieldNumber());
        }
        if (elementColl != null && elementColl instanceof SCOMtoN) {
            if (((SCOMtoN)elementColl).contains(ownerOP.getObject())) {
                NucleusLogger.DATASTORE.info(JoinSetStore.LOCALISER.msg("056040", this.ownerMemberMetaData.getFullFieldName(), element));
                return true;
            }
        }
        else if (this.locate(ownerOP, element)) {
            NucleusLogger.DATASTORE.info(JoinSetStore.LOCALISER.msg("056040", this.ownerMemberMetaData.getFullFieldName(), element));
            return true;
        }
        return false;
    }
    
    @Override
    public boolean add(final ObjectProvider op, final Object element, final int size) {
        final ExecutionContext ec = op.getExecutionContext();
        this.validateElementForWriting(ec, element, null);
        if (this.relationType == RelationType.ONE_TO_MANY_BI) {
            final ObjectProvider elementSM = ec.findObjectProvider(element);
            if (elementSM != null) {
                final AbstractMemberMetaData[] relatedMmds = this.ownerMemberMetaData.getRelatedMemberMetaData(this.clr);
                final Object elementOwner = elementSM.provideField(relatedMmds[0].getAbsoluteFieldNumber());
                if (elementOwner == null) {
                    NucleusLogger.PERSISTENCE.info(JoinSetStore.LOCALISER.msg("056037", op.getObjectAsPrintable(), this.ownerMemberMetaData.getFullFieldName(), StringUtils.toJVMIDString(elementSM.getObject())));
                    elementSM.replaceField(relatedMmds[0].getAbsoluteFieldNumber(), op.getObject());
                }
                else if (elementOwner != op.getObject() && op.getReferencedPC() == null) {
                    throw new NucleusUserException(JoinSetStore.LOCALISER.msg("056038", op.getObjectAsPrintable(), this.ownerMemberMetaData.getFullFieldName(), StringUtils.toJVMIDString(elementSM.getObject()), StringUtils.toJVMIDString(elementOwner)));
                }
            }
        }
        boolean modified = false;
        boolean toBeInserted = true;
        if (this.relationType == RelationType.MANY_TO_MANY_BI) {
            toBeInserted = !this.elementAlreadyContainsOwnerInMtoN(op, element);
        }
        if (toBeInserted) {
            try {
                final ManagedConnection mconn = this.storeMgr.getConnection(ec);
                try {
                    int orderID = -1;
                    if (this.orderMapping != null) {
                        orderID = this.getNextIDForOrderColumn(op);
                    }
                    final int[] returnCode = this.internalAdd(op, element, mconn, false, orderID, true);
                    if (returnCode[0] > 0) {
                        modified = true;
                    }
                }
                finally {
                    mconn.release();
                }
            }
            catch (MappedDatastoreException e) {
                NucleusLogger.DATASTORE.error(e);
                final String msg = JoinSetStore.LOCALISER.msg("056009", e.getMessage());
                NucleusLogger.DATASTORE.error(msg);
                throw new NucleusDataStoreException(msg, e);
            }
        }
        return modified;
    }
    
    @Override
    public boolean addAll(final ObjectProvider op, final Collection elements, final int size) {
        if (elements == null || elements.size() == 0) {
            return false;
        }
        boolean modified = false;
        final List exceptions = new ArrayList();
        final boolean batched = elements.size() > 1;
        final ExecutionContext ec = op.getExecutionContext();
        for (final Object element : elements) {
            this.validateElementForWriting(ec, element, null);
            if (this.relationType == RelationType.ONE_TO_MANY_BI) {
                final ObjectProvider elementSM = op.getExecutionContext().findObjectProvider(element);
                if (elementSM == null) {
                    continue;
                }
                final AbstractMemberMetaData[] relatedMmds = this.ownerMemberMetaData.getRelatedMemberMetaData(this.clr);
                final Object elementOwner = elementSM.provideField(relatedMmds[0].getAbsoluteFieldNumber());
                if (elementOwner == null) {
                    NucleusLogger.PERSISTENCE.info(JoinSetStore.LOCALISER.msg("056037", op.getObjectAsPrintable(), this.ownerMemberMetaData.getFullFieldName(), StringUtils.toJVMIDString(elementSM.getObject())));
                    elementSM.replaceField(relatedMmds[0].getAbsoluteFieldNumber(), op.getObject());
                }
                else {
                    if (elementOwner != op.getObject() && op.getReferencedPC() == null) {
                        throw new NucleusUserException(JoinSetStore.LOCALISER.msg("056038", op.getObjectAsPrintable(), this.ownerMemberMetaData.getFullFieldName(), StringUtils.toJVMIDString(elementSM.getObject()), StringUtils.toJVMIDString(elementOwner)));
                    }
                    continue;
                }
            }
        }
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            try {
                this.preGetNextIDForOrderColumn(mconn);
                int nextOrderID = 0;
                if (this.orderMapping != null) {
                    nextOrderID = this.getNextIDForOrderColumn(op);
                }
                final Iterator iter = elements.iterator();
                Object element2 = null;
                while (iter.hasNext()) {
                    element2 = iter.next();
                    try {
                        final int[] rc = this.internalAdd(op, element2, mconn, batched, nextOrderID, !batched || (batched && !iter.hasNext()));
                        if (rc != null) {
                            for (int i = 0; i < rc.length; ++i) {
                                if (rc[i] > 0) {
                                    modified = true;
                                }
                            }
                        }
                        ++nextOrderID;
                    }
                    catch (MappedDatastoreException mde) {
                        exceptions.add(mde);
                        NucleusLogger.DATASTORE.error("Exception thrown", mde);
                    }
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (MappedDatastoreException e) {
            exceptions.add(e);
            NucleusLogger.DATASTORE.error("Exception thrown", e);
        }
        if (!exceptions.isEmpty()) {
            final String msg = JoinSetStore.LOCALISER.msg("056009", exceptions.get(0).getMessage());
            NucleusLogger.DATASTORE.error(msg);
            throw new NucleusDataStoreException(msg, exceptions.toArray(new Throwable[exceptions.size()]), op.getObject());
        }
        return modified;
    }
    
    private int[] internalAdd(final ObjectProvider op, final Object element, final ManagedConnection conn, final boolean batched, final int orderId, final boolean executeNow) throws MappedDatastoreException {
        boolean toBeInserted = true;
        if (this.relationType == RelationType.MANY_TO_MANY_BI) {
            toBeInserted = !this.elementAlreadyContainsOwnerInMtoN(op, element);
        }
        if (toBeInserted) {
            return this.doInternalAdd(op, element, conn, batched, orderId, executeNow);
        }
        return null;
    }
    
    protected boolean removeAllInternal(final ObjectProvider op, final Collection elements, final int size) {
        boolean modified = false;
        final String removeAllStmt = this.getRemoveAllStmt(op, elements);
        try {
            final ExecutionContext ec = op.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, removeAllStmt, false);
                try {
                    int jdbcPosition = 1;
                    for (final Object element : elements) {
                        jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                        jdbcPosition = BackingStoreHelper.populateElementForWhereClauseInStatement(ec, ps, element, jdbcPosition, this.elementMapping);
                        if (this.relationDiscriminatorMapping != null) {
                            jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                        }
                    }
                    final int[] number = sqlControl.executeStatementUpdate(ec, mconn, removeAllStmt, ps, true);
                    if (number[0] > 0) {
                        modified = true;
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
            NucleusLogger.DATASTORE.error(e);
            throw new NucleusDataStoreException(JoinSetStore.LOCALISER.msg("056012", removeAllStmt), e);
        }
        return modified;
    }
    
    @Override
    protected String getRemoveStmt(final Object element) {
        final StringBuffer stmt = new StringBuffer("DELETE FROM ");
        stmt.append(this.containerTable.toString());
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
        BackingStoreHelper.appendWhereClauseForElement(stmt, this.elementMapping, element, this.elementsAreSerialised, null, false);
        if (this.relationDiscriminatorMapping != null) {
            BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
        }
        return stmt.toString();
    }
    
    protected String getRemoveAllStmt(final ObjectProvider op, final Collection elements) {
        if (elements == null || elements.size() == 0) {
            return null;
        }
        final StringBuffer stmt = new StringBuffer("DELETE FROM ");
        stmt.append(this.containerTable.toString());
        stmt.append(" WHERE ");
        final Iterator elementsIter = elements.iterator();
        boolean first = true;
        while (elementsIter.hasNext()) {
            final Object element = elementsIter.next();
            if (first) {
                stmt.append("(");
            }
            else {
                stmt.append(" OR (");
            }
            BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
            BackingStoreHelper.appendWhereClauseForElement(stmt, this.elementMapping, element, this.elementsAreSerialised, null, false);
            if (this.relationDiscriminatorMapping != null) {
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
            }
            stmt.append(")");
            first = false;
        }
        return stmt.toString();
    }
    
    public boolean locate(final ObjectProvider op, final Object element) {
        boolean exists = true;
        final String stmt = this.getLocateStmt(element);
        try {
            final ExecutionContext ec = op.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, stmt);
                try {
                    int jdbcPosition = 1;
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    jdbcPosition = BackingStoreHelper.populateElementForWhereClauseInStatement(ec, ps, element, jdbcPosition, this.elementMapping);
                    if (this.relationDiscriminatorMapping != null) {
                        jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                    }
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, stmt, ps);
                    try {
                        if (!rs.next()) {
                            exists = false;
                        }
                    }
                    catch (SQLException sqle) {
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
            NucleusLogger.DATASTORE.error(e);
            throw new NucleusDataStoreException(JoinSetStore.LOCALISER.msg("RDBMS.SCO.LocateRequestFailed", stmt), e);
        }
        return exists;
    }
    
    protected int[] doInternalAdd(final ObjectProvider op, final Object element, final ManagedConnection conn, final boolean batched, final int orderId, final boolean executeNow) throws MappedDatastoreException {
        if (this.storeMgr.getBooleanObjectProperty("datanucleus.rdbms.dynamicSchemaUpdates")) {
            final DynamicSchemaFieldManager dynamicSchemaFM = new DynamicSchemaFieldManager(this.storeMgr, op);
            final Collection coll = new HashSet();
            coll.add(element);
            dynamicSchemaFM.storeObjectField(this.ownerMemberMetaData.getAbsoluteFieldNumber(), coll);
            if (dynamicSchemaFM.hasPerformedSchemaUpdates()) {
                this.invalidateAddStmt();
            }
        }
        final String addStmt = this.getAddStmt();
        boolean notYetFlushedError = false;
        final ExecutionContext ec = op.getExecutionContext();
        final SQLController sqlControl = this.storeMgr.getSQLController();
        try {
            final PreparedStatement ps = sqlControl.getStatementForUpdate(conn, addStmt, batched);
            try {
                int jdbcPosition = 1;
                jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                jdbcPosition = BackingStoreHelper.populateElementInStatement(ec, ps, element, jdbcPosition, this.elementMapping);
                if (this.orderMapping != null) {
                    jdbcPosition = BackingStoreHelper.populateOrderInStatement(ec, ps, orderId, jdbcPosition, this.orderMapping);
                }
                if (this.relationDiscriminatorMapping != null) {
                    jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                }
                return sqlControl.executeStatementUpdate(ec, conn, addStmt, ps, executeNow);
            }
            catch (NotYetFlushedException nfe) {
                notYetFlushedError = true;
                throw nfe;
            }
            finally {
                if (notYetFlushedError) {
                    sqlControl.abortStatementForConnection(conn, ps);
                }
                else {
                    sqlControl.closeStatement(conn, ps);
                }
            }
        }
        catch (SQLException e) {
            throw new MappedDatastoreException(addStmt, e);
        }
    }
    
    private synchronized String getLocateStmt(final Object element) {
        if (this.elementMapping instanceof ReferenceMapping && this.elementMapping.getNumberOfDatastoreMappings() > 1) {
            return this.getLocateStatementString(element);
        }
        if (this.locateStmt == null) {
            synchronized (this) {
                this.locateStmt = this.getLocateStatementString(element);
            }
        }
        return this.locateStmt;
    }
    
    private String getLocateStatementString(final Object element) {
        final StringBuffer stmt = new StringBuffer("SELECT 1 FROM ");
        stmt.append(this.containerTable.toString());
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
        BackingStoreHelper.appendWhereClauseForElement(stmt, this.elementMapping, element, this.elementsAreSerialised, null, false);
        if (this.relationDiscriminatorMapping != null) {
            BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
        }
        return stmt.toString();
    }
    
    protected void preGetNextIDForOrderColumn(final ManagedConnection mconn) throws MappedDatastoreException {
        final SQLController sqlControl = this.storeMgr.getSQLController();
        try {
            sqlControl.processStatementsForConnection(mconn);
        }
        catch (SQLException e) {
            throw new MappedDatastoreException("SQLException", e);
        }
    }
    
    private synchronized String getMaxOrderColumnIdStmt() {
        if (this.maxOrderColumnIdStmt == null) {
            synchronized (this) {
                final StringBuffer stmt = new StringBuffer("SELECT MAX(" + this.orderMapping.getDatastoreMapping(0).getColumn().getIdentifier().toString() + ")");
                stmt.append(" FROM ");
                stmt.append(this.containerTable.toString());
                stmt.append(" WHERE ");
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
                if (this.relationDiscriminatorMapping != null) {
                    BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
                }
                this.maxOrderColumnIdStmt = stmt.toString();
            }
        }
        return this.maxOrderColumnIdStmt;
    }
    
    protected int getNextIDForOrderColumn(final ObjectProvider op) {
        final ExecutionContext ec = op.getExecutionContext();
        final String stmt = this.getMaxOrderColumnIdStmt();
        int nextID;
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, stmt);
                try {
                    int jdbcPosition = 1;
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    if (this.relationDiscriminatorMapping != null) {
                        BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                    }
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
            throw new NucleusDataStoreException(JoinSetStore.LOCALISER.msg("056020", stmt), e);
        }
        return nextID;
    }
    
    @Override
    public Iterator iterator(final ObjectProvider ownerOP) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        final IteratorStatement iterStmt = this.getIteratorStatement(ec.getClassLoaderResolver(), ec.getFetchPlan(), true);
        final SQLStatement sqlStmt = iterStmt.sqlStmt;
        final StatementClassMapping iteratorMappingClass = iterStmt.stmtClassMapping;
        int inputParamNum = 1;
        final StatementMappingIndex ownerStmtMapIdx = new StatementMappingIndex(this.ownerMapping);
        if (sqlStmt.getNumberOfUnions() > 0) {
            for (int j = 0; j < sqlStmt.getNumberOfUnions() + 1; ++j) {
                final int[] paramPositions = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
                for (int k = 0; k < paramPositions.length; ++k) {
                    paramPositions[k] = inputParamNum++;
                }
                ownerStmtMapIdx.addParameterOccurrence(paramPositions);
            }
        }
        else {
            final int[] paramPositions2 = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
            for (int i = 0; i < paramPositions2.length; ++i) {
                paramPositions2[i] = inputParamNum++;
            }
            ownerStmtMapIdx.addParameterOccurrence(paramPositions2);
        }
        if (ec.getTransaction().getSerializeRead() != null && ec.getTransaction().getSerializeRead()) {
            sqlStmt.addExtension("lock-for-update", true);
        }
        final String stmt = sqlStmt.getSelectStatement().toSQL();
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, stmt);
                for (int numParams = ownerStmtMapIdx.getNumberOfParameterOccurrences(), paramInstance = 0; paramInstance < numParams; ++paramInstance) {
                    ownerStmtMapIdx.getMapping().setObject(ec, ps, ownerStmtMapIdx.getParameterPositionsForOccurrence(paramInstance), ownerOP.getObject());
                }
                try {
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, stmt, ps);
                    try {
                        if (this.elementsAreEmbedded || this.elementsAreSerialised) {
                            return new SetStoreIterator(ownerOP, rs, null, this);
                        }
                        if (this.elementMapping instanceof ReferenceMapping) {
                            return new SetStoreIterator(ownerOP, rs, null, this);
                        }
                        final ResultObjectFactory rof = this.storeMgr.newResultObjectFactory(this.emd, iteratorMappingClass, false, null, this.clr.classForName(this.elementType));
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
            throw new NucleusDataStoreException(JoinSetStore.LOCALISER.msg("056006", stmt), e);
        }
        catch (MappedDatastoreException e2) {
            throw new NucleusDataStoreException(JoinSetStore.LOCALISER.msg("056006", stmt), e2);
        }
    }
    
    public IteratorStatement getIteratorStatement(final ClassLoaderResolver clr, final FetchPlan fp, final boolean addRestrictionOnOwner) {
        SQLStatement sqlStmt = null;
        final SQLExpressionFactory exprFactory = this.storeMgr.getSQLExpressionFactory();
        StatementClassMapping iteratorMappingClass = null;
        if (this.elementsAreEmbedded || this.elementsAreSerialised) {
            sqlStmt = new SQLStatement(this.storeMgr, this.containerTable, null, null);
            sqlStmt.setClassLoaderResolver(clr);
            sqlStmt.select(sqlStmt.getPrimaryTable(), this.elementMapping, null);
        }
        else if (this.elementMapping instanceof ReferenceMapping) {
            sqlStmt = new SQLStatement(this.storeMgr, this.containerTable, null, null);
            sqlStmt.setClassLoaderResolver(clr);
            sqlStmt.select(sqlStmt.getPrimaryTable(), this.elementMapping, null);
        }
        else {
            iteratorMappingClass = new StatementClassMapping();
            for (int i = 0; i < this.elementInfo.length; ++i) {
                final int elementNo = i;
                final Class elementCls = clr.classForName(this.elementInfo[elementNo].getClassName());
                SQLStatement elementStmt = null;
                if (this.elementInfo[elementNo].getDiscriminatorStrategy() != null && this.elementInfo[elementNo].getDiscriminatorStrategy() != DiscriminatorStrategy.NONE) {
                    final String elementType = this.ownerMemberMetaData.getCollection().getElementType();
                    if (ClassUtils.isReferenceType(clr.classForName(elementType))) {
                        final String[] clsNames = this.storeMgr.getNucleusContext().getMetaDataManager().getClassesImplementingInterface(elementType, clr);
                        final Class[] cls = new Class[clsNames.length];
                        for (int j = 0; j < clsNames.length; ++j) {
                            cls[j] = clr.classForName(clsNames[j]);
                        }
                        final StatementGenerator stmtGen = new DiscriminatorStatementGenerator(this.storeMgr, clr, cls, true, (DatastoreIdentifier)null, (String)null, this.containerTable, (DatastoreIdentifier)null, this.elementMapping);
                        if (this.allowNulls) {
                            stmtGen.setOption("allowNulls");
                        }
                        elementStmt = stmtGen.getStatement();
                    }
                    else {
                        final StatementGenerator stmtGen2 = new DiscriminatorStatementGenerator(this.storeMgr, clr, elementCls, true, null, null, this.containerTable, null, this.elementMapping);
                        if (this.allowNulls) {
                            stmtGen2.setOption("allowNulls");
                        }
                        elementStmt = stmtGen2.getStatement();
                    }
                    this.iterateUsingDiscriminator = true;
                }
                else {
                    final StatementGenerator stmtGen3 = new UnionStatementGenerator(this.storeMgr, clr, elementCls, true, null, null, this.containerTable, null, this.elementMapping);
                    stmtGen3.setOption("selectNucleusType");
                    iteratorMappingClass.setNucleusTypeColumnName("NUCLEUS_TYPE");
                    elementStmt = stmtGen3.getStatement();
                }
                if (sqlStmt == null) {
                    sqlStmt = elementStmt;
                }
                else {
                    sqlStmt.union(elementStmt);
                }
            }
            final SQLTable elementSqlTbl = sqlStmt.getTable(this.elementInfo[0].getDatastoreClass(), sqlStmt.getPrimaryTable().getGroupName());
            SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, iteratorMappingClass, fp, elementSqlTbl, this.emd, 0);
        }
        if (addRestrictionOnOwner) {
            final SQLTable ownerSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStmt, sqlStmt.getPrimaryTable(), this.ownerMapping);
            final SQLExpression ownerExpr = exprFactory.newExpression(sqlStmt, ownerSqlTbl, this.ownerMapping);
            final SQLExpression ownerVal = exprFactory.newLiteralParameter(sqlStmt, this.ownerMapping, null, "OWNER");
            sqlStmt.whereAnd(ownerExpr.eq(ownerVal), true);
        }
        if (this.relationDiscriminatorMapping != null) {
            final SQLTable distSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStmt, sqlStmt.getPrimaryTable(), this.relationDiscriminatorMapping);
            final SQLExpression distExpr = exprFactory.newExpression(sqlStmt, distSqlTbl, this.relationDiscriminatorMapping);
            final SQLExpression distVal = exprFactory.newLiteral(sqlStmt, this.relationDiscriminatorMapping, this.relationDiscriminatorValue);
            sqlStmt.whereAnd(distExpr.eq(distVal), true);
        }
        if (this.orderMapping != null) {
            final SQLTable orderSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStmt, sqlStmt.getPrimaryTable(), this.orderMapping);
            final SQLExpression[] orderExprs = new SQLExpression[this.orderMapping.getNumberOfDatastoreMappings()];
            final boolean[] descendingOrder = new boolean[this.orderMapping.getNumberOfDatastoreMappings()];
            orderExprs[0] = exprFactory.newExpression(sqlStmt, orderSqlTbl, this.orderMapping);
            sqlStmt.setOrdering(orderExprs, descendingOrder);
        }
        return new IteratorStatement(this, sqlStmt, iteratorMappingClass);
    }
}
