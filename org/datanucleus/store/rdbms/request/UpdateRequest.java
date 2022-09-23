// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.request;

import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.exceptions.NotYetFlushedException;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.util.ArrayList;
import org.datanucleus.util.StringUtils;
import org.datanucleus.exceptions.NucleusOptimisticException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.fieldmanager.OldValueParameterSetter;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.store.VersionHelper;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.store.rdbms.mapping.MappingConsumer;
import org.datanucleus.metadata.VersionStrategy;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.mapping.MappingCallbacks;

public class UpdateRequest extends Request
{
    private final String updateStmt;
    private final String updateStmtOptimistic;
    private final MappingCallbacks[] callbacks;
    private StatementMappingDefinition stmtMappingDefinition;
    private final int[] updateFieldNumbers;
    private final int[] whereFieldNumbers;
    protected AbstractClassMetaData cmd;
    protected VersionMetaData versionMetaData;
    protected boolean versionChecks;
    
    public UpdateRequest(final DatastoreClass table, final AbstractMemberMetaData[] reqFieldMetaData, final AbstractClassMetaData cmd, final ClassLoaderResolver clr) {
        super(table);
        this.cmd = null;
        this.versionMetaData = null;
        this.versionChecks = false;
        this.cmd = cmd;
        this.versionMetaData = table.getVersionMetaData();
        if (this.versionMetaData != null && this.versionMetaData.getVersionStrategy() != VersionStrategy.NONE) {
            this.versionChecks = true;
        }
        this.stmtMappingDefinition = new StatementMappingDefinition();
        final UpdateMappingConsumer consumer = new UpdateMappingConsumer(cmd);
        if (this.versionMetaData != null) {
            if (this.versionMetaData.getFieldName() != null) {
                final AbstractMemberMetaData[] updateFmds = new AbstractMemberMetaData[reqFieldMetaData.length + 1];
                for (int i = 0; i < reqFieldMetaData.length; ++i) {
                    updateFmds[i] = reqFieldMetaData[i];
                }
                updateFmds[updateFmds.length - 1] = cmd.getMetaDataForMember(this.versionMetaData.getFieldName());
                table.provideMappingsForMembers(consumer, updateFmds, false);
            }
            else {
                table.provideMappingsForMembers(consumer, reqFieldMetaData, false);
                table.provideVersionMappings(consumer);
            }
        }
        else {
            table.provideMappingsForMembers(consumer, reqFieldMetaData, false);
        }
        consumer.setWhereClauseConsumption(true);
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
        this.updateStmt = consumer.getStatement();
        if (this.versionMetaData != null) {
            if (this.versionMetaData.getFieldName() != null) {
                final AbstractMemberMetaData[] updateFmds = { cmd.getMetaDataForMember(this.versionMetaData.getFieldName()) };
                table.provideMappingsForMembers(consumer, updateFmds, false);
            }
            else {
                table.provideVersionMappings(consumer);
            }
        }
        this.updateStmtOptimistic = consumer.getStatement();
        this.callbacks = consumer.getMappingCallbacks().toArray(new MappingCallbacks[consumer.getMappingCallbacks().size()]);
        this.whereFieldNumbers = consumer.getWhereFieldNumbers();
        this.updateFieldNumbers = consumer.getUpdateFieldNumbers();
    }
    
    @Override
    public void execute(final ObjectProvider op) {
        String stmt = null;
        final ExecutionContext ec = op.getExecutionContext();
        final boolean optimisticChecks = this.versionMetaData != null && ec.getTransaction().getOptimistic() && this.versionChecks;
        if (optimisticChecks) {
            stmt = this.updateStmtOptimistic;
        }
        else {
            stmt = this.updateStmt;
        }
        if (stmt != null) {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                final StringBuffer fieldStr = new StringBuffer();
                for (int i = 0; i < this.updateFieldNumbers.length; ++i) {
                    if (fieldStr.length() > 0) {
                        fieldStr.append(",");
                    }
                    fieldStr.append(this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(this.updateFieldNumbers[i]).getName());
                }
                if (this.versionMetaData != null && this.versionMetaData.getFieldName() == null) {
                    if (fieldStr.length() > 0) {
                        fieldStr.append(",");
                    }
                    fieldStr.append("[VERSION]");
                }
                NucleusLogger.PERSISTENCE.debug(UpdateRequest.LOCALISER.msg("052214", op.getObjectAsPrintable(), fieldStr.toString(), this.table));
            }
            final RDBMSStoreManager storeMgr = this.table.getStoreManager();
            final boolean batch = false;
            try {
                final ManagedConnection mconn = storeMgr.getConnection(ec);
                final SQLController sqlControl = storeMgr.getSQLController();
                try {
                    final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, stmt, batch);
                    try {
                        Object currentVersion = op.getTransactionalVersion();
                        Object nextVersion = null;
                        if (this.versionMetaData != null) {
                            if (this.versionMetaData.getFieldName() != null) {
                                final AbstractMemberMetaData verfmd = this.cmd.getMetaDataForMember(this.table.getVersionMetaData().getFieldName());
                                if (currentVersion instanceof Number) {
                                    currentVersion = ((Number)currentVersion).longValue();
                                }
                                nextVersion = VersionHelper.getNextVersion(this.versionMetaData.getVersionStrategy(), currentVersion);
                                if (verfmd.getType() == Integer.class || verfmd.getType() == Integer.TYPE) {
                                    nextVersion = ((Long)nextVersion).intValue();
                                }
                                op.replaceField(verfmd.getAbsoluteFieldNumber(), nextVersion);
                            }
                            else {
                                nextVersion = VersionHelper.getNextVersion(this.versionMetaData.getVersionStrategy(), currentVersion);
                            }
                            op.setTransactionalVersion(nextVersion);
                        }
                        if (this.updateFieldNumbers != null) {
                            final StatementClassMapping mappingDefinition = new StatementClassMapping();
                            final StatementMappingIndex[] idxs = this.stmtMappingDefinition.getUpdateFields();
                            for (int j = 0; j < idxs.length; ++j) {
                                if (idxs[j] != null) {
                                    mappingDefinition.addMappingForMember(j, idxs[j]);
                                }
                            }
                            op.provideFields(this.updateFieldNumbers, storeMgr.getFieldManagerForStatementGeneration(op, ps, mappingDefinition));
                        }
                        if (this.versionMetaData != null && this.versionMetaData.getFieldName() == null) {
                            final StatementMappingIndex mapIdx = this.stmtMappingDefinition.getUpdateVersion();
                            for (int k = 0; k < mapIdx.getNumberOfParameterOccurrences(); ++k) {
                                this.table.getVersionMapping(false).setObject(ec, ps, mapIdx.getParameterPositionsForOccurrence(k), nextVersion);
                            }
                        }
                        if (this.table.getIdentityType() == IdentityType.DATASTORE) {
                            final StatementMappingIndex mapIdx = this.stmtMappingDefinition.getWhereDatastoreId();
                            for (int k = 0; k < mapIdx.getNumberOfParameterOccurrences(); ++k) {
                                this.table.getDatastoreObjectIdMapping().setObject(ec, ps, mapIdx.getParameterPositionsForOccurrence(k), op.getInternalObjectId());
                            }
                        }
                        else {
                            final StatementClassMapping mappingDefinition = new StatementClassMapping();
                            final StatementMappingIndex[] idxs = this.stmtMappingDefinition.getWhereFields();
                            for (int j = 0; j < idxs.length; ++j) {
                                if (idxs[j] != null) {
                                    mappingDefinition.addMappingForMember(j, idxs[j]);
                                }
                            }
                            FieldManager fm = null;
                            if (this.cmd.getIdentityType() == IdentityType.NONDURABLE) {
                                fm = new OldValueParameterSetter(op, ps, mappingDefinition);
                            }
                            else {
                                fm = storeMgr.getFieldManagerForStatementGeneration(op, ps, mappingDefinition);
                            }
                            op.provideFields(this.whereFieldNumbers, fm);
                        }
                        if (optimisticChecks) {
                            if (currentVersion == null) {
                                final String msg = UpdateRequest.LOCALISER.msg("052201", op.getInternalObjectId(), this.table);
                                NucleusLogger.PERSISTENCE.error(msg);
                                throw new NucleusException(msg);
                            }
                            final StatementMappingIndex mapIdx = this.stmtMappingDefinition.getWhereVersion();
                            for (int k = 0; k < mapIdx.getNumberOfParameterOccurrences(); ++k) {
                                mapIdx.getMapping().setObject(ec, ps, mapIdx.getParameterPositionsForOccurrence(k), currentVersion);
                            }
                        }
                        final int[] rcs = sqlControl.executeStatementUpdate(ec, mconn, stmt, ps, !batch);
                        if (rcs[0] == 0 && optimisticChecks) {
                            final String msg2 = UpdateRequest.LOCALISER.msg("052203", op.getObjectAsPrintable(), op.getInternalObjectId(), "" + currentVersion);
                            NucleusLogger.PERSISTENCE.error(msg2);
                            throw new NucleusOptimisticException(msg2, op.getObject());
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
                final String msg3 = UpdateRequest.LOCALISER.msg("052215", op.getObjectAsPrintable(), stmt, StringUtils.getStringFromStackTrace(e));
                NucleusLogger.DATASTORE_PERSIST.error(msg3);
                final List exceptions = new ArrayList();
                exceptions.add(e);
                while ((e = e.getNextException()) != null) {
                    exceptions.add(e);
                }
                throw new NucleusDataStoreException(msg3, exceptions.toArray(new Throwable[exceptions.size()]));
            }
        }
        for (int l = 0; l < this.callbacks.length; ++l) {
            try {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(UpdateRequest.LOCALISER.msg("052216", op.getObjectAsPrintable(), ((JavaTypeMapping)this.callbacks[l]).getMemberMetaData().getFullFieldName()));
                }
                this.callbacks[l].postUpdate(op);
            }
            catch (NotYetFlushedException e2) {
                op.updateFieldAfterInsert(e2.getPersistable(), ((JavaTypeMapping)this.callbacks[l]).getMemberMetaData().getAbsoluteFieldNumber());
            }
        }
    }
    
    private class UpdateMappingConsumer implements MappingConsumer
    {
        boolean initialized;
        int paramIndex;
        List updateFields;
        List whereFields;
        List mc;
        StringBuffer columnAssignments;
        Map assignedColumns;
        StringBuffer where;
        private final AbstractClassMetaData cmd;
        private boolean whereClauseConsumption;
        
        public UpdateMappingConsumer(final AbstractClassMetaData cmd) {
            this.initialized = false;
            this.paramIndex = 1;
            this.updateFields = new ArrayList();
            this.whereFields = new ArrayList();
            this.mc = new ArrayList();
            this.columnAssignments = new StringBuffer();
            this.assignedColumns = new HashMap();
            this.where = new StringBuffer();
            this.whereClauseConsumption = false;
            this.cmd = cmd;
        }
        
        public void setWhereClauseConsumption(final boolean whereClause) {
            this.whereClauseConsumption = whereClause;
        }
        
        @Override
        public void preConsumeMapping(final int highest) {
            if (!this.initialized) {
                UpdateRequest.this.stmtMappingDefinition.setWhereFields(new StatementMappingIndex[highest]);
                UpdateRequest.this.stmtMappingDefinition.setUpdateFields(new StatementMappingIndex[highest]);
                this.initialized = true;
            }
        }
        
        @Override
        public void consumeMapping(final JavaTypeMapping m, final AbstractMemberMetaData fmd) {
            if (!fmd.getAbstractClassMetaData().isSameOrAncestorOf(this.cmd)) {
                return;
            }
            if (m.includeInUpdateStatement()) {
                if (fmd.hasExtension("updateable") && fmd.getValueForExtension("updateable").equalsIgnoreCase("false")) {
                    return;
                }
                final ColumnMetaData[] colmds = fmd.getColumnMetaData();
                if (colmds != null && colmds.length > 0) {
                    for (int i = 0; i < colmds.length; ++i) {
                        if (!colmds[i].getUpdateable()) {
                            return;
                        }
                    }
                }
                final Integer abs_field_num = fmd.getAbsoluteFieldNumber();
                final int[] parametersIndex = new int[m.getNumberOfDatastoreMappings()];
                final StatementMappingIndex sei = new StatementMappingIndex(m);
                sei.addParameterOccurrence(parametersIndex);
                if (this.whereClauseConsumption) {
                    VersionMetaData vermd = this.cmd.getVersionMetaDataForTable();
                    if (!UpdateRequest.this.table.managesClass(this.cmd.getFullClassName())) {
                        vermd = this.cmd.getBaseAbstractClassMetaData().getVersionMetaDataForClass();
                    }
                    if (vermd != null && vermd.getFieldName() != null && fmd.getName().equals(vermd.getFieldName())) {
                        UpdateRequest.this.stmtMappingDefinition.setWhereVersion(sei);
                        parametersIndex[0] = this.paramIndex++;
                        if (this.where.length() > 0) {
                            this.where.append(" AND ");
                        }
                        this.where.append(m.getDatastoreMapping(0).getColumn().getIdentifier());
                        this.where.append("=");
                        this.where.append(((AbstractDatastoreMapping)m.getDatastoreMapping(0)).getUpdateInputParameter());
                    }
                    else {
                        UpdateRequest.this.stmtMappingDefinition.getWhereFields()[fmd.getAbsoluteFieldNumber()] = sei;
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
                else {
                    UpdateRequest.this.stmtMappingDefinition.getUpdateFields()[fmd.getAbsoluteFieldNumber()] = sei;
                    for (int k = 0; k < parametersIndex.length; ++k) {
                        final Column c = m.getDatastoreMapping(k).getColumn();
                        final DatastoreIdentifier columnId = c.getIdentifier();
                        final boolean columnExists = this.assignedColumns.containsKey(columnId.toString());
                        if (columnExists) {
                            parametersIndex[k] = this.assignedColumns.get(columnId.toString());
                        }
                        final String param = ((AbstractDatastoreMapping)m.getDatastoreMapping(k)).getUpdateInputParameter();
                        if (!columnExists) {
                            if (this.columnAssignments.length() > 0) {
                                this.columnAssignments.append(",");
                            }
                            this.columnAssignments.append(columnId).append("=").append(param);
                        }
                        if (param.indexOf("?") > -1) {
                            if (!this.updateFields.contains(abs_field_num)) {
                                this.updateFields.add(abs_field_num);
                            }
                            parametersIndex[k] = this.paramIndex++;
                        }
                        if (!columnExists) {
                            this.assignedColumns.put(columnId.toString(), fmd.getAbsoluteFieldNumber());
                        }
                    }
                }
            }
            if (m instanceof MappingCallbacks) {
                this.mc.add(m);
            }
        }
        
        @Override
        public void consumeMapping(final JavaTypeMapping m, final int mappingType) {
            if (mappingType == 1) {
                final String inputParam = ((AbstractDatastoreMapping)m.getDatastoreMapping(0)).getUpdateInputParameter();
                if (this.whereClauseConsumption) {
                    if (this.where.length() > 0) {
                        this.where.append(" AND ");
                    }
                    this.where.append(m.getDatastoreMapping(0).getColumn().getIdentifier());
                    this.where.append("=");
                    this.where.append(inputParam);
                    final StatementMappingIndex versStmtIdx = new StatementMappingIndex(m);
                    versStmtIdx.addParameterOccurrence(new int[] { this.paramIndex++ });
                    UpdateRequest.this.stmtMappingDefinition.setWhereVersion(versStmtIdx);
                }
                else {
                    final String condition = m.getDatastoreMapping(0).getColumn().getIdentifier() + "=" + inputParam;
                    if (this.columnAssignments.length() > 0) {
                        this.columnAssignments.append(", ");
                    }
                    this.columnAssignments.append(condition);
                    final StatementMappingIndex versStmtIdx2 = new StatementMappingIndex(m);
                    versStmtIdx2.addParameterOccurrence(new int[] { this.paramIndex++ });
                    UpdateRequest.this.stmtMappingDefinition.setUpdateVersion(versStmtIdx2);
                }
            }
            else if (mappingType == 2) {
                if (this.where.length() > 0) {
                    this.where.append(" AND ");
                }
                this.where.append(UpdateRequest.this.key.getColumns().get(0).getIdentifier());
                this.where.append("=");
                this.where.append(((AbstractDatastoreMapping)m.getDatastoreMapping(0)).getUpdateInputParameter());
                final StatementMappingIndex datastoreIdIdx = new StatementMappingIndex(m);
                datastoreIdIdx.addParameterOccurrence(new int[] { this.paramIndex++ });
                UpdateRequest.this.stmtMappingDefinition.setWhereDatastoreId(datastoreIdIdx);
            }
        }
        
        @Override
        public void consumeUnmappedColumn(final Column col) {
        }
        
        public List getMappingCallbacks() {
            return this.mc;
        }
        
        public int[] getUpdateFieldNumbers() {
            final int[] fieldNumbers = new int[this.updateFields.size()];
            for (int i = 0; i < this.updateFields.size(); ++i) {
                fieldNumbers[i] = this.updateFields.get(i);
            }
            return fieldNumbers;
        }
        
        public int[] getWhereFieldNumbers() {
            final int[] fieldNumbers = new int[this.whereFields.size()];
            for (int i = 0; i < this.whereFields.size(); ++i) {
                fieldNumbers[i] = this.whereFields.get(i);
            }
            return fieldNumbers;
        }
        
        public String getStatement() {
            if (this.columnAssignments.length() < 1) {
                return null;
            }
            return "UPDATE " + UpdateRequest.this.table.toString() + " SET " + (Object)this.columnAssignments + " WHERE " + (Object)this.where;
        }
    }
}
