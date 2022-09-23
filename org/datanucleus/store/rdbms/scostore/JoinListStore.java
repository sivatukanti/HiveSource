// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.metadata.OrderMetaData;
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
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.Transaction;
import org.datanucleus.FetchPlan;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import java.util.ListIterator;
import org.datanucleus.metadata.CollectionMetaData;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import java.util.Iterator;
import org.datanucleus.ExecutionContext;
import java.sql.SQLException;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.rdbms.fieldmanager.DynamicSchemaFieldManager;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.store.FieldValues;
import java.util.Collection;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.CollectionTable;
import org.datanucleus.metadata.AbstractMemberMetaData;

public class JoinListStore extends AbstractListStore
{
    private String setStmt;
    
    public JoinListStore(final AbstractMemberMetaData mmd, final CollectionTable collTable, final ClassLoaderResolver clr) {
        super(collTable.getStoreManager(), clr);
        this.containerTable = collTable;
        this.setOwner(mmd);
        this.ownerMapping = collTable.getOwnerMapping();
        this.elementMapping = collTable.getElementMapping();
        this.orderMapping = collTable.getOrderMapping();
        if (this.ownerMemberMetaData.getOrderMetaData() != null && !this.ownerMemberMetaData.getOrderMetaData().isIndexedList()) {
            this.indexedList = false;
        }
        if (this.orderMapping == null && this.indexedList) {
            throw new NucleusUserException(JoinListStore.LOCALISER.msg("056044", this.ownerMemberMetaData.getFullFieldName(), collTable.toString()));
        }
        this.relationDiscriminatorMapping = collTable.getRelationDiscriminatorMapping();
        this.relationDiscriminatorValue = collTable.getRelationDiscriminatorValue();
        this.elementType = mmd.getCollection().getElementType();
        this.elementsAreEmbedded = collTable.isEmbeddedElement();
        this.elementsAreSerialised = collTable.isSerialisedElement();
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
                if (this.emd != null) {
                    if (!this.elementsAreEmbedded) {
                        this.elementInfo = this.getElementInformationForClass();
                    }
                    else {
                        this.elementInfo = null;
                    }
                }
                else {
                    this.elementInfo = null;
                }
            }
        }
    }
    
    @Override
    protected boolean internalAdd(final ObjectProvider op, int start, final boolean atEnd, final Collection c, final int size) {
        if (c == null || c.size() == 0) {
            return true;
        }
        final int shift = c.size();
        final ExecutionContext ec = op.getExecutionContext();
        final Iterator iter = c.iterator();
        while (iter.hasNext()) {
            final Object element = iter.next();
            this.validateElementForWriting(ec, element, null);
            if (this.relationType == RelationType.ONE_TO_MANY_BI) {
                final ObjectProvider elementSM = ec.findObjectProvider(element);
                if (elementSM == null) {
                    continue;
                }
                final AbstractMemberMetaData[] relatedMmds = this.ownerMemberMetaData.getRelatedMemberMetaData(this.clr);
                final Object elementOwner = elementSM.provideField(relatedMmds[0].getAbsoluteFieldNumber());
                if (elementOwner == null) {
                    NucleusLogger.PERSISTENCE.info(JoinListStore.LOCALISER.msg("056037", op.getObjectAsPrintable(), this.ownerMemberMetaData.getFullFieldName(), StringUtils.toJVMIDString(elementSM.getObject())));
                    elementSM.replaceField(relatedMmds[0].getAbsoluteFieldNumber(), op.getObject());
                }
                else {
                    if (elementOwner != op.getObject() && op.getReferencedPC() == null) {
                        throw new NucleusUserException(JoinListStore.LOCALISER.msg("056038", op.getObjectAsPrintable(), this.ownerMemberMetaData.getFullFieldName(), StringUtils.toJVMIDString(elementSM.getObject()), StringUtils.toJVMIDString(elementOwner)));
                    }
                    continue;
                }
            }
        }
        int currentListSize = 0;
        if (size < 0) {
            currentListSize = this.size(op);
        }
        else {
            currentListSize = size;
        }
        if (this.storeMgr.getBooleanObjectProperty("datanucleus.rdbms.dynamicSchemaUpdates")) {
            final DynamicSchemaFieldManager dynamicSchemaFM = new DynamicSchemaFieldManager(this.storeMgr, op);
            dynamicSchemaFM.storeObjectField(this.getOwnerMemberMetaData().getAbsoluteFieldNumber(), c);
            if (dynamicSchemaFM.hasPerformedSchemaUpdates()) {
                this.invalidateAddStmt();
            }
        }
        final String addStmt = this.getAddStmt();
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                if (!atEnd && start != currentListSize) {
                    final boolean batched = currentListSize - start > 0;
                    for (int i = currentListSize - 1; i >= start; --i) {
                        this.internalShift(op, mconn, batched, i, shift, i == start);
                    }
                }
                else {
                    start = currentListSize;
                }
                int jdbcPosition = 1;
                final boolean batched2 = c.size() > 1;
                for (final Object element2 : c) {
                    final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, addStmt, batched2);
                    try {
                        final JavaTypeMapping orderMapping = this.getOrderMapping();
                        final JavaTypeMapping elementMapping = this.getElementMapping();
                        final JavaTypeMapping relationDiscriminatorMapping = this.getRelationDiscriminatorMapping();
                        jdbcPosition = 1;
                        jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                        jdbcPosition = BackingStoreHelper.populateElementInStatement(ec, ps, element2, jdbcPosition, elementMapping);
                        if (orderMapping != null) {
                            jdbcPosition = BackingStoreHelper.populateOrderInStatement(ec, ps, start, jdbcPosition, orderMapping);
                        }
                        if (relationDiscriminatorMapping != null) {
                            jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                        }
                        ++start;
                        sqlControl.executeStatementUpdate(ec, mconn, addStmt, ps, !iter.hasNext());
                    }
                    finally {
                        sqlControl.closeStatement(mconn, ps);
                    }
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (MappedDatastoreException e) {
            throw new NucleusDataStoreException(JoinListStore.LOCALISER.msg("056009", addStmt), e);
        }
        catch (SQLException e2) {
            throw new NucleusDataStoreException(JoinListStore.LOCALISER.msg("056009", addStmt), e2);
        }
        return true;
    }
    
    @Override
    public Object set(final ObjectProvider op, final int index, final Object element, final boolean allowDependentField) {
        final ExecutionContext ec = op.getExecutionContext();
        this.validateElementForWriting(ec, element, null);
        final Object oldElement = this.get(op, index);
        if (this.storeMgr.getBooleanObjectProperty("datanucleus.rdbms.dynamicSchemaUpdates")) {
            final DynamicSchemaFieldManager dynamicSchemaFM = new DynamicSchemaFieldManager(this.storeMgr, op);
            final Collection coll = new ArrayList();
            coll.add(element);
            dynamicSchemaFM.storeObjectField(this.getOwnerMemberMetaData().getAbsoluteFieldNumber(), coll);
            if (dynamicSchemaFM.hasPerformedSchemaUpdates()) {
                this.setStmt = null;
            }
        }
        final String setStmt = this.getSetStmt();
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, setStmt, false);
                try {
                    int jdbcPosition = 1;
                    jdbcPosition = BackingStoreHelper.populateElementInStatement(ec, ps, element, jdbcPosition, this.elementMapping);
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    if (this.getOwnerMemberMetaData().getOrderMetaData() != null && !this.getOwnerMemberMetaData().getOrderMetaData().isIndexedList()) {
                        NucleusLogger.PERSISTENCE.warn("Calling List.addElement at a position for an ordered list is a stupid thing to do; the ordering is set my the ordering specification. Use an indexed list to do this correctly");
                    }
                    else {
                        jdbcPosition = BackingStoreHelper.populateOrderInStatement(ec, ps, index, jdbcPosition, this.orderMapping);
                    }
                    if (this.relationDiscriminatorMapping != null) {
                        jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                    }
                    sqlControl.executeStatementUpdate(ec, mconn, setStmt, ps, true);
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
            throw new NucleusDataStoreException(JoinListStore.LOCALISER.msg("056015", setStmt), e);
        }
        final CollectionMetaData collmd = this.ownerMemberMetaData.getCollection();
        boolean dependent = collmd.isDependentElement();
        if (this.ownerMemberMetaData.isCascadeRemoveOrphans()) {
            dependent = true;
        }
        if (dependent && !collmd.isEmbeddedElement() && allowDependentField && oldElement != null && !this.contains(op, oldElement)) {
            ec.deleteObjectInternal(oldElement);
        }
        return oldElement;
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
        final Collection existing = new ArrayList();
        final Iterator elemIter = this.iterator(op);
        while (elemIter.hasNext()) {
            final Object elem = elemIter.next();
            if (!coll.contains(elem)) {
                this.remove(op, elem, -1, true);
            }
            else {
                existing.add(elem);
            }
        }
        if (existing.equals(coll)) {
            return;
        }
        this.clear(op);
        this.addAll(op, coll, 0);
    }
    
    @Override
    protected boolean internalRemove(final ObjectProvider ownerOP, final Object element, final int size) {
        boolean modified = false;
        if (this.indexedList) {
            final Collection elements = new ArrayList();
            elements.add(element);
            final int[] indices = this.getIndicesOf(ownerOP, elements);
            for (int i = 0; i < indices.length; ++i) {
                this.internalRemoveAt(ownerOP, indices[i], size);
                modified = true;
            }
        }
        else {
            final ExecutionContext ec = ownerOP.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            try {
                final int[] rcs = this.internalRemove(ownerOP, mconn, false, element, true);
                if (rcs != null && rcs[0] > 0) {
                    modified = true;
                }
            }
            catch (MappedDatastoreException sqe) {
                final String msg = JoinListStore.LOCALISER.msg("056012", sqe.getMessage());
                NucleusLogger.DATASTORE.error(msg, sqe.getCause());
                throw new NucleusDataStoreException(msg, sqe, ownerOP.getObject());
            }
            finally {
                mconn.release();
            }
        }
        return modified;
    }
    
    @Override
    public boolean removeAll(final ObjectProvider op, final Collection elements, final int size) {
        if (elements == null || elements.size() == 0) {
            return false;
        }
        final int currentListSize = this.size(op);
        final int[] indices = this.getIndicesOf(op, elements);
        boolean modified = false;
        final SQLController sqlControl = this.storeMgr.getSQLController();
        final ExecutionContext ec = op.getExecutionContext();
        final String removeAllStmt = this.getRemoveAllStmt(elements);
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
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
            throw new NucleusDataStoreException(JoinListStore.LOCALISER.msg("056012", removeAllStmt), e);
        }
        try {
            final boolean batched = this.storeMgr.allowsBatching();
            final ManagedConnection mconn2 = this.storeMgr.getConnection(ec);
            try {
                for (int i = 0; i < currentListSize; ++i) {
                    int shift = 0;
                    boolean removed = false;
                    for (int j = 0; j < indices.length; ++j) {
                        if (indices[j] == i) {
                            removed = true;
                            break;
                        }
                        if (indices[j] < i) {
                            ++shift;
                        }
                    }
                    if (!removed && shift > 0) {
                        this.internalShift(op, mconn2, batched, i, -1 * shift, i == currentListSize - 1);
                    }
                }
            }
            finally {
                mconn2.release();
            }
        }
        catch (MappedDatastoreException e2) {
            NucleusLogger.DATASTORE.error(e2);
            throw new NucleusDataStoreException(JoinListStore.LOCALISER.msg("056012", removeAllStmt), e2);
        }
        boolean dependent = this.getOwnerMemberMetaData().getCollection().isDependentElement();
        if (this.getOwnerMemberMetaData().isCascadeRemoveOrphans()) {
            dependent = true;
        }
        if (dependent) {
            op.getExecutionContext().deleteObjects(elements.toArray());
        }
        return modified;
    }
    
    @Override
    protected void internalRemoveAt(final ObjectProvider op, final int index, final int size) {
        if (!this.indexedList) {
            throw new NucleusUserException("Cannot remove an element from a particular position with an ordered list since no indexes exist");
        }
        this.internalRemoveAt(op, index, this.getRemoveAtStmt(), size);
    }
    
    @Override
    protected ListIterator listIterator(final ObjectProvider op, final int startIdx, final int endIdx) {
        final ExecutionContext ec = op.getExecutionContext();
        final Transaction tx = ec.getTransaction();
        final IteratorStatement iterStmt = this.getIteratorStatement(op.getExecutionContext().getClassLoaderResolver(), ec.getFetchPlan(), true, startIdx, endIdx);
        final SQLStatement sqlStmt = iterStmt.getSQLStatement();
        final StatementClassMapping resultMapping = iterStmt.getStatementClassMapping();
        int inputParamNum = 1;
        final StatementMappingIndex ownerIdx = new StatementMappingIndex(this.ownerMapping);
        if (sqlStmt.getNumberOfUnions() > 0) {
            for (int j = 0; j < sqlStmt.getNumberOfUnions() + 1; ++j) {
                final int[] paramPositions = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
                for (int k = 0; k < paramPositions.length; ++k) {
                    paramPositions[k] = inputParamNum++;
                }
                ownerIdx.addParameterOccurrence(paramPositions);
            }
        }
        else {
            final int[] paramPositions2 = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
            for (int i = 0; i < paramPositions2.length; ++i) {
                paramPositions2[i] = inputParamNum++;
            }
            ownerIdx.addParameterOccurrence(paramPositions2);
        }
        if (tx.getSerializeRead() != null && tx.getSerializeRead()) {
            sqlStmt.addExtension("lock-for-update", true);
        }
        final String stmt = sqlStmt.getSelectStatement().toSQL();
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, stmt);
                for (int numParams = ownerIdx.getNumberOfParameterOccurrences(), paramInstance = 0; paramInstance < numParams; ++paramInstance) {
                    ownerIdx.getMapping().setObject(ec, ps, ownerIdx.getParameterPositionsForOccurrence(paramInstance), op.getObject());
                }
                try {
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, stmt, ps);
                    try {
                        if (this.elementsAreEmbedded || this.elementsAreSerialised) {
                            return new ListStoreIterator(op, rs, null, this);
                        }
                        if (this.elementMapping instanceof ReferenceMapping) {
                            return new ListStoreIterator(op, rs, null, this);
                        }
                        final ResultObjectFactory rof = this.storeMgr.newResultObjectFactory(this.emd, resultMapping, false, null, this.clr.classForName(this.elementType));
                        return new ListStoreIterator(op, rs, rof, this);
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
            throw new NucleusDataStoreException(JoinListStore.LOCALISER.msg("056006", stmt), e);
        }
        catch (MappedDatastoreException e2) {
            throw new NucleusDataStoreException(JoinListStore.LOCALISER.msg("056006", stmt), e2);
        }
    }
    
    protected String getSetStmt() {
        if (this.setStmt == null) {
            synchronized (this) {
                final StringBuffer stmt = new StringBuffer("UPDATE ");
                stmt.append(this.containerTable.toString());
                stmt.append(" SET ");
                for (int i = 0; i < this.elementMapping.getNumberOfDatastoreMappings(); ++i) {
                    if (i > 0) {
                        stmt.append(",");
                    }
                    stmt.append(this.elementMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                    stmt.append(" = ");
                    stmt.append(((AbstractDatastoreMapping)this.elementMapping.getDatastoreMapping(i)).getUpdateInputParameter());
                }
                stmt.append(" WHERE ");
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
                if (this.getOwnerMemberMetaData().getOrderMetaData() == null || this.getOwnerMemberMetaData().getOrderMetaData().isIndexedList()) {
                    BackingStoreHelper.appendWhereClauseForMapping(stmt, this.orderMapping, null, false);
                }
                if (this.relationDiscriminatorMapping != null) {
                    BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
                }
                this.setStmt = stmt.toString();
            }
        }
        return this.setStmt;
    }
    
    protected String getRemoveAllStmt(final Collection elements) {
        if (elements == null || elements.size() == 0) {
            return null;
        }
        final StringBuffer stmt = new StringBuffer("DELETE FROM ");
        stmt.append(this.containerTable.toString());
        stmt.append(" WHERE ");
        boolean first = true;
        for (final Object element : elements) {
            if (first) {
                stmt.append("(");
            }
            else {
                stmt.append(" OR (");
            }
            BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
            BackingStoreHelper.appendWhereClauseForElement(stmt, this.elementMapping, element, this.isElementsAreSerialised(), null, false);
            if (this.relationDiscriminatorMapping != null) {
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
            }
            stmt.append(")");
            first = false;
        }
        return stmt.toString();
    }
    
    public IteratorStatement getIteratorStatement(final ClassLoaderResolver clr, final FetchPlan fp, final boolean addRestrictionOnOwner, final int startIdx, final int endIdx) {
        SQLStatement sqlStmt = null;
        final StatementClassMapping stmtClassMapping = new StatementClassMapping();
        final SQLExpressionFactory exprFactory = this.storeMgr.getSQLExpressionFactory();
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
                    stmtClassMapping.setNucleusTypeColumnName("NUCLEUS_TYPE");
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
            SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, stmtClassMapping, fp, elementSqlTbl, this.emd, 0);
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
        if (this.indexedList) {
            boolean needsOrdering = true;
            if (startIdx == -1 && endIdx == -1) {
                final SQLExpression indexExpr = exprFactory.newExpression(sqlStmt, sqlStmt.getPrimaryTable(), this.orderMapping);
                final SQLExpression indexVal = exprFactory.newLiteral(sqlStmt, this.orderMapping, 0);
                sqlStmt.whereAnd(indexExpr.ge(indexVal), true);
            }
            else if (startIdx >= 0 && endIdx == startIdx) {
                needsOrdering = false;
                final SQLExpression indexExpr = exprFactory.newExpression(sqlStmt, sqlStmt.getPrimaryTable(), this.orderMapping);
                final SQLExpression indexVal = exprFactory.newLiteral(sqlStmt, this.orderMapping, startIdx);
                sqlStmt.whereAnd(indexExpr.eq(indexVal), true);
            }
            else {
                if (startIdx >= 0) {
                    final SQLExpression indexExpr = exprFactory.newExpression(sqlStmt, sqlStmt.getPrimaryTable(), this.orderMapping);
                    final SQLExpression indexVal = exprFactory.newLiteral(sqlStmt, this.orderMapping, startIdx);
                    sqlStmt.whereAnd(indexExpr.ge(indexVal), true);
                }
                else {
                    final SQLExpression indexExpr = exprFactory.newExpression(sqlStmt, sqlStmt.getPrimaryTable(), this.orderMapping);
                    final SQLExpression indexVal = exprFactory.newLiteral(sqlStmt, this.orderMapping, 0);
                    sqlStmt.whereAnd(indexExpr.ge(indexVal), true);
                }
                if (endIdx >= 0) {
                    final SQLExpression indexExpr2 = exprFactory.newExpression(sqlStmt, sqlStmt.getPrimaryTable(), this.orderMapping);
                    final SQLExpression indexVal2 = exprFactory.newLiteral(sqlStmt, this.orderMapping, endIdx);
                    sqlStmt.whereAnd(indexExpr2.lt(indexVal2), true);
                }
            }
            if (needsOrdering) {
                final SQLTable orderSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStmt, sqlStmt.getPrimaryTable(), this.orderMapping);
                final SQLExpression[] orderExprs = new SQLExpression[this.orderMapping.getNumberOfDatastoreMappings()];
                final boolean[] descendingOrder = new boolean[this.orderMapping.getNumberOfDatastoreMappings()];
                orderExprs[0] = exprFactory.newExpression(sqlStmt, orderSqlTbl, this.orderMapping);
                sqlStmt.setOrdering(orderExprs, descendingOrder);
            }
        }
        else if (this.elementInfo != null && this.elementInfo.length > 0) {
            final DatastoreClass elementTbl = this.elementInfo[0].getDatastoreClass();
            final OrderMetaData.FieldOrder[] orderComponents = this.ownerMemberMetaData.getOrderMetaData().getFieldOrders();
            final SQLExpression[] orderExprs = new SQLExpression[orderComponents.length];
            final boolean[] orderDirs = new boolean[orderComponents.length];
            for (int k = 0; k < orderComponents.length; ++k) {
                final String fieldName = orderComponents[k].getFieldName();
                final JavaTypeMapping fieldMapping = elementTbl.getMemberMapping(this.elementInfo[0].getAbstractClassMetaData().getMetaDataForMember(fieldName));
                orderDirs[k] = !orderComponents[k].isForward();
                final SQLTable fieldSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStmt, sqlStmt.getPrimaryTable(), fieldMapping);
                orderExprs[k] = exprFactory.newExpression(sqlStmt, fieldSqlTbl, fieldMapping);
            }
            sqlStmt.setOrdering(orderExprs, orderDirs);
        }
        return new IteratorStatement(this, sqlStmt, stmtClassMapping);
    }
}
