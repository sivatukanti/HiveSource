// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.metadata.OrderMetaData;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.store.scostore.Store;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.UnionStatementGenerator;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.DiscriminatorStatementGenerator;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.Transaction;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import java.util.ListIterator;
import org.datanucleus.FetchPlan;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.util.StringUtils;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.store.FieldValues;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.mapping.MappingHelper;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.ExecutionContext;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.metadata.CollectionMetaData;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractMemberMetaData;

public class FKListStore extends AbstractListStore
{
    private final int ownerFieldNumber;
    private String updateFkStmt;
    private String clearNullifyStmt;
    private String removeAtNullifyStmt;
    private String setStmt;
    private String unsetStmt;
    
    public FKListStore(final AbstractMemberMetaData mmd, final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        super(storeMgr, clr);
        this.setOwner(mmd);
        final CollectionMetaData colmd = mmd.getCollection();
        if (colmd == null) {
            throw new NucleusUserException(FKListStore.LOCALISER.msg("056001", mmd.getFullFieldName()));
        }
        this.elementType = colmd.getElementType();
        final Class element_class = clr.classForName(this.elementType);
        if (ClassUtils.isReferenceType(element_class)) {
            this.emd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForImplementationOfReference(element_class, null, clr);
            if (this.emd != null) {
                this.elementType = this.emd.getFullClassName();
            }
        }
        else {
            this.emd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(element_class, clr);
        }
        if (this.emd == null) {
            throw new NucleusUserException(FKListStore.LOCALISER.msg("056003", element_class.getName(), mmd.getFullFieldName()));
        }
        this.elementInfo = this.getElementInformationForClass();
        if (this.elementInfo != null && this.elementInfo.length > 1) {
            throw new NucleusUserException(FKListStore.LOCALISER.msg("056031", this.ownerMemberMetaData.getFullFieldName()));
        }
        if (this.elementInfo == null || this.elementInfo.length == 0) {
            throw new NucleusUserException(FKListStore.LOCALISER.msg("056075", this.ownerMemberMetaData.getFullFieldName(), this.elementType));
        }
        this.elementMapping = this.elementInfo[0].getDatastoreClass().getIdMapping();
        this.elementsAreEmbedded = false;
        this.elementsAreSerialised = false;
        final String mappedByFieldName = mmd.getMappedBy();
        if (mappedByFieldName != null) {
            final AbstractMemberMetaData eofmd = this.emd.getMetaDataForMember(mappedByFieldName);
            if (eofmd == null) {
                throw new NucleusUserException(FKListStore.LOCALISER.msg("056024", mmd.getFullFieldName(), mappedByFieldName, element_class.getName()));
            }
            if (!clr.isAssignableFrom(eofmd.getType(), mmd.getAbstractClassMetaData().getFullClassName())) {
                throw new NucleusUserException(FKListStore.LOCALISER.msg("056025", mmd.getFullFieldName(), eofmd.getFullFieldName(), eofmd.getTypeName(), mmd.getAbstractClassMetaData().getFullClassName()));
            }
            final String ownerFieldName = eofmd.getName();
            this.ownerFieldNumber = this.emd.getAbsolutePositionOfMember(ownerFieldName);
            this.ownerMapping = this.elementInfo[0].getDatastoreClass().getMemberMapping(eofmd);
            if (this.ownerMapping == null) {
                throw new NucleusUserException(FKListStore.LOCALISER.msg("056029", mmd.getAbstractClassMetaData().getFullClassName(), mmd.getName(), this.elementType, ownerFieldName));
            }
            if (this.isEmbeddedMapping(this.ownerMapping)) {
                throw new NucleusUserException(FKListStore.LOCALISER.msg("056026", ownerFieldName, this.elementType, eofmd.getTypeName(), mmd.getClassName()));
            }
        }
        else {
            this.ownerFieldNumber = -1;
            this.ownerMapping = this.elementInfo[0].getDatastoreClass().getExternalMapping(mmd, 5);
            if (this.ownerMapping == null) {
                throw new NucleusUserException(FKListStore.LOCALISER.msg("056030", mmd.getAbstractClassMetaData().getFullClassName(), mmd.getName(), this.elementType));
            }
        }
        this.orderMapping = this.elementInfo[0].getDatastoreClass().getExternalMapping(mmd, 4);
        if (mmd.getOrderMetaData() != null && !mmd.getOrderMetaData().isIndexedList()) {
            this.indexedList = false;
        }
        if (this.orderMapping == null && this.indexedList) {
            throw new NucleusUserException(FKListStore.LOCALISER.msg("056041", mmd.getAbstractClassMetaData().getFullClassName(), mmd.getName(), this.elementType));
        }
        this.relationDiscriminatorMapping = this.elementInfo[0].getDatastoreClass().getExternalMapping(mmd, 6);
        if (this.relationDiscriminatorMapping != null) {
            this.relationDiscriminatorValue = mmd.getValueForExtension("relation-discriminator-value");
            if (this.relationDiscriminatorValue == null) {
                this.relationDiscriminatorValue = mmd.getFullFieldName();
            }
        }
        this.containerTable = this.elementInfo[0].getDatastoreClass();
        if (mmd.getMappedBy() != null && this.ownerMapping.getTable() != this.containerTable) {
            this.containerTable = this.ownerMapping.getTable();
        }
    }
    
    @Override
    public Object set(final ObjectProvider op, final int index, final Object element, final boolean allowDependentField) {
        this.validateElementForWriting(op, element, -1);
        final Object oldElement = this.get(op, index);
        try {
            final ExecutionContext ec = op.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final String unsetStmt = this.getUnsetStmt();
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, unsetStmt, false);
                try {
                    int jdbcPosition = 1;
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    if (this.orderMapping != null) {
                        jdbcPosition = BackingStoreHelper.populateOrderInStatement(ec, ps, index, jdbcPosition, this.orderMapping);
                    }
                    if (this.relationDiscriminatorMapping != null) {
                        jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                    }
                    sqlControl.executeStatementUpdate(ec, mconn, unsetStmt, ps, true);
                }
                finally {
                    sqlControl.closeStatement(mconn, ps);
                }
                final String setStmt = this.getSetStmt(element);
                final PreparedStatement ps2 = sqlControl.getStatementForUpdate(mconn, setStmt, false);
                try {
                    int jdbcPosition2 = 1;
                    jdbcPosition2 = BackingStoreHelper.populateOwnerInStatement(op, ec, ps2, jdbcPosition2, this);
                    if (this.orderMapping != null) {
                        jdbcPosition2 = BackingStoreHelper.populateOrderInStatement(ec, ps2, index, jdbcPosition2, this.orderMapping);
                    }
                    if (this.relationDiscriminatorMapping != null) {
                        jdbcPosition2 = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps2, jdbcPosition2, this);
                    }
                    jdbcPosition2 = BackingStoreHelper.populateElementForWhereClauseInStatement(ec, ps2, element, jdbcPosition2, this.elementMapping);
                    sqlControl.executeStatementUpdate(ec, mconn, setStmt, ps2, true);
                }
                finally {
                    ps2.close();
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(FKListStore.LOCALISER.msg("056015", this.setStmt), e);
        }
        boolean dependent = this.getOwnerMemberMetaData().getCollection().isDependentElement();
        if (this.getOwnerMemberMetaData().isCascadeRemoveOrphans()) {
            dependent = true;
        }
        if (dependent && allowDependentField && oldElement != null) {
            op.getExecutionContext().deleteObjectInternal(oldElement);
        }
        return oldElement;
    }
    
    private boolean updateElementFk(final ObjectProvider op, final Object element, final Object owner, final int index) {
        if (element == null) {
            return false;
        }
        final ExecutionContext ec = op.getExecutionContext();
        final JavaTypeMapping ownerMapping = this.getOwnerMapping();
        final JavaTypeMapping orderMapping = this.getOrderMapping();
        final JavaTypeMapping elementMapping = this.getElementMapping();
        final ElementInfo[] elementInfo = this.getElementInfo();
        final JavaTypeMapping relationDiscriminatorMapping = this.getRelationDiscriminatorMapping();
        final AbstractMemberMetaData ownerMemberMetaData = this.getOwnerMemberMetaData();
        final String updateFkStmt = this.getUpdateFkStmt(element);
        boolean retval;
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, updateFkStmt, false);
                try {
                    int jdbcPosition = 1;
                    if (elementInfo.length > 1) {
                        this.storeMgr.getDatastoreClass(element.getClass().getName(), this.clr);
                    }
                    if (owner == null) {
                        if (ownerMemberMetaData != null) {
                            ownerMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, ownerMapping), null, op, ownerMemberMetaData.getAbsoluteFieldNumber());
                        }
                        else {
                            ownerMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, ownerMapping), null);
                        }
                        jdbcPosition += ownerMapping.getNumberOfDatastoreMappings();
                    }
                    else {
                        jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    }
                    if (orderMapping != null) {
                        jdbcPosition = BackingStoreHelper.populateOrderInStatement(ec, ps, index, jdbcPosition, orderMapping);
                    }
                    if (relationDiscriminatorMapping != null) {
                        jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                    }
                    jdbcPosition = BackingStoreHelper.populateElementForWhereClauseInStatement(ec, ps, element, jdbcPosition, elementMapping);
                    sqlControl.executeStatementUpdate(ec, mconn, updateFkStmt, ps, true);
                    retval = true;
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
            throw new NucleusDataStoreException(FKListStore.LOCALISER.msg("056027", updateFkStmt), e);
        }
        return retval;
    }
    
    @Override
    public void update(final ObjectProvider op, final Collection coll) {
        if (coll == null || coll.isEmpty()) {
            this.clear(op);
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
    protected boolean internalAdd(final ObjectProvider op, int startAt, final boolean atEnd, final Collection c, final int size) {
        if (c == null || c.size() == 0) {
            return true;
        }
        int currentListSize = 0;
        if (size < 0) {
            currentListSize = this.size(op);
        }
        else {
            currentListSize = size;
        }
        boolean shiftingElements = true;
        if (atEnd || startAt == currentListSize) {
            shiftingElements = false;
            startAt = currentListSize;
        }
        boolean elementsNeedPositioning = false;
        int position = startAt;
        Iterator elementIter = c.iterator();
        while (elementIter.hasNext()) {
            if (shiftingElements) {
                position = -1;
            }
            final boolean inserted = this.validateElementForWriting(op, elementIter.next(), position);
            if (!inserted || shiftingElements) {
                elementsNeedPositioning = true;
            }
            if (!shiftingElements) {
                ++position;
            }
        }
        if (shiftingElements) {
            try {
                final int shift = c.size();
                final ExecutionContext ec = op.getExecutionContext();
                final ManagedConnection mconn = this.storeMgr.getConnection(ec);
                try {
                    for (int i = currentListSize - 1; i >= startAt; --i) {
                        this.internalShift(op, mconn, true, i, shift, false);
                    }
                }
                finally {
                    mconn.release();
                }
            }
            catch (MappedDatastoreException e) {
                throw new NucleusDataStoreException(FKListStore.LOCALISER.msg("056009", e.getMessage()), e.getCause());
            }
        }
        if (shiftingElements || elementsNeedPositioning) {
            elementIter = c.iterator();
            while (elementIter.hasNext()) {
                final Object element = elementIter.next();
                this.updateElementFk(op, element, op.getObject(), startAt);
                ++startAt;
            }
        }
        return true;
    }
    
    @Override
    protected boolean internalRemove(final ObjectProvider op, final Object element, final int size) {
        if (this.indexedList) {
            final int index = this.indexOf(op, element);
            if (index == -1) {
                return false;
            }
            this.internalRemoveAt(op, index, size);
        }
        else if (this.ownerMapping.isNullable()) {
            final ExecutionContext ec = op.getExecutionContext();
            final ObjectProvider elementSM = ec.findObjectProvider(element);
            if (this.relationType == RelationType.ONE_TO_MANY_BI) {
                elementSM.replaceFieldMakeDirty(this.ownerMemberMetaData.getRelatedMemberMetaData(this.clr)[0].getAbsoluteFieldNumber(), null);
                if (op.getExecutionContext().isFlushing()) {
                    elementSM.flush();
                }
            }
            else {
                this.updateElementFk(op, element, null, -1);
            }
        }
        else {
            op.getExecutionContext().deleteObjectInternal(element);
        }
        return true;
    }
    
    protected void manageRemovalOfElement(final ObjectProvider ownerOP, final Object element) {
    }
    
    @Override
    protected void internalRemoveAt(final ObjectProvider op, final int index, final int size) {
        if (!this.indexedList) {
            throw new NucleusUserException("Cannot remove an element from a particular position with an ordered list since no indexes exist");
        }
        boolean nullify = false;
        if (this.ownerMapping.isNullable() && this.orderMapping != null && this.orderMapping.isNullable()) {
            NucleusLogger.DATASTORE.debug(FKListStore.LOCALISER.msg("056043"));
            nullify = true;
        }
        else {
            NucleusLogger.DATASTORE.debug(FKListStore.LOCALISER.msg("056042"));
        }
        String stmt;
        if (nullify) {
            stmt = this.getRemoveAtNullifyStmt();
        }
        else {
            stmt = this.getRemoveAtStmt();
        }
        this.internalRemoveAt(op, index, stmt, size);
    }
    
    @Override
    public void clear(final ObjectProvider op) {
        boolean deleteElements = false;
        final ExecutionContext ec = op.getExecutionContext();
        boolean dependent = this.ownerMemberMetaData.getCollection().isDependentElement();
        if (this.ownerMemberMetaData.isCascadeRemoveOrphans()) {
            dependent = true;
        }
        if (dependent) {
            NucleusLogger.DATASTORE.debug(FKListStore.LOCALISER.msg("056034"));
            deleteElements = true;
        }
        else if (this.ownerMapping.isNullable() && this.orderMapping == null) {
            NucleusLogger.DATASTORE.debug(FKListStore.LOCALISER.msg("056036"));
            deleteElements = false;
        }
        else if (this.ownerMapping.isNullable() && this.orderMapping != null && this.orderMapping.isNullable()) {
            NucleusLogger.DATASTORE.debug(FKListStore.LOCALISER.msg("056036"));
            deleteElements = false;
        }
        else {
            NucleusLogger.DATASTORE.debug(FKListStore.LOCALISER.msg("056035"));
            deleteElements = true;
        }
        if (deleteElements) {
            final Iterator elementsIter = this.iterator(op);
            if (elementsIter != null) {
                while (elementsIter.hasNext()) {
                    final Object element = elementsIter.next();
                    if (ec.getApiAdapter().isPersistable(element) && ec.getApiAdapter().isDeleted(element)) {
                        final ObjectProvider objSM = ec.findObjectProvider(element);
                        objSM.flush();
                    }
                    else {
                        ec.deleteObjectInternal(element);
                    }
                }
            }
        }
        else {
            final String clearNullifyStmt = this.getClearNullifyStmt();
            try {
                final ManagedConnection mconn = this.storeMgr.getConnection(ec);
                final SQLController sqlControl = this.storeMgr.getSQLController();
                try {
                    final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, clearNullifyStmt, false);
                    try {
                        int jdbcPosition = 1;
                        jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                        if (this.getRelationDiscriminatorMapping() != null) {
                            BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                        }
                        sqlControl.executeStatementUpdate(ec, mconn, clearNullifyStmt, ps, true);
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
                throw new NucleusDataStoreException(FKListStore.LOCALISER.msg("056013", clearNullifyStmt), e);
            }
        }
    }
    
    protected boolean validateElementForWriting(final ObjectProvider op, final Object element, final int index) {
        final Object newOwner = op.getObject();
        final boolean inserted = super.validateElementForWriting(op.getExecutionContext(), element, new FieldValues() {
            @Override
            public void fetchFields(final ObjectProvider esm) {
                final boolean isPersistentInterface = FKListStore.this.storeMgr.getNucleusContext().getMetaDataManager().isPersistentInterface(FKListStore.this.elementType);
                DatastoreClass elementTable = null;
                if (isPersistentInterface) {
                    elementTable = FKListStore.this.storeMgr.getDatastoreClass(FKListStore.this.storeMgr.getNucleusContext().getMetaDataManager().getImplementationNameForPersistentInterface(FKListStore.this.elementType), FKListStore.this.clr);
                }
                else {
                    elementTable = FKListStore.this.storeMgr.getDatastoreClass(FKListStore.this.elementType, FKListStore.this.clr);
                }
                if (elementTable == null) {
                    final AbstractClassMetaData[] managingCmds = FKListStore.this.storeMgr.getClassesManagingTableForClass(FKListStore.this.emd, FKListStore.this.clr);
                    if (managingCmds != null && managingCmds.length > 0) {
                        for (int i = 0; i < managingCmds.length; ++i) {
                            final Class tblCls = FKListStore.this.clr.classForName(managingCmds[i].getFullClassName());
                            if (tblCls.isAssignableFrom(esm.getObject().getClass())) {
                                elementTable = FKListStore.this.storeMgr.getDatastoreClass(managingCmds[i].getFullClassName(), FKListStore.this.clr);
                                break;
                            }
                        }
                    }
                }
                if (elementTable != null) {
                    final JavaTypeMapping externalFKMapping = elementTable.getExternalMapping(FKListStore.this.ownerMemberMetaData, 5);
                    if (externalFKMapping != null) {
                        esm.setAssociatedValue(externalFKMapping, op.getObject());
                    }
                    if (FKListStore.this.relationDiscriminatorMapping != null) {
                        esm.setAssociatedValue(FKListStore.this.relationDiscriminatorMapping, FKListStore.this.relationDiscriminatorValue);
                    }
                    if (FKListStore.this.orderMapping != null && index >= 0) {
                        if (FKListStore.this.ownerMemberMetaData.getOrderMetaData() != null && FKListStore.this.ownerMemberMetaData.getOrderMetaData().getMappedBy() != null) {
                            Object indexValue = null;
                            if (FKListStore.this.orderMapping.getMemberMetaData().getTypeName().equals(ClassNameConstants.JAVA_LANG_LONG) || FKListStore.this.orderMapping.getMemberMetaData().getTypeName().equals(ClassNameConstants.LONG)) {
                                indexValue = index;
                            }
                            else {
                                indexValue = index;
                            }
                            esm.replaceFieldMakeDirty(FKListStore.this.orderMapping.getMemberMetaData().getAbsoluteFieldNumber(), indexValue);
                        }
                        else {
                            esm.setAssociatedValue(FKListStore.this.orderMapping, index);
                        }
                    }
                }
                if (FKListStore.this.ownerFieldNumber >= 0) {
                    final Object currentOwner = esm.provideField(FKListStore.this.ownerFieldNumber);
                    if (currentOwner == null) {
                        NucleusLogger.PERSISTENCE.info(BaseContainerStore.LOCALISER.msg("056037", op.getObjectAsPrintable(), FKListStore.this.ownerMemberMetaData.getFullFieldName(), StringUtils.toJVMIDString(esm.getObject())));
                        esm.replaceFieldMakeDirty(FKListStore.this.ownerFieldNumber, newOwner);
                    }
                    else if (currentOwner != newOwner && op.getReferencedPC() == null) {
                        throw new NucleusUserException(BaseContainerStore.LOCALISER.msg("056038", op.getObjectAsPrintable(), FKListStore.this.ownerMemberMetaData.getFullFieldName(), StringUtils.toJVMIDString(esm.getObject()), StringUtils.toJVMIDString(currentOwner)));
                    }
                }
            }
            
            @Override
            public void fetchNonLoadedFields(final ObjectProvider op) {
            }
            
            @Override
            public FetchPlan getFetchPlanForLoading() {
                return null;
            }
        });
        return inserted;
    }
    
    @Override
    protected ListIterator listIterator(final ObjectProvider op, final int startIdx, final int endIdx) {
        final ExecutionContext ec = op.getExecutionContext();
        final Transaction tx = ec.getTransaction();
        if (this.elementInfo == null || this.elementInfo.length == 0) {
            return null;
        }
        final IteratorStatement iterStmt = this.getIteratorStatement(op.getExecutionContext().getClassLoaderResolver(), ec.getFetchPlan(), true, startIdx, endIdx);
        final SQLStatement sqlStmt = iterStmt.getSQLStatement();
        final StatementClassMapping resultMapping = iterStmt.getStatementClassMapping();
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
                        ResultObjectFactory rof = null;
                        if (this.elementsAreEmbedded || this.elementsAreSerialised) {
                            throw new NucleusException("Cannot have FK set with non-persistent objects");
                        }
                        rof = this.storeMgr.newResultObjectFactory(this.emd, resultMapping, false, null, this.clr.classForName(this.elementType));
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
            throw new NucleusDataStoreException(FKListStore.LOCALISER.msg("056006", stmt), e);
        }
        catch (MappedDatastoreException e2) {
            throw new NucleusDataStoreException(FKListStore.LOCALISER.msg("056006", stmt), e2);
        }
    }
    
    private String getUpdateFkStmt(final Object element) {
        if (this.elementMapping instanceof ReferenceMapping && this.elementMapping.getNumberOfDatastoreMappings() > 1) {
            return this.getUpdateFkStatementString(element);
        }
        if (this.updateFkStmt == null) {
            synchronized (this) {
                this.updateFkStmt = this.getUpdateFkStatementString(element);
            }
        }
        return this.updateFkStmt;
    }
    
    private String getUpdateFkStatementString(final Object element) {
        final StringBuffer stmt = new StringBuffer("UPDATE ");
        if (this.elementInfo.length > 1) {
            stmt.append("?");
        }
        else {
            stmt.append(this.containerTable.toString());
        }
        stmt.append(" SET ");
        for (int i = 0; i < this.ownerMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(this.ownerMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
            stmt.append("=");
            stmt.append(((AbstractDatastoreMapping)this.ownerMapping.getDatastoreMapping(i)).getUpdateInputParameter());
        }
        if (this.orderMapping != null) {
            for (int i = 0; i < this.orderMapping.getNumberOfDatastoreMappings(); ++i) {
                stmt.append(",");
                stmt.append(this.orderMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                stmt.append("=");
                stmt.append(((AbstractDatastoreMapping)this.orderMapping.getDatastoreMapping(i)).getUpdateInputParameter());
            }
        }
        if (this.relationDiscriminatorMapping != null) {
            for (int i = 0; i < this.relationDiscriminatorMapping.getNumberOfDatastoreMappings(); ++i) {
                stmt.append(",");
                stmt.append(this.relationDiscriminatorMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                stmt.append("=");
                stmt.append(((AbstractDatastoreMapping)this.relationDiscriminatorMapping.getDatastoreMapping(i)).getUpdateInputParameter());
            }
        }
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForElement(stmt, this.elementMapping, element, this.elementsAreSerialised, null, true);
        return stmt.toString();
    }
    
    private String getClearNullifyStmt() {
        if (this.clearNullifyStmt == null) {
            synchronized (this) {
                final StringBuffer stmt = new StringBuffer("UPDATE ");
                if (this.elementInfo.length > 1) {
                    stmt.append("?");
                }
                else {
                    stmt.append(this.containerTable.toString());
                }
                stmt.append(" SET ");
                for (int i = 0; i < this.ownerMapping.getNumberOfDatastoreMappings(); ++i) {
                    if (i > 0) {
                        stmt.append(", ");
                    }
                    stmt.append(this.ownerMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString() + "=NULL");
                }
                if (this.orderMapping != null) {
                    for (int i = 0; i < this.orderMapping.getNumberOfDatastoreMappings(); ++i) {
                        stmt.append(", ");
                        stmt.append(this.orderMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString() + "=-1");
                    }
                }
                if (this.relationDiscriminatorMapping != null) {
                    for (int i = 0; i < this.relationDiscriminatorMapping.getNumberOfDatastoreMappings(); ++i) {
                        stmt.append(", ");
                        stmt.append(this.relationDiscriminatorMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                        stmt.append("=NULL");
                    }
                }
                stmt.append(" WHERE ");
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
                if (this.relationDiscriminatorMapping != null) {
                    BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
                }
                this.clearNullifyStmt = stmt.toString();
            }
        }
        return this.clearNullifyStmt;
    }
    
    private String getSetStmt(final Object element) {
        if (this.elementMapping instanceof ReferenceMapping && this.elementMapping.getNumberOfDatastoreMappings() > 1) {
            return this.getSetStatementString(element);
        }
        if (this.setStmt == null) {
            synchronized (this) {
                this.setStmt = this.getSetStatementString(element);
            }
        }
        return this.setStmt;
    }
    
    private String getSetStatementString(final Object element) {
        final StringBuffer stmt = new StringBuffer("UPDATE ");
        stmt.append(this.containerTable.toString());
        stmt.append(" SET ");
        for (int i = 0; i < this.ownerMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(this.ownerMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
            stmt.append(" = ");
            stmt.append(((AbstractDatastoreMapping)this.ownerMapping.getDatastoreMapping(i)).getUpdateInputParameter());
        }
        if (this.orderMapping != null) {
            for (int i = 0; i < this.orderMapping.getNumberOfDatastoreMappings(); ++i) {
                stmt.append(",");
                stmt.append(this.orderMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                stmt.append(" = ");
                stmt.append(((AbstractDatastoreMapping)this.orderMapping.getDatastoreMapping(i)).getUpdateInputParameter());
            }
        }
        if (this.relationDiscriminatorMapping != null) {
            for (int i = 0; i < this.relationDiscriminatorMapping.getNumberOfDatastoreMappings(); ++i) {
                stmt.append(",");
                stmt.append(this.relationDiscriminatorMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                stmt.append(" = ");
                stmt.append(((AbstractDatastoreMapping)this.relationDiscriminatorMapping.getDatastoreMapping(i)).getUpdateInputParameter());
            }
        }
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForElement(stmt, this.elementMapping, element, this.isElementsAreSerialised(), null, true);
        return stmt.toString();
    }
    
    private String getUnsetStmt() {
        if (this.unsetStmt == null) {
            synchronized (this) {
                final StringBuffer stmt = new StringBuffer("UPDATE ");
                stmt.append(this.containerTable.toString());
                stmt.append(" SET ");
                for (int i = 0; i < this.ownerMapping.getNumberOfDatastoreMappings(); ++i) {
                    if (i > 0) {
                        stmt.append(",");
                    }
                    stmt.append(this.ownerMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                    stmt.append("=NULL");
                }
                if (this.orderMapping != null) {
                    for (int i = 0; i < this.orderMapping.getNumberOfDatastoreMappings(); ++i) {
                        stmt.append(",");
                        stmt.append(this.orderMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                        stmt.append("=-1");
                    }
                }
                if (this.relationDiscriminatorMapping != null) {
                    for (int i = 0; i < this.relationDiscriminatorMapping.getNumberOfDatastoreMappings(); ++i) {
                        stmt.append(",");
                        stmt.append(this.relationDiscriminatorMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                        stmt.append(" = NULL");
                    }
                }
                stmt.append(" WHERE ");
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.orderMapping, null, false);
                if (this.relationDiscriminatorMapping != null) {
                    BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
                }
                this.unsetStmt = stmt.toString();
            }
        }
        return this.unsetStmt;
    }
    
    private String getRemoveAtNullifyStmt() {
        if (this.removeAtNullifyStmt == null) {
            synchronized (this) {
                final StringBuffer stmt = new StringBuffer("UPDATE ");
                if (this.elementInfo.length > 1) {
                    stmt.append("?");
                }
                else {
                    stmt.append(this.containerTable.toString());
                }
                stmt.append(" SET ");
                for (int i = 0; i < this.ownerMapping.getNumberOfDatastoreMappings(); ++i) {
                    if (i > 0) {
                        stmt.append(", ");
                    }
                    stmt.append(this.ownerMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                    stmt.append("=NULL");
                }
                if (this.orderMapping != null) {
                    for (int i = 0; i < this.orderMapping.getNumberOfDatastoreMappings(); ++i) {
                        stmt.append(", ");
                        stmt.append(this.orderMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                        stmt.append("=-1");
                    }
                }
                stmt.append(" WHERE ");
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.orderMapping, null, false);
                if (this.relationDiscriminatorMapping != null) {
                    BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
                }
                this.removeAtNullifyStmt = stmt.toString();
            }
        }
        return this.removeAtNullifyStmt;
    }
    
    public IteratorStatement getIteratorStatement(final ClassLoaderResolver clr, final FetchPlan fp, final boolean addRestrictionOnOwner, final int startIdx, final int endIdx) {
        SQLStatement sqlStmt = null;
        final StatementClassMapping stmtClassMapping = new StatementClassMapping();
        final SQLExpressionFactory exprFactory = this.storeMgr.getSQLExpressionFactory();
        if (this.elementInfo.length == 1 && this.elementInfo[0].getDatastoreClass().getDiscriminatorMetaData() != null && this.elementInfo[0].getDatastoreClass().getDiscriminatorMetaData().getStrategy() != DiscriminatorStrategy.NONE) {
            final String elementType = this.ownerMemberMetaData.getCollection().getElementType();
            if (ClassUtils.isReferenceType(clr.classForName(elementType))) {
                final String[] clsNames = this.storeMgr.getNucleusContext().getMetaDataManager().getClassesImplementingInterface(elementType, clr);
                final Class[] cls = new Class[clsNames.length];
                for (int i = 0; i < clsNames.length; ++i) {
                    cls[i] = clr.classForName(clsNames[i]);
                }
                sqlStmt = new DiscriminatorStatementGenerator(this.storeMgr, clr, cls, true, (DatastoreIdentifier)null, (String)null).getStatement();
            }
            else {
                sqlStmt = new DiscriminatorStatementGenerator(this.storeMgr, clr, clr.classForName(this.elementInfo[0].getClassName()), true, null, null).getStatement();
            }
            this.iterateUsingDiscriminator = true;
            SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, stmtClassMapping, fp, sqlStmt.getPrimaryTable(), this.emd, 0);
        }
        else {
            for (int j = 0; j < this.elementInfo.length; ++j) {
                final Class elementCls = clr.classForName(this.elementInfo[j].getClassName());
                final UnionStatementGenerator stmtGen = new UnionStatementGenerator(this.storeMgr, clr, elementCls, true, null, null);
                stmtGen.setOption("selectNucleusType");
                stmtClassMapping.setNucleusTypeColumnName("NUCLEUS_TYPE");
                final SQLStatement subStmt = stmtGen.getStatement();
                if (sqlStmt == null) {
                    SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(subStmt, stmtClassMapping, fp, subStmt.getPrimaryTable(), this.emd, 0);
                }
                else {
                    SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(subStmt, null, fp, subStmt.getPrimaryTable(), this.emd, 0);
                }
                if (sqlStmt == null) {
                    sqlStmt = subStmt;
                }
                else {
                    sqlStmt.union(subStmt);
                }
            }
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
        else {
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
