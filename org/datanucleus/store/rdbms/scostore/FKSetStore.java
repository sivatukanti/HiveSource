// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.store.scostore.Store;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.UnionStatementGenerator;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.DiscriminatorStatementGenerator;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.FetchPlan;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.util.StringUtils;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collection;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.ExecutionContext;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.rdbms.mapping.MappingHelper;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.FieldValues;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.metadata.CollectionMetaData;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractMemberMetaData;

public class FKSetStore extends AbstractSetStore
{
    private final int ownerFieldNumber;
    private String updateFkStmt;
    private String clearNullifyStmt;
    
    public FKSetStore(final AbstractMemberMetaData mmd, final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        super(storeMgr, clr);
        this.setOwner(mmd);
        final CollectionMetaData colmd = mmd.getCollection();
        if (colmd == null) {
            throw new NucleusUserException(FKSetStore.LOCALISER.msg("056001", mmd.getFullFieldName()));
        }
        this.elementType = colmd.getElementType();
        final Class element_class = clr.classForName(this.elementType);
        if (ClassUtils.isReferenceType(element_class)) {
            this.elementIsPersistentInterface = storeMgr.getNucleusContext().getMetaDataManager().isPersistentInterface(element_class.getName());
            if (this.elementIsPersistentInterface) {
                this.emd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForInterface(element_class, clr);
            }
            else {
                this.emd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForImplementationOfReference(element_class, null, clr);
                if (this.emd != null) {}
            }
        }
        else {
            this.emd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(element_class, clr);
        }
        if (this.emd == null) {
            throw new NucleusUserException(FKSetStore.LOCALISER.msg("056003", element_class.getName(), mmd.getFullFieldName()));
        }
        this.elementInfo = this.getElementInformationForClass();
        if (this.elementInfo == null || this.elementInfo.length == 0) {
            throw new NucleusUserException(FKSetStore.LOCALISER.msg("056075", this.ownerMemberMetaData.getFullFieldName(), this.elementType));
        }
        this.elementMapping = this.elementInfo[0].getDatastoreClass().getIdMapping();
        this.elementsAreEmbedded = false;
        this.elementsAreSerialised = false;
        if (mmd.getMappedBy() != null) {
            final AbstractMemberMetaData eofmd = this.emd.getMetaDataForMember(mmd.getMappedBy());
            if (eofmd == null) {
                throw new NucleusUserException(FKSetStore.LOCALISER.msg("056024", mmd.getFullFieldName(), mmd.getMappedBy(), element_class.getName()));
            }
            final String ownerFieldName = eofmd.getName();
            this.ownerFieldNumber = this.emd.getAbsolutePositionOfMember(ownerFieldName);
            this.ownerMapping = this.elementInfo[0].getDatastoreClass().getMemberMapping(eofmd);
            if (this.ownerMapping == null && this.elementInfo.length > 1) {
                this.ownerMapping = this.elementInfo[0].getDatastoreClass().getMemberMapping(eofmd.getName());
            }
            if (this.ownerMapping == null) {
                throw new NucleusUserException(FKSetStore.LOCALISER.msg("056029", mmd.getAbstractClassMetaData().getFullClassName(), mmd.getName(), this.elementType, ownerFieldName));
            }
            if (this.isEmbeddedMapping(this.ownerMapping)) {
                throw new NucleusUserException(FKSetStore.LOCALISER.msg("056026", ownerFieldName, this.elementType, eofmd.getTypeName(), mmd.getClassName()));
            }
        }
        else {
            this.ownerFieldNumber = -1;
            this.ownerMapping = this.elementInfo[0].getDatastoreClass().getExternalMapping(mmd, 5);
            if (this.ownerMapping == null) {
                throw new NucleusUserException(FKSetStore.LOCALISER.msg("056030", mmd.getAbstractClassMetaData().getFullClassName(), mmd.getName(), this.elementType));
            }
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
    
    private boolean updateElementFk(final ObjectProvider op, final Object element, final Object owner) {
        if (element == null) {
            return false;
        }
        this.validateElementForWriting(op.getExecutionContext(), element, null);
        final ExecutionContext ec = op.getExecutionContext();
        String stmt = this.getUpdateFkStmt(element);
        boolean retval;
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                int jdbcPosition = 1;
                if (this.elementInfo.length > 1) {
                    final DatastoreClass table = this.storeMgr.getDatastoreClass(element.getClass().getName(), this.clr);
                    if (table != null) {
                        stmt = stmt.replace("<TABLE NAME>", table.toString());
                    }
                    else {
                        NucleusLogger.PERSISTENCE.warn("FKSetStore.updateElementFK : need to set table in statement but dont know table where to store " + element);
                    }
                }
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, stmt, false);
                try {
                    if (owner == null) {
                        if (this.ownerMemberMetaData != null) {
                            this.ownerMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, this.ownerMapping), null, op, this.ownerMemberMetaData.getAbsoluteFieldNumber());
                        }
                        else {
                            this.ownerMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, this.ownerMapping), null);
                        }
                    }
                    else if (this.ownerMemberMetaData != null) {
                        this.ownerMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, this.ownerMapping), op.getObject(), op, this.ownerMemberMetaData.getAbsoluteFieldNumber());
                    }
                    else {
                        this.ownerMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, this.ownerMapping), op.getObject());
                    }
                    jdbcPosition += this.ownerMapping.getNumberOfDatastoreMappings();
                    if (this.relationDiscriminatorMapping != null) {
                        jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                    }
                    jdbcPosition = BackingStoreHelper.populateElementForWhereClauseInStatement(ec, ps, element, jdbcPosition, this.elementMapping);
                    sqlControl.executeStatementUpdate(ec, mconn, stmt, ps, true);
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
            throw new NucleusDataStoreException(FKSetStore.LOCALISER.msg("056027", stmt), e);
        }
        return retval;
    }
    
    protected int getFieldNumberInElementForBidirectional(final ObjectProvider op) {
        if (this.ownerFieldNumber < 0) {
            return -1;
        }
        return op.getClassMetaData().getAbsolutePositionOfMember(this.ownerMemberMetaData.getMappedBy());
    }
    
    @Override
    public void update(final ObjectProvider op, final Collection coll) {
        if (coll == null || coll.isEmpty()) {
            this.clear(op);
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
    public boolean add(final ObjectProvider op, Object element, final int size) {
        if (element == null) {
            throw new NucleusUserException(FKSetStore.LOCALISER.msg("056039"));
        }
        final Object newOwner = op.getObject();
        final ExecutionContext ec = op.getExecutionContext();
        final boolean isPersistentInterface = this.storeMgr.getNucleusContext().getMetaDataManager().isPersistentInterface(this.elementType);
        DatastoreClass elementTable = null;
        if (isPersistentInterface) {
            elementTable = this.storeMgr.getDatastoreClass(this.storeMgr.getNucleusContext().getMetaDataManager().getImplementationNameForPersistentInterface(this.elementType), this.clr);
        }
        else {
            final Class elementTypeCls = this.clr.classForName(this.elementType);
            if (elementTypeCls.isInterface()) {
                elementTable = this.storeMgr.getDatastoreClass(element.getClass().getName(), this.clr);
            }
            else {
                elementTable = this.storeMgr.getDatastoreClass(this.elementType, this.clr);
            }
        }
        if (elementTable == null) {
            final AbstractClassMetaData[] managingCmds = this.storeMgr.getClassesManagingTableForClass(this.emd, this.clr);
            if (managingCmds != null && managingCmds.length > 0) {
                for (int i = 0; i < managingCmds.length; ++i) {
                    final Class tblCls = this.clr.classForName(managingCmds[i].getFullClassName());
                    if (tblCls.isAssignableFrom(element.getClass())) {
                        elementTable = this.storeMgr.getDatastoreClass(managingCmds[i].getFullClassName(), this.clr);
                        break;
                    }
                }
            }
        }
        final DatastoreClass elementTbl = elementTable;
        final boolean inserted = this.validateElementForWriting(ec, element, new FieldValues() {
            @Override
            public void fetchFields(final ObjectProvider elementOP) {
                if (elementTbl != null) {
                    final JavaTypeMapping externalFKMapping = elementTbl.getExternalMapping(FKSetStore.this.ownerMemberMetaData, 5);
                    if (externalFKMapping != null) {
                        elementOP.setAssociatedValue(externalFKMapping, op.getObject());
                    }
                    if (FKSetStore.this.relationDiscriminatorMapping != null) {
                        elementOP.setAssociatedValue(FKSetStore.this.relationDiscriminatorMapping, FKSetStore.this.relationDiscriminatorValue);
                    }
                }
                final int fieldNumInElement = FKSetStore.this.getFieldNumberInElementForBidirectional(elementOP);
                if (fieldNumInElement >= 0) {
                    final Object currentOwner = elementOP.provideField(fieldNumInElement);
                    if (currentOwner == null) {
                        NucleusLogger.PERSISTENCE.info(BaseContainerStore.LOCALISER.msg("056037", op.getObjectAsPrintable(), FKSetStore.this.ownerMemberMetaData.getFullFieldName(), StringUtils.toJVMIDString(elementOP.getObject())));
                        elementOP.replaceFieldMakeDirty(fieldNumInElement, newOwner);
                    }
                    else if (currentOwner != newOwner) {
                        final Object ownerId1 = ec.getApiAdapter().getIdForObject(currentOwner);
                        final Object ownerId2 = ec.getApiAdapter().getIdForObject(newOwner);
                        if (ownerId1 != null && ownerId2 != null && ownerId1.equals(ownerId2)) {
                            if (!elementOP.getExecutionContext().getApiAdapter().isDetached(newOwner)) {
                                elementOP.replaceField(fieldNumInElement, newOwner);
                            }
                        }
                        else if (op.getReferencedPC() == null) {
                            throw new NucleusUserException(BaseContainerStore.LOCALISER.msg("056038", op.getObjectAsPrintable(), FKSetStore.this.ownerMemberMetaData.getFullFieldName(), StringUtils.toJVMIDString(elementOP.getObject()), StringUtils.toJVMIDString(currentOwner)));
                        }
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
        if (inserted) {
            return true;
        }
        ObjectProvider elementOP = ec.findObjectProvider(element);
        if (elementOP == null) {
            final Object elementId = ec.getApiAdapter().getIdForObject(element);
            if (elementId != null) {
                element = ec.findObject(elementId, false, false, element.getClass().getName());
                if (element != null) {
                    elementOP = ec.findObjectProvider(element);
                }
            }
        }
        final int fieldNumInElement = this.getFieldNumberInElementForBidirectional(elementOP);
        if (fieldNumInElement >= 0) {
            elementOP.isLoaded(fieldNumInElement);
            final Object oldOwner = elementOP.provideField(fieldNumInElement);
            if (oldOwner != newOwner) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(FKSetStore.LOCALISER.msg("055009", op.getObjectAsPrintable(), this.ownerMemberMetaData.getFullFieldName(), StringUtils.toJVMIDString(element)));
                }
                elementOP.replaceFieldMakeDirty(fieldNumInElement, newOwner);
                if (ec.getManageRelations()) {
                    ec.getRelationshipManager(elementOP).relationChange(fieldNumInElement, oldOwner, newOwner);
                }
                if (ec.isFlushing()) {
                    elementOP.flush();
                }
            }
            return oldOwner != newOwner;
        }
        final boolean contained = this.contains(op, element);
        return !contained && this.updateElementFk(op, element, newOwner);
    }
    
    @Override
    public boolean addAll(final ObjectProvider op, final Collection elements, final int size) {
        if (elements == null || elements.size() == 0) {
            return false;
        }
        boolean success = false;
        final Iterator iter = elements.iterator();
        while (iter.hasNext()) {
            if (this.add(op, iter.next(), -1)) {
                success = true;
            }
        }
        return success;
    }
    
    @Override
    public boolean remove(final ObjectProvider op, final Object element, final int size, final boolean allowDependentField) {
        if (element == null) {
            return false;
        }
        if (!this.validateElementForReading(op, element)) {
            return false;
        }
        Object elementToRemove = element;
        final ExecutionContext ec = op.getExecutionContext();
        if (ec.getApiAdapter().isDetached(element)) {
            elementToRemove = ec.findObject(ec.getApiAdapter().getIdForObject(element), true, false, element.getClass().getName());
        }
        final ObjectProvider elementOP = ec.findObjectProvider(elementToRemove);
        Object oldOwner = null;
        if (this.ownerFieldNumber >= 0 && !ec.getApiAdapter().isDeleted(elementToRemove)) {
            elementOP.isLoaded(this.ownerFieldNumber);
            oldOwner = elementOP.provideField(this.ownerFieldNumber);
        }
        if (this.ownerFieldNumber >= 0 && oldOwner != op.getObject() && oldOwner != null) {
            return false;
        }
        final boolean deleteElement = this.checkRemovalOfElementShouldDelete(op);
        if (deleteElement) {
            if (ec.getApiAdapter().isPersistable(elementToRemove) && ec.getApiAdapter().isDeleted(elementToRemove)) {
                elementOP.flush();
            }
            else {
                ec.deleteObjectInternal(elementToRemove);
            }
        }
        else {
            this.manageRemovalOfElement(op, elementToRemove);
            this.updateElementFk(op, elementToRemove, null);
        }
        return true;
    }
    
    @Override
    public boolean removeAll(final ObjectProvider op, final Collection elements, final int size) {
        if (elements == null || elements.size() == 0) {
            return false;
        }
        boolean success = true;
        final Iterator iter = elements.iterator();
        while (iter.hasNext()) {
            if (this.remove(op, iter.next(), -1, true)) {
                success = false;
            }
        }
        return success;
    }
    
    protected boolean checkRemovalOfElementShouldDelete(final ObjectProvider op) {
        boolean delete = false;
        boolean dependent = this.ownerMemberMetaData.getCollection().isDependentElement();
        if (this.ownerMemberMetaData.isCascadeRemoveOrphans()) {
            dependent = true;
        }
        if (dependent) {
            if (NucleusLogger.DATASTORE.isDebugEnabled()) {
                NucleusLogger.DATASTORE.debug(FKSetStore.LOCALISER.msg("056034"));
            }
            delete = true;
        }
        else if (this.ownerMapping.isNullable()) {
            if (NucleusLogger.DATASTORE.isDebugEnabled()) {
                NucleusLogger.DATASTORE.debug(FKSetStore.LOCALISER.msg("056036"));
            }
            delete = false;
        }
        else {
            if (NucleusLogger.DATASTORE.isDebugEnabled()) {
                NucleusLogger.DATASTORE.debug(FKSetStore.LOCALISER.msg("056035"));
            }
            delete = true;
        }
        return delete;
    }
    
    protected void manageRemovalOfElement(final ObjectProvider op, final Object element) {
        final ExecutionContext ec = op.getExecutionContext();
        if (this.relationType == RelationType.ONE_TO_MANY_BI && !ec.getApiAdapter().isDeleted(element)) {
            final ObjectProvider elementOP = ec.findObjectProvider(element);
            if (elementOP != null) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(FKSetStore.LOCALISER.msg("055010", op.getObjectAsPrintable(), this.ownerMemberMetaData.getFullFieldName(), StringUtils.toJVMIDString(element)));
                }
                final int relatedFieldNumber = this.getFieldNumberInElementForBidirectional(elementOP);
                final Object currentValue = elementOP.provideField(relatedFieldNumber);
                if (currentValue != null) {
                    elementOP.replaceFieldMakeDirty(relatedFieldNumber, null);
                    if (ec.isFlushing()) {
                        elementOP.flush();
                    }
                }
            }
        }
    }
    
    @Override
    public void clear(final ObjectProvider op) {
        final ExecutionContext ec = op.getExecutionContext();
        final boolean deleteElements = this.checkRemovalOfElementShouldDelete(op);
        if (deleteElements) {
            final Iterator elementsIter = this.iterator(op);
            if (elementsIter != null) {
                while (elementsIter.hasNext()) {
                    final Object element = elementsIter.next();
                    if (ec.getApiAdapter().isPersistable(element) && ec.getApiAdapter().isDeleted(element)) {
                        ec.findObjectProvider(element).flush();
                    }
                    else {
                        ec.deleteObjectInternal(element);
                    }
                }
            }
        }
        else {
            op.isLoaded(this.ownerMemberMetaData.getAbsoluteFieldNumber());
            final Collection value = (Collection)op.provideField(this.ownerMemberMetaData.getAbsoluteFieldNumber());
            Iterator elementsIter2 = null;
            if (value != null && !value.isEmpty()) {
                elementsIter2 = value.iterator();
            }
            else {
                elementsIter2 = this.iterator(op);
            }
            if (elementsIter2 != null) {
                while (elementsIter2.hasNext()) {
                    final Object element2 = elementsIter2.next();
                    this.manageRemovalOfElement(op, element2);
                }
            }
            String stmt = this.getClearNullifyStmt();
            try {
                if (this.elementInfo.length > 1) {
                    final DatastoreClass table = this.storeMgr.getDatastoreClass(this.elementInfo[0].getClassName(), this.clr);
                    if (table != null) {
                        stmt = stmt.replace("<TABLE NAME>", table.toString());
                    }
                    else {
                        NucleusLogger.PERSISTENCE.warn("FKSetStore.updateElementFK : need to set table in statement but dont know table where to store " + this.elementInfo[0].getClassName());
                    }
                }
                final ManagedConnection mconn = this.storeMgr.getConnection(ec);
                final SQLController sqlControl = this.storeMgr.getSQLController();
                try {
                    final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, stmt, false);
                    try {
                        final int jdbcPosition = 1;
                        BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                        sqlControl.executeStatementUpdate(ec, mconn, stmt, ps, true);
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
                throw new NucleusDataStoreException(FKSetStore.LOCALISER.msg("056013", stmt), e);
            }
        }
    }
    
    protected String getClearNullifyStmt() {
        if (this.clearNullifyStmt == null) {
            synchronized (this) {
                final StringBuffer stmt = new StringBuffer("UPDATE ");
                if (this.elementInfo.length > 1) {
                    stmt.append("<TABLE NAME>");
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
                if (this.relationDiscriminatorMapping != null) {
                    for (int i = 0; i < this.relationDiscriminatorMapping.getNumberOfDatastoreMappings(); ++i) {
                        stmt.append(", ");
                        stmt.append(this.relationDiscriminatorMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                        stmt.append("=NULL");
                    }
                }
                stmt.append(" WHERE ");
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
                this.clearNullifyStmt = stmt.toString();
            }
        }
        return this.clearNullifyStmt;
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
            stmt.append("<TABLE NAME>");
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
    
    @Override
    public Iterator iterator(final ObjectProvider op) {
        final ExecutionContext ec = op.getExecutionContext();
        if (this.elementInfo == null || this.elementInfo.length == 0) {
            return null;
        }
        final IteratorStatement iterStmt = this.getIteratorStatement(op.getExecutionContext().getClassLoaderResolver(), op.getExecutionContext().getFetchPlan(), true);
        final SQLStatement sqlStmt = iterStmt.getSQLStatement();
        final StatementClassMapping iteratorMappingClass = iterStmt.getStatementClassMapping();
        int inputParamNum = 1;
        final StatementMappingIndex ownerStmtMapIdx = new StatementMappingIndex(this.ownerMapping);
        if (sqlStmt.getNumberOfUnions() > 0) {
            for (int j = 0; j < sqlStmt.getNumberOfUnions() + 1; ++j) {
                final int[] paramPositions = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
                for (int k = 0; k < this.ownerMapping.getNumberOfDatastoreMappings(); ++k) {
                    paramPositions[k] = inputParamNum++;
                }
                ownerStmtMapIdx.addParameterOccurrence(paramPositions);
            }
        }
        else {
            final int[] paramPositions2 = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
            for (int i = 0; i < this.ownerMapping.getNumberOfDatastoreMappings(); ++i) {
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
                    ownerStmtMapIdx.getMapping().setObject(ec, ps, ownerStmtMapIdx.getParameterPositionsForOccurrence(paramInstance), op.getObject());
                }
                try {
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, stmt, ps);
                    try {
                        ResultObjectFactory rof = null;
                        if (this.elementsAreEmbedded || this.elementsAreSerialised) {
                            throw new NucleusException("Cannot have FK set with non-persistent objects");
                        }
                        rof = this.storeMgr.newResultObjectFactory(this.emd, iteratorMappingClass, false, null, this.clr.classForName(this.elementType));
                        return new SetStoreIterator(op, rs, rof, this);
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
            throw new NucleusDataStoreException(FKSetStore.LOCALISER.msg("056006", stmt), e);
        }
        catch (MappedDatastoreException e2) {
            throw new NucleusDataStoreException(FKSetStore.LOCALISER.msg("056006", stmt), e2);
        }
    }
    
    public IteratorStatement getIteratorStatement(final ClassLoaderResolver clr, final FetchPlan fp, final boolean addRestrictionOnOwner) {
        SQLStatement sqlStmt = null;
        final SQLExpressionFactory exprFactory = this.storeMgr.getSQLExpressionFactory();
        final StatementClassMapping iteratorMappingClass = new StatementClassMapping();
        if (this.elementInfo[0].getDatastoreClass().getDiscriminatorMetaData() != null && this.elementInfo[0].getDatastoreClass().getDiscriminatorMetaData().getStrategy() != DiscriminatorStrategy.NONE) {
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
            SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, iteratorMappingClass, fp, sqlStmt.getPrimaryTable(), this.emd, 0);
        }
        else {
            boolean selectFetchPlan = true;
            final Class elementTypeCls = clr.classForName(this.elementType);
            if (elementTypeCls.isInterface() && this.elementInfo.length > 1) {
                selectFetchPlan = false;
            }
            for (int j = 0; j < this.elementInfo.length; ++j) {
                final Class elementCls = clr.classForName(this.elementInfo[j].getClassName());
                final UnionStatementGenerator stmtGen = new UnionStatementGenerator(this.storeMgr, clr, elementCls, true, null, null);
                stmtGen.setOption("selectNucleusType");
                iteratorMappingClass.setNucleusTypeColumnName("NUCLEUS_TYPE");
                final SQLStatement subStmt = stmtGen.getStatement();
                if (selectFetchPlan) {
                    if (sqlStmt == null) {
                        SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(subStmt, iteratorMappingClass, fp, subStmt.getPrimaryTable(), this.elementInfo[j].getAbstractClassMetaData(), 0);
                    }
                    else {
                        SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(subStmt, null, fp, subStmt.getPrimaryTable(), this.elementInfo[j].getAbstractClassMetaData(), 0);
                    }
                }
                else if (sqlStmt == null) {
                    SQLStatementHelper.selectIdentityOfCandidateInStatement(subStmt, iteratorMappingClass, this.elementInfo[j].getAbstractClassMetaData());
                }
                else {
                    SQLStatementHelper.selectIdentityOfCandidateInStatement(subStmt, null, this.elementInfo[j].getAbstractClassMetaData());
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
