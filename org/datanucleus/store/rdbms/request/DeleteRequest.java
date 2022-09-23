// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.request;

import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import java.util.Set;
import org.datanucleus.metadata.ForeignKeyMetaData;
import org.datanucleus.store.rdbms.mapping.MappingHelper;
import org.datanucleus.metadata.InterfaceMetaData;
import java.util.List;
import java.util.Iterator;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.ExecutionContext;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.util.ArrayList;
import org.datanucleus.exceptions.NucleusOptimisticException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import java.util.HashSet;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.store.rdbms.mapping.MappingConsumer;
import org.datanucleus.metadata.VersionStrategy;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.store.rdbms.mapping.MappingCallbacks;

public class DeleteRequest extends Request
{
    private final MappingCallbacks[] callbacks;
    private final String deleteStmt;
    private final String deleteStmtOptimistic;
    private StatementMappingDefinition mappingStatementIndex;
    private StatementMappingIndex multitenancyStatementMapping;
    private final int[] whereFieldNumbers;
    private final AbstractMemberMetaData[] oneToOneNonOwnerFields;
    protected AbstractClassMetaData cmd;
    protected VersionMetaData versionMetaData;
    protected boolean versionChecks;
    
    public DeleteRequest(final DatastoreClass table, final AbstractClassMetaData cmd, final ClassLoaderResolver clr) {
        super(table);
        this.cmd = null;
        this.versionMetaData = null;
        this.versionChecks = false;
        this.cmd = cmd;
        this.versionMetaData = table.getVersionMetaData();
        if (this.versionMetaData != null && this.versionMetaData.getVersionStrategy() != VersionStrategy.NONE) {
            this.versionChecks = true;
        }
        this.mappingStatementIndex = new StatementMappingDefinition();
        final DeleteMappingConsumer consumer = new DeleteMappingConsumer(clr, cmd);
        table.provideNonPrimaryKeyMappings(consumer);
        consumer.setWhereClauseConsumption();
        if (cmd.getIdentityType() == IdentityType.APPLICATION) {
            table.providePrimaryKeyMappings(consumer);
        }
        else if (cmd.getIdentityType() == IdentityType.DATASTORE) {
            table.provideDatastoreIdMappings(consumer);
        }
        else {
            final AbstractMemberMetaData[] mmds = cmd.getManagedMembers();
            table.provideMappingsForMembers(consumer, mmds, false);
        }
        table.provideMultitenancyMapping(consumer);
        this.deleteStmt = consumer.getStatement();
        if (this.versionMetaData != null) {
            if (this.versionMetaData.getFieldName() != null) {
                final AbstractMemberMetaData[] versionFmds = { cmd.getMetaDataForMember(this.versionMetaData.getFieldName()) };
                table.provideMappingsForMembers(consumer, versionFmds, false);
            }
            else {
                table.provideVersionMappings(consumer);
            }
        }
        this.deleteStmtOptimistic = consumer.getStatement();
        this.whereFieldNumbers = consumer.getWhereFieldNumbers();
        this.callbacks = consumer.getMappingCallBacks().toArray(new MappingCallbacks[consumer.getMappingCallBacks().size()]);
        this.oneToOneNonOwnerFields = consumer.getOneToOneNonOwnerFields();
    }
    
    @Override
    public void execute(final ObjectProvider op) {
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(DeleteRequest.LOCALISER.msg("052210", op.getObjectAsPrintable(), this.table));
        }
        final ClassLoaderResolver clr = op.getExecutionContext().getClassLoaderResolver();
        HashSet relatedObjectsToDelete = null;
        for (int i = 0; i < this.callbacks.length; ++i) {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(DeleteRequest.LOCALISER.msg("052212", op.getObjectAsPrintable(), ((JavaTypeMapping)this.callbacks[i]).getMemberMetaData().getFullFieldName()));
            }
            this.callbacks[i].preDelete(op);
            final JavaTypeMapping mapping = (JavaTypeMapping)this.callbacks[i];
            final AbstractMemberMetaData mmd = mapping.getMemberMetaData();
            final RelationType relationType = mmd.getRelationType(clr);
            if (mmd.isDependent()) {
                if (relationType != RelationType.ONE_TO_ONE_UNI) {
                    if (relationType != RelationType.ONE_TO_ONE_BI || mmd.getMappedBy() != null) {
                        continue;
                    }
                }
                try {
                    op.isLoaded(mmd.getAbsoluteFieldNumber());
                    final Object relatedPc = op.provideField(mmd.getAbsoluteFieldNumber());
                    final boolean relatedObjectDeleted = op.getExecutionContext().getApiAdapter().isDeleted(relatedPc);
                    if (!relatedObjectDeleted) {
                        if (relatedObjectsToDelete == null) {
                            relatedObjectsToDelete = new HashSet();
                        }
                        relatedObjectsToDelete.add(relatedPc);
                    }
                }
                catch (Exception ex) {}
            }
        }
        if (this.oneToOneNonOwnerFields != null && this.oneToOneNonOwnerFields.length > 0) {
            for (int i = 0; i < this.oneToOneNonOwnerFields.length; ++i) {
                final AbstractMemberMetaData relatedFmd = this.oneToOneNonOwnerFields[i];
                this.updateOneToOneBidirectionalOwnerObjectForField(op, relatedFmd);
            }
        }
        String stmt = null;
        final ExecutionContext ec = op.getExecutionContext();
        final RDBMSStoreManager storeMgr = this.table.getStoreManager();
        final boolean optimisticChecks = this.versionMetaData != null && ec.getTransaction().getOptimistic() && this.versionChecks;
        if (optimisticChecks) {
            stmt = this.deleteStmtOptimistic;
        }
        else {
            stmt = this.deleteStmt;
        }
        try {
            final ManagedConnection mconn = storeMgr.getConnection(ec);
            final SQLController sqlControl = storeMgr.getSQLController();
            try {
                boolean batch = true;
                if (optimisticChecks || !ec.getTransaction().isActive()) {
                    batch = false;
                }
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, stmt, batch);
                try {
                    if (this.cmd.getIdentityType() == IdentityType.DATASTORE) {
                        final StatementMappingIndex mapIdx = this.mappingStatementIndex.getWhereDatastoreId();
                        for (int j = 0; j < mapIdx.getNumberOfParameterOccurrences(); ++j) {
                            this.table.getDatastoreObjectIdMapping().setObject(ec, ps, mapIdx.getParameterPositionsForOccurrence(j), op.getInternalObjectId());
                        }
                    }
                    else {
                        final StatementClassMapping mappingDefinition = new StatementClassMapping();
                        final StatementMappingIndex[] idxs = this.mappingStatementIndex.getWhereFields();
                        for (int k = 0; k < idxs.length; ++k) {
                            if (idxs[k] != null) {
                                mappingDefinition.addMappingForMember(k, idxs[k]);
                            }
                        }
                        op.provideFields(this.whereFieldNumbers, storeMgr.getFieldManagerForStatementGeneration(op, ps, mappingDefinition));
                    }
                    if (this.multitenancyStatementMapping != null) {
                        this.table.getMultitenancyMapping().setObject(ec, ps, this.multitenancyStatementMapping.getParameterPositionsForOccurrence(0), storeMgr.getStringProperty("datanucleus.TenantID"));
                    }
                    if (optimisticChecks) {
                        final JavaTypeMapping verMapping = this.mappingStatementIndex.getWhereVersion().getMapping();
                        final Object currentVersion = op.getTransactionalVersion();
                        if (currentVersion == null) {
                            final String msg = DeleteRequest.LOCALISER.msg("052202", op.getInternalObjectId(), this.table);
                            NucleusLogger.PERSISTENCE.error(msg);
                            throw new NucleusException(msg);
                        }
                        final StatementMappingIndex mapIdx2 = this.mappingStatementIndex.getWhereVersion();
                        for (int l = 0; l < mapIdx2.getNumberOfParameterOccurrences(); ++l) {
                            verMapping.setObject(ec, ps, mapIdx2.getParameterPositionsForOccurrence(l), currentVersion);
                        }
                    }
                    final int[] rcs = sqlControl.executeStatementUpdate(ec, mconn, stmt, ps, !batch);
                    if (optimisticChecks && rcs[0] == 0) {
                        final String msg2 = DeleteRequest.LOCALISER.msg("052203", op.getObjectAsPrintable(), op.getInternalObjectId(), "" + op.getTransactionalVersion());
                        NucleusLogger.DATASTORE.error(msg2);
                        throw new NucleusOptimisticException(msg2, op.getObject());
                    }
                    if (relatedObjectsToDelete != null && !relatedObjectsToDelete.isEmpty()) {
                        for (final Object relatedObject : relatedObjectsToDelete) {
                            ec.deleteObjectInternal(relatedObject);
                        }
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
            final String msg3 = DeleteRequest.LOCALISER.msg("052211", op.getObjectAsPrintable(), stmt, e.getMessage());
            NucleusLogger.DATASTORE_PERSIST.warn(msg3);
            final List exceptions = new ArrayList();
            exceptions.add(e);
            while ((e = e.getNextException()) != null) {
                exceptions.add(e);
            }
            throw new NucleusDataStoreException(msg3, exceptions.toArray(new Throwable[exceptions.size()]));
        }
    }
    
    private void updateOneToOneBidirectionalOwnerObjectForField(final ObjectProvider op, final AbstractMemberMetaData fmd) {
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(DeleteRequest.LOCALISER.msg("052217", op.getObjectAsPrintable(), fmd.getFullFieldName()));
        }
        final RDBMSStoreManager storeMgr = this.table.getStoreManager();
        final ExecutionContext ec = op.getExecutionContext();
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        final AbstractMemberMetaData[] relatedMmds = fmd.getRelatedMemberMetaData(clr);
        boolean checkFK = true;
        if (ec.getStringProperty("datanucleus.deletionPolicy").equals("JDO2")) {
            checkFK = false;
        }
        if (checkFK) {
            for (int i = 0; i < relatedMmds.length; ++i) {
                final ForeignKeyMetaData relFkmd = relatedMmds[i].getForeignKeyMetaData();
                if (relFkmd != null && relFkmd.getDeleteAction() != null) {
                    return;
                }
            }
        }
        final String fullClassName = ((AbstractClassMetaData)relatedMmds[0].getParent()).getFullClassName();
        String[] classes;
        if (((AbstractClassMetaData)relatedMmds[0].getParent()) instanceof InterfaceMetaData) {
            classes = storeMgr.getNucleusContext().getMetaDataManager().getClassesImplementingInterface(fullClassName, clr);
        }
        else {
            classes = new String[] { fullClassName };
        }
        final Set datastoreClasses = new HashSet();
        for (int j = 0; j < classes.length; ++j) {
            datastoreClasses.add(storeMgr.getDatastoreClass(classes[j], clr));
        }
        for (final DatastoreClass refTable : datastoreClasses) {
            final JavaTypeMapping refMapping = refTable.getMemberMapping(fmd.getMappedBy());
            if (refMapping.isNullable()) {
                final StringBuffer clearLinkStmt = new StringBuffer("UPDATE " + refTable.toString() + " SET ");
                for (int k = 0; k < refMapping.getNumberOfDatastoreMappings(); ++k) {
                    if (k > 0) {
                        clearLinkStmt.append(",");
                    }
                    clearLinkStmt.append(refMapping.getDatastoreMapping(k).getColumn().getIdentifier());
                    clearLinkStmt.append("=NULL");
                }
                clearLinkStmt.append(" WHERE ");
                for (int k = 0; k < refMapping.getNumberOfDatastoreMappings(); ++k) {
                    if (k > 0) {
                        clearLinkStmt.append(" AND ");
                    }
                    clearLinkStmt.append(refMapping.getDatastoreMapping(k).getColumn().getIdentifier());
                    clearLinkStmt.append("=?");
                }
                try {
                    final ManagedConnection mconn = storeMgr.getConnection(ec);
                    final SQLController sqlControl = storeMgr.getSQLController();
                    try {
                        PreparedStatement ps = null;
                        try {
                            ps = sqlControl.getStatementForUpdate(mconn, clearLinkStmt.toString(), false);
                            refMapping.setObject(ec, ps, MappingHelper.getMappingIndices(1, refMapping), op.getObject());
                            sqlControl.executeStatementUpdate(ec, mconn, clearLinkStmt.toString(), ps, true);
                        }
                        finally {
                            if (ps != null) {
                                sqlControl.closeStatement(mconn, ps);
                            }
                        }
                    }
                    finally {
                        mconn.release();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new NucleusDataStoreException("Update request failed", e);
                }
            }
        }
    }
    
    private class DeleteMappingConsumer implements MappingConsumer
    {
        boolean initialized;
        StringBuffer where;
        int paramIndex;
        private List whereFields;
        private List oneToOneNonOwnerFields;
        private List mc;
        private final ClassLoaderResolver clr;
        private final AbstractClassMetaData cmd;
        private boolean whereClauseConsumption;
        
        public DeleteMappingConsumer(final ClassLoaderResolver clr, final AbstractClassMetaData cmd) {
            this.initialized = false;
            this.where = new StringBuffer();
            this.paramIndex = 1;
            this.whereFields = new ArrayList();
            this.oneToOneNonOwnerFields = new ArrayList();
            this.mc = new ArrayList();
            this.whereClauseConsumption = false;
            this.clr = clr;
            this.cmd = cmd;
            this.paramIndex = 1;
        }
        
        public void setWhereClauseConsumption() {
            this.whereClauseConsumption = true;
        }
        
        @Override
        public void preConsumeMapping(final int highest) {
            if (!this.initialized) {
                DeleteRequest.this.mappingStatementIndex.setWhereFields(new StatementMappingIndex[highest]);
                DeleteRequest.this.mappingStatementIndex.setUpdateFields(new StatementMappingIndex[highest]);
                this.initialized = true;
            }
        }
        
        @Override
        public void consumeMapping(final JavaTypeMapping m, final AbstractMemberMetaData mmd) {
            if (!mmd.getAbstractClassMetaData().isSameOrAncestorOf(this.cmd)) {
                return;
            }
            if (m.includeInUpdateStatement()) {
                if (this.whereClauseConsumption) {
                    VersionMetaData vermd = this.cmd.getVersionMetaDataForTable();
                    if (!DeleteRequest.this.table.managesClass(this.cmd.getFullClassName())) {
                        vermd = this.cmd.getBaseAbstractClassMetaData().getVersionMetaDataForClass();
                    }
                    if (vermd != null && vermd.getFieldName() != null && mmd.getName().equals(vermd.getFieldName())) {
                        final StatementMappingIndex sei = new StatementMappingIndex(m);
                        DeleteRequest.this.mappingStatementIndex.setWhereVersion(sei);
                        final int[] parametersIndex = { this.paramIndex++ };
                        sei.addParameterOccurrence(parametersIndex);
                        if (this.where.length() > 0) {
                            this.where.append(" AND ");
                        }
                        this.where.append(m.getDatastoreMapping(0).getColumn().getIdentifier());
                        this.where.append("=");
                        this.where.append(((AbstractDatastoreMapping)m.getDatastoreMapping(0)).getUpdateInputParameter());
                    }
                    else {
                        final Integer abs_field_num = mmd.getAbsoluteFieldNumber();
                        final int[] parametersIndex = new int[m.getNumberOfDatastoreMappings()];
                        final StatementMappingIndex sei2 = new StatementMappingIndex(m);
                        sei2.addParameterOccurrence(parametersIndex);
                        DeleteRequest.this.mappingStatementIndex.getWhereFields()[mmd.getAbsoluteFieldNumber()] = sei2;
                        for (int j = 0; j < parametersIndex.length; ++j) {
                            if (this.where.length() > 0) {
                                this.where.append(" AND ");
                            }
                            this.where.append(m.getDatastoreMapping(j).getColumn().getIdentifier());
                            this.where.append("=");
                            this.where.append(((AbstractDatastoreMapping)m.getDatastoreMapping(j)).getUpdateInputParameter());
                            if (!this.whereFields.contains(abs_field_num)) {
                                this.whereFields.add(abs_field_num);
                            }
                            parametersIndex[j] = this.paramIndex++;
                        }
                    }
                }
                if ((m instanceof PersistableMapping || m instanceof ReferenceMapping) && m.getNumberOfDatastoreMappings() == 0) {
                    final RelationType relationType = mmd.getRelationType(this.clr);
                    if (relationType == RelationType.ONE_TO_ONE_BI) {
                        if (mmd.getMappedBy() != null) {
                            this.oneToOneNonOwnerFields.add(mmd);
                        }
                    }
                    else if (relationType == RelationType.MANY_TO_ONE_BI) {
                        final AbstractMemberMetaData[] relatedMmds = mmd.getRelatedMemberMetaData(this.clr);
                        if (mmd.getJoinMetaData() != null || relatedMmds[0].getJoinMetaData() != null) {}
                    }
                }
            }
            if (m instanceof MappingCallbacks) {
                this.mc.add(m);
            }
        }
        
        @Override
        public void consumeMapping(final JavaTypeMapping m, final int mappingType) {
            if (mappingType == 2) {
                if (this.where.length() > 0) {
                    this.where.append(" AND ");
                }
                this.where.append(m.getDatastoreMapping(0).getColumn().getIdentifier().toString());
                this.where.append("=");
                this.where.append(((AbstractDatastoreMapping)m.getDatastoreMapping(0)).getUpdateInputParameter());
                final StatementMappingIndex datastoreMappingIdx = new StatementMappingIndex(m);
                DeleteRequest.this.mappingStatementIndex.setWhereDatastoreId(datastoreMappingIdx);
                final int[] param = { this.paramIndex++ };
                datastoreMappingIdx.addParameterOccurrence(param);
            }
            else if (mappingType == 1) {
                if (this.where.length() > 0) {
                    this.where.append(" AND ");
                }
                this.where.append(m.getDatastoreMapping(0).getColumn().getIdentifier());
                this.where.append("=");
                this.where.append(((AbstractDatastoreMapping)m.getDatastoreMapping(0)).getUpdateInputParameter());
                final StatementMappingIndex versStmtIdx = new StatementMappingIndex(m);
                DeleteRequest.this.mappingStatementIndex.setWhereVersion(versStmtIdx);
                final int[] param = { this.paramIndex++ };
                versStmtIdx.addParameterOccurrence(param);
            }
            else if (mappingType == 7) {
                final JavaTypeMapping tenantMapping = DeleteRequest.this.table.getMultitenancyMapping();
                if (this.where.length() > 0) {
                    this.where.append(" AND ");
                }
                this.where.append(tenantMapping.getDatastoreMapping(0).getColumn().getIdentifier().toString());
                this.where.append("=");
                this.where.append(((AbstractDatastoreMapping)tenantMapping.getDatastoreMapping(0)).getUpdateInputParameter());
                DeleteRequest.this.multitenancyStatementMapping = new StatementMappingIndex(tenantMapping);
                final int[] param = { this.paramIndex++ };
                DeleteRequest.this.multitenancyStatementMapping.addParameterOccurrence(param);
            }
        }
        
        @Override
        public void consumeUnmappedColumn(final Column col) {
        }
        
        public int[] getWhereFieldNumbers() {
            final int[] fieldNumbers = new int[this.whereFields.size()];
            for (int i = 0; i < this.whereFields.size(); ++i) {
                fieldNumbers[i] = this.whereFields.get(i);
            }
            return fieldNumbers;
        }
        
        public AbstractMemberMetaData[] getOneToOneNonOwnerFields() {
            final AbstractMemberMetaData[] fmds = new AbstractMemberMetaData[this.oneToOneNonOwnerFields.size()];
            for (int i = 0; i < this.oneToOneNonOwnerFields.size(); ++i) {
                fmds[i] = this.oneToOneNonOwnerFields.get(i);
            }
            return fmds;
        }
        
        public List getMappingCallBacks() {
            return this.mc;
        }
        
        public String getStatement() {
            return "DELETE FROM " + DeleteRequest.this.table.toString() + " WHERE " + (Object)this.where;
        }
    }
}
