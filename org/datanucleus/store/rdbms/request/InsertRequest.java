// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.request;

import java.util.Iterator;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import org.datanucleus.store.rdbms.table.SecondaryTable;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.store.rdbms.table.Column;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.table.Table;
import java.util.List;
import org.datanucleus.metadata.DiscriminatorMetaData;
import java.sql.PreparedStatement;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.store.exceptions.NotYetFlushedException;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.util.ArrayList;
import org.datanucleus.util.StringUtils;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.state.ActivityState;
import org.datanucleus.store.VersionHelper;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.store.rdbms.mapping.MappingConsumer;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.store.rdbms.mapping.MappingCallbacks;

public class InsertRequest extends Request
{
    private static final int IDPARAMNUMBER = 1;
    private final MappingCallbacks[] callbacks;
    private final int[] insertFieldNumbers;
    private final int[] pkFieldNumbers;
    private final int[] reachableFieldNumbers;
    private final int[] relationFieldNumbers;
    private final String insertStmt;
    private boolean hasIdentityColumn;
    private StatementMappingIndex[] stmtMappings;
    private StatementMappingIndex[] retrievedStmtMappings;
    private StatementMappingIndex versionStmtMapping;
    private StatementMappingIndex discriminatorStmtMapping;
    private StatementMappingIndex multitenancyStmtMapping;
    private StatementMappingIndex[] externalFKStmtMappings;
    private StatementMappingIndex[] externalFKDiscrimStmtMappings;
    private StatementMappingIndex[] externalOrderStmtMappings;
    private boolean batch;
    
    public InsertRequest(final DatastoreClass table, final AbstractClassMetaData cmd, final ClassLoaderResolver clr) {
        super(table);
        this.hasIdentityColumn = false;
        this.batch = false;
        final InsertMappingConsumer consumer = new InsertMappingConsumer(clr, cmd, 1);
        table.provideDatastoreIdMappings(consumer);
        table.provideNonPrimaryKeyMappings(consumer);
        table.providePrimaryKeyMappings(consumer);
        table.provideVersionMappings(consumer);
        table.provideDiscriminatorMappings(consumer);
        table.provideMultitenancyMapping(consumer);
        table.provideExternalMappings(consumer, 5);
        table.provideExternalMappings(consumer, 6);
        table.provideExternalMappings(consumer, 4);
        table.provideUnmappedColumns(consumer);
        this.callbacks = consumer.getMappingCallbacks().toArray(new MappingCallbacks[consumer.getMappingCallbacks().size()]);
        this.stmtMappings = consumer.getStatementMappings();
        this.versionStmtMapping = consumer.getVersionStatementMapping();
        this.discriminatorStmtMapping = consumer.getDiscriminatorStatementMapping();
        this.multitenancyStmtMapping = consumer.getMultitenancyStatementMapping();
        this.externalFKStmtMappings = consumer.getExternalFKStatementMapping();
        this.externalFKDiscrimStmtMappings = consumer.getExternalFKDiscrimStatementMapping();
        this.externalOrderStmtMappings = consumer.getExternalOrderStatementMapping();
        this.pkFieldNumbers = consumer.getPrimaryKeyFieldNumbers();
        if (table.getIdentityType() == IdentityType.APPLICATION && this.pkFieldNumbers.length < 1 && !this.hasIdentityColumn) {
            throw new NucleusException(InsertRequest.LOCALISER.msg("052200", cmd.getFullClassName())).setFatal();
        }
        this.insertFieldNumbers = consumer.getInsertFieldNumbers();
        this.retrievedStmtMappings = consumer.getReachableStatementMappings();
        this.reachableFieldNumbers = consumer.getReachableFieldNumbers();
        this.relationFieldNumbers = consumer.getRelationFieldNumbers();
        this.insertStmt = consumer.getInsertStmt();
        if (!this.hasIdentityColumn && !cmd.hasRelations(clr, table.getStoreManager().getMetaDataManager()) && this.externalFKStmtMappings == null) {
            this.batch = true;
        }
    }
    
    @Override
    public void execute(final ObjectProvider op) {
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(InsertRequest.LOCALISER.msg("052207", op.getObjectAsPrintable(), this.table));
        }
        try {
            final VersionMetaData vermd = this.table.getVersionMetaData();
            final ExecutionContext ec = op.getExecutionContext();
            final RDBMSStoreManager storeMgr = this.table.getStoreManager();
            if (vermd != null && vermd.getFieldName() != null) {
                final AbstractMemberMetaData verfmd = ((AbstractClassMetaData)vermd.getParent()).getMetaDataForMember(vermd.getFieldName());
                Object currentVersion = op.getVersion();
                if (currentVersion instanceof Number) {
                    currentVersion = ((Number)currentVersion).longValue();
                }
                Object nextOptimisticVersion = VersionHelper.getNextVersion(this.table.getVersionMetaData().getVersionStrategy(), currentVersion);
                if (verfmd.getType() == Integer.class || verfmd.getType() == Integer.TYPE) {
                    nextOptimisticVersion = ((Long)nextOptimisticVersion).intValue();
                }
                op.replaceField(verfmd.getAbsoluteFieldNumber(), nextOptimisticVersion);
            }
            op.changeActivityState(ActivityState.INSERTING);
            final SQLController sqlControl = storeMgr.getSQLController();
            final ManagedConnection mconn = storeMgr.getConnection(ec);
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, this.insertStmt, this.batch, this.hasIdentityColumn && storeMgr.getDatastoreAdapter().supportsOption("GetGeneratedKeysStatement"));
                try {
                    StatementClassMapping mappingDefinition = new StatementClassMapping();
                    StatementMappingIndex[] idxs = this.stmtMappings;
                    for (int i = 0; i < idxs.length; ++i) {
                        if (idxs[i] != null) {
                            mappingDefinition.addMappingForMember(i, idxs[i]);
                        }
                    }
                    if (this.table.getIdentityType() == IdentityType.DATASTORE) {
                        if (!this.table.isObjectIdDatastoreAttributed() || !this.table.isBaseDatastoreClass()) {
                            final int[] paramNumber = { 1 };
                            this.table.getDatastoreObjectIdMapping().setObject(ec, ps, paramNumber, op.getInternalObjectId());
                        }
                    }
                    else if (this.table.getIdentityType() == IdentityType.APPLICATION) {
                        op.provideFields(this.pkFieldNumbers, storeMgr.getFieldManagerForStatementGeneration(op, ps, mappingDefinition));
                    }
                    if (this.insertFieldNumbers.length > 0) {
                        int numberOfFieldsToProvide = 0;
                        for (int j = 0; j < this.insertFieldNumbers.length; ++j) {
                            if (this.insertFieldNumbers[j] < op.getClassMetaData().getMemberCount()) {
                                ++numberOfFieldsToProvide;
                            }
                        }
                        int k = 0;
                        final int[] fieldNums = new int[numberOfFieldsToProvide];
                        for (int l = 0; l < this.insertFieldNumbers.length; ++l) {
                            if (this.insertFieldNumbers[l] < op.getClassMetaData().getMemberCount()) {
                                fieldNums[k++] = this.insertFieldNumbers[l];
                            }
                        }
                        op.provideFields(fieldNums, storeMgr.getFieldManagerForStatementGeneration(op, ps, mappingDefinition));
                    }
                    if (this.table.getVersionMapping(false) != null) {
                        final Object currentVersion2 = op.getVersion();
                        final Object nextOptimisticVersion2 = VersionHelper.getNextVersion(this.table.getVersionMetaData().getVersionStrategy(), currentVersion2);
                        for (int m = 0; m < this.versionStmtMapping.getNumberOfParameterOccurrences(); ++m) {
                            this.table.getVersionMapping(false).setObject(ec, ps, this.versionStmtMapping.getParameterPositionsForOccurrence(m), nextOptimisticVersion2);
                        }
                        op.setTransactionalVersion(nextOptimisticVersion2);
                    }
                    else if (vermd != null && vermd.getFieldName() != null) {
                        final Object currentVersion2 = op.getVersion();
                        final Object nextOptimisticVersion2 = VersionHelper.getNextVersion(this.table.getVersionMetaData().getVersionStrategy(), currentVersion2);
                        op.setTransactionalVersion(nextOptimisticVersion2);
                    }
                    if (this.multitenancyStmtMapping != null) {
                        this.table.getMultitenancyMapping().setObject(ec, ps, this.multitenancyStmtMapping.getParameterPositionsForOccurrence(0), storeMgr.getStringProperty("datanucleus.TenantID"));
                    }
                    if (this.table.getDiscriminatorMapping(false) != null) {
                        DiscriminatorMetaData dismd = this.table.getDiscriminatorMetaData();
                        if (dismd.getStrategy() == DiscriminatorStrategy.CLASS_NAME) {
                            for (int k2 = 0; k2 < this.discriminatorStmtMapping.getNumberOfParameterOccurrences(); ++k2) {
                                this.table.getDiscriminatorMapping(false).setObject(ec, ps, this.discriminatorStmtMapping.getParameterPositionsForOccurrence(k2), op.getObject().getClass().getName());
                            }
                        }
                        else if (dismd.getStrategy() == DiscriminatorStrategy.VALUE_MAP) {
                            dismd = op.getClassMetaData().getInheritanceMetaData().getDiscriminatorMetaData();
                            for (int k2 = 0; k2 < this.discriminatorStmtMapping.getNumberOfParameterOccurrences(); ++k2) {
                                this.table.getDiscriminatorMapping(false).setObject(ec, ps, this.discriminatorStmtMapping.getParameterPositionsForOccurrence(k2), dismd.getValue());
                            }
                        }
                    }
                    if (this.externalFKStmtMappings != null) {
                        for (int i = 0; i < this.externalFKStmtMappings.length; ++i) {
                            final Object fkValue = op.getAssociatedValue(this.externalFKStmtMappings[i].getMapping());
                            if (fkValue != null) {
                                final AbstractMemberMetaData ownerFmd = this.table.getMetaDataForExternalMapping(this.externalFKStmtMappings[i].getMapping(), 5);
                                for (int k3 = 0; k3 < this.externalFKStmtMappings[i].getNumberOfParameterOccurrences(); ++k3) {
                                    this.externalFKStmtMappings[i].getMapping().setObject(ec, ps, this.externalFKStmtMappings[i].getParameterPositionsForOccurrence(k3), fkValue, null, ownerFmd.getAbsoluteFieldNumber());
                                }
                            }
                            else {
                                for (int m = 0; m < this.externalFKStmtMappings[i].getNumberOfParameterOccurrences(); ++m) {
                                    this.externalFKStmtMappings[i].getMapping().setObject(ec, ps, this.externalFKStmtMappings[i].getParameterPositionsForOccurrence(m), null);
                                }
                            }
                        }
                    }
                    if (this.externalFKDiscrimStmtMappings != null) {
                        for (int i = 0; i < this.externalFKDiscrimStmtMappings.length; ++i) {
                            final Object discrimValue = op.getAssociatedValue(this.externalFKDiscrimStmtMappings[i].getMapping());
                            for (int m = 0; m < this.externalFKDiscrimStmtMappings[i].getNumberOfParameterOccurrences(); ++m) {
                                this.externalFKDiscrimStmtMappings[i].getMapping().setObject(ec, ps, this.externalFKDiscrimStmtMappings[i].getParameterPositionsForOccurrence(m), discrimValue);
                            }
                        }
                    }
                    if (this.externalOrderStmtMappings != null) {
                        for (int i = 0; i < this.externalOrderStmtMappings.length; ++i) {
                            Object orderValue = op.getAssociatedValue(this.externalOrderStmtMappings[i].getMapping());
                            if (orderValue == null) {
                                orderValue = -1;
                            }
                            for (int m = 0; m < this.externalOrderStmtMappings[i].getNumberOfParameterOccurrences(); ++m) {
                                this.externalOrderStmtMappings[i].getMapping().setObject(ec, ps, this.externalOrderStmtMappings[i].getParameterPositionsForOccurrence(m), orderValue);
                            }
                        }
                    }
                    sqlControl.executeStatementUpdate(ec, mconn, this.insertStmt, ps, !this.batch);
                    if (this.hasIdentityColumn) {
                        final Object newId = this.getInsertedDatastoreIdentity(ec, sqlControl, op, mconn, ps);
                        if (NucleusLogger.DATASTORE_PERSIST.isDebugEnabled()) {
                            NucleusLogger.DATASTORE_PERSIST.debug(InsertRequest.LOCALISER.msg("052206", op.getObjectAsPrintable(), newId));
                        }
                        op.setPostStoreNewObjectId(newId);
                    }
                    for (int i = 0; i < this.callbacks.length; ++i) {
                        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                            NucleusLogger.PERSISTENCE.debug(InsertRequest.LOCALISER.msg("052222", op.getObjectAsPrintable(), ((JavaTypeMapping)this.callbacks[i]).getMemberMetaData().getFullFieldName()));
                        }
                        this.callbacks[i].insertPostProcessing(op);
                    }
                    storeMgr.setObjectIsInsertedToLevel(op, this.table);
                    for (int i = 0; i < this.relationFieldNumbers.length; ++i) {
                        final Object value = op.provideField(this.relationFieldNumbers[i]);
                        if (value != null && ec.getApiAdapter().isDetached(value)) {
                            final Object valueAttached = ec.persistObjectInternal(value, null, -1, 0);
                            op.replaceField(this.relationFieldNumbers[i], valueAttached);
                        }
                    }
                    if (this.reachableFieldNumbers.length > 0) {
                        int numberOfReachableFields = 0;
                        for (int j = 0; j < this.reachableFieldNumbers.length; ++j) {
                            if (this.reachableFieldNumbers[j] < op.getClassMetaData().getMemberCount()) {
                                ++numberOfReachableFields;
                            }
                        }
                        final int[] fieldNums2 = new int[numberOfReachableFields];
                        int j2 = 0;
                        for (int l = 0; l < this.reachableFieldNumbers.length; ++l) {
                            if (this.reachableFieldNumbers[l] < op.getClassMetaData().getMemberCount()) {
                                fieldNums2[j2++] = this.reachableFieldNumbers[l];
                            }
                        }
                        mappingDefinition = new StatementClassMapping();
                        idxs = this.retrievedStmtMappings;
                        for (int l = 0; l < idxs.length; ++l) {
                            if (idxs[l] != null) {
                                mappingDefinition.addMappingForMember(l, idxs[l]);
                            }
                        }
                        NucleusLogger.PERSISTENCE.debug("Performing reachability on fields " + StringUtils.intArrayToString(fieldNums2));
                        op.provideFields(fieldNums2, storeMgr.getFieldManagerForStatementGeneration(op, ps, mappingDefinition));
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
            final String msg = InsertRequest.LOCALISER.msg("052208", op.getObjectAsPrintable(), this.insertStmt, e.getMessage());
            NucleusLogger.DATASTORE_PERSIST.warn(msg);
            final List exceptions = new ArrayList();
            exceptions.add(e);
            while ((e = e.getNextException()) != null) {
                exceptions.add(e);
            }
            throw new NucleusDataStoreException(msg, exceptions.toArray(new Throwable[exceptions.size()]));
        }
        for (int i2 = 0; i2 < this.callbacks.length; ++i2) {
            try {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(InsertRequest.LOCALISER.msg("052209", op.getObjectAsPrintable(), ((JavaTypeMapping)this.callbacks[i2]).getMemberMetaData().getFullFieldName()));
                }
                this.callbacks[i2].postInsert(op);
            }
            catch (NotYetFlushedException e2) {
                op.updateFieldAfterInsert(e2.getPersistable(), ((JavaTypeMapping)this.callbacks[i2]).getMemberMetaData().getAbsoluteFieldNumber());
            }
        }
    }
    
    private Object getInsertedDatastoreIdentity(final ExecutionContext ec, final SQLController sqlControl, final ObjectProvider op, final ManagedConnection mconn, final PreparedStatement ps) throws SQLException {
        Object datastoreId = null;
        final RDBMSStoreManager storeMgr = this.table.getStoreManager();
        if (storeMgr.getDatastoreAdapter().supportsOption("GetGeneratedKeysStatement")) {
            ResultSet rs = null;
            try {
                rs = ps.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    datastoreId = rs.getObject(1);
                }
            }
            catch (Throwable e) {}
            finally {
                if (rs != null) {
                    rs.close();
                }
            }
        }
        if (datastoreId == null) {
            String columnName = null;
            final JavaTypeMapping idMapping = this.table.getIdMapping();
            if (idMapping != null) {
                for (int i = 0; i < idMapping.getNumberOfDatastoreMappings(); ++i) {
                    final Column col = idMapping.getDatastoreMapping(i).getColumn();
                    if (col.isIdentity()) {
                        columnName = col.getIdentifier().toString();
                        break;
                    }
                }
            }
            final String autoIncStmt = storeMgr.getDatastoreAdapter().getAutoIncrementStmt(this.table, columnName);
            final PreparedStatement psAutoIncrement = sqlControl.getStatementForQuery(mconn, autoIncStmt);
            ResultSet rs2 = null;
            try {
                rs2 = sqlControl.executeStatementQuery(ec, mconn, autoIncStmt, psAutoIncrement);
                if (rs2.next()) {
                    datastoreId = rs2.getObject(1);
                }
            }
            finally {
                if (rs2 != null) {
                    rs2.close();
                }
                if (psAutoIncrement != null) {
                    psAutoIncrement.close();
                }
            }
        }
        if (datastoreId == null) {
            throw new NucleusDataStoreException(InsertRequest.LOCALISER.msg("052205", this.table));
        }
        return datastoreId;
    }
    
    private class InsertMappingConsumer implements MappingConsumer
    {
        List insertFields;
        List pkFields;
        List reachableFields;
        List relationFields;
        StringBuffer columnNames;
        StringBuffer columnValues;
        Map assignedColumns;
        List mc;
        boolean initialized;
        int paramIndex;
        private StatementMappingIndex[] statementMappings;
        private StatementMappingIndex[] retrievedStatementMappings;
        private StatementMappingIndex versionStatementMapping;
        private StatementMappingIndex discriminatorStatementMapping;
        private StatementMappingIndex multitenancyStatementMapping;
        private StatementMappingIndex[] externalFKStmtExprIndex;
        private StatementMappingIndex[] externalFKDiscrimStmtExprIndex;
        private StatementMappingIndex[] externalOrderStmtExprIndex;
        private final ClassLoaderResolver clr;
        private final AbstractClassMetaData cmd;
        
        public InsertMappingConsumer(final ClassLoaderResolver clr, final AbstractClassMetaData cmd, final int initialParamIndex) {
            this.insertFields = new ArrayList();
            this.pkFields = new ArrayList();
            this.reachableFields = new ArrayList();
            this.relationFields = new ArrayList();
            this.columnNames = new StringBuffer();
            this.columnValues = new StringBuffer();
            this.assignedColumns = new HashMap();
            this.mc = new ArrayList();
            this.initialized = false;
            this.clr = clr;
            this.cmd = cmd;
            this.paramIndex = initialParamIndex;
        }
        
        @Override
        public void preConsumeMapping(final int highestFieldNumber) {
            if (!this.initialized) {
                this.statementMappings = new StatementMappingIndex[highestFieldNumber];
                this.retrievedStatementMappings = new StatementMappingIndex[highestFieldNumber];
                this.initialized = true;
            }
        }
        
        @Override
        public void consumeMapping(final JavaTypeMapping m, final AbstractMemberMetaData mmd) {
            if (!mmd.getAbstractClassMetaData().isSameOrAncestorOf(this.cmd)) {
                return;
            }
            if (m.includeInInsertStatement()) {
                if (m.getNumberOfDatastoreMappings() == 0 && (m instanceof PersistableMapping || m instanceof ReferenceMapping)) {
                    this.retrievedStatementMappings[mmd.getAbsoluteFieldNumber()] = new StatementMappingIndex(m);
                    final RelationType relationType = mmd.getRelationType(this.clr);
                    if (relationType == RelationType.ONE_TO_ONE_BI) {
                        if (mmd.getMappedBy() != null) {
                            this.reachableFields.add(mmd.getAbsoluteFieldNumber());
                        }
                    }
                    else if (relationType == RelationType.MANY_TO_ONE_BI) {
                        final AbstractMemberMetaData[] relatedMmds = mmd.getRelatedMemberMetaData(this.clr);
                        if (mmd.getJoinMetaData() != null || relatedMmds[0].getJoinMetaData() != null) {
                            this.reachableFields.add(mmd.getAbsoluteFieldNumber());
                        }
                    }
                }
                else {
                    if (mmd.hasExtension("insertable") && mmd.getValueForExtension("insertable").equalsIgnoreCase("false")) {
                        return;
                    }
                    final ColumnMetaData[] colmds = mmd.getColumnMetaData();
                    if (colmds != null && colmds.length > 0) {
                        for (int i = 0; i < colmds.length; ++i) {
                            if (!colmds[i].getInsertable()) {
                                return;
                            }
                        }
                    }
                    final RelationType relationType2 = mmd.getRelationType(this.clr);
                    if (relationType2 == RelationType.ONE_TO_ONE_BI) {
                        if (mmd.getMappedBy() == null) {}
                    }
                    else if (relationType2 == RelationType.MANY_TO_ONE_BI) {
                        final AbstractMemberMetaData[] relatedMmds2 = mmd.getRelatedMemberMetaData(this.clr);
                        if (mmd.getJoinMetaData() == null && relatedMmds2[0].getJoinMetaData() == null) {
                            this.relationFields.add(mmd.getAbsoluteFieldNumber());
                        }
                    }
                    this.statementMappings[mmd.getAbsoluteFieldNumber()] = new StatementMappingIndex(m);
                    final int[] parametersIndex = new int[m.getNumberOfDatastoreMappings()];
                    for (int j = 0; j < parametersIndex.length; ++j) {
                        final Column c = m.getDatastoreMapping(j).getColumn();
                        final DatastoreIdentifier columnId = c.getIdentifier();
                        final boolean columnExists = this.assignedColumns.containsKey(columnId.toString());
                        if (columnExists) {
                            parametersIndex[j] = this.assignedColumns.get(c.getIdentifier().toString());
                        }
                        if (InsertRequest.this.table instanceof SecondaryTable || !InsertRequest.this.table.isBaseDatastoreClass() || (!InsertRequest.this.table.getStoreManager().isStrategyDatastoreAttributed(this.cmd, mmd.getAbsoluteFieldNumber()) && !c.isIdentity())) {
                            if (!columnExists) {
                                if (this.columnNames.length() > 0) {
                                    this.columnNames.append(',');
                                    this.columnValues.append(',');
                                }
                                this.columnNames.append(columnId);
                                this.columnValues.append(((AbstractDatastoreMapping)m.getDatastoreMapping(j)).getInsertionInputParameter());
                            }
                            if (((AbstractDatastoreMapping)m.getDatastoreMapping(j)).insertValuesOnInsert()) {
                                final Integer abs_field_num = mmd.getAbsoluteFieldNumber();
                                if (mmd.isPrimaryKey()) {
                                    if (!this.pkFields.contains(abs_field_num)) {
                                        this.pkFields.add(abs_field_num);
                                    }
                                }
                                else if (!this.insertFields.contains(abs_field_num)) {
                                    this.insertFields.add(abs_field_num);
                                }
                                if (columnExists) {
                                    parametersIndex[j] = this.assignedColumns.get(c.getIdentifier().toString());
                                }
                                else {
                                    parametersIndex[j] = this.paramIndex++;
                                }
                            }
                            if (!columnExists) {
                                this.assignedColumns.put(c.getIdentifier().toString(), mmd.getAbsoluteFieldNumber());
                            }
                        }
                        else {
                            InsertRequest.this.hasIdentityColumn = true;
                        }
                    }
                    this.statementMappings[mmd.getAbsoluteFieldNumber()].addParameterOccurrence(parametersIndex);
                }
            }
            if (m instanceof MappingCallbacks) {
                this.mc.add(m);
            }
        }
        
        @Override
        public void consumeMapping(final JavaTypeMapping m, final int mappingType) {
            if (mappingType == 1) {
                if (InsertRequest.this.table.getVersionMapping(false) != null) {
                    final String val = ((AbstractDatastoreMapping)InsertRequest.this.table.getVersionMapping(false).getDatastoreMapping(0)).getUpdateInputParameter();
                    if (this.columnNames.length() > 0) {
                        this.columnNames.append(',');
                        this.columnValues.append(',');
                    }
                    this.columnNames.append(InsertRequest.this.table.getVersionMapping(false).getDatastoreMapping(0).getColumn().getIdentifier());
                    this.columnValues.append(val);
                    this.versionStatementMapping = new StatementMappingIndex(InsertRequest.this.table.getVersionMapping(false));
                    final int[] param = { this.paramIndex++ };
                    this.versionStatementMapping.addParameterOccurrence(param);
                }
                else {
                    this.versionStatementMapping = null;
                }
            }
            else if (mappingType == 3) {
                if (InsertRequest.this.table.getDiscriminatorMapping(false) != null) {
                    final String val = ((AbstractDatastoreMapping)InsertRequest.this.table.getDiscriminatorMapping(false).getDatastoreMapping(0)).getUpdateInputParameter();
                    if (this.columnNames.length() > 0) {
                        this.columnNames.append(',');
                        this.columnValues.append(',');
                    }
                    this.columnNames.append(InsertRequest.this.table.getDiscriminatorMapping(false).getDatastoreMapping(0).getColumn().getIdentifier());
                    this.columnValues.append(val);
                    this.discriminatorStatementMapping = new StatementMappingIndex(InsertRequest.this.table.getDiscriminatorMapping(false));
                    final int[] param = { this.paramIndex++ };
                    this.discriminatorStatementMapping.addParameterOccurrence(param);
                }
                else {
                    this.discriminatorStatementMapping = null;
                }
            }
            else if (mappingType == 2) {
                if (InsertRequest.this.table.getIdentityType() == IdentityType.DATASTORE) {
                    if (!InsertRequest.this.table.isObjectIdDatastoreAttributed() || !InsertRequest.this.table.isBaseDatastoreClass()) {
                        final Iterator iterator = InsertRequest.this.key.getColumns().iterator();
                        if (this.columnNames.length() > 0) {
                            this.columnNames.append(',');
                            this.columnValues.append(',');
                        }
                        this.columnNames.append(iterator.next().getIdentifier().toString());
                        this.columnValues.append("?");
                        ++this.paramIndex;
                    }
                    else {
                        InsertRequest.this.hasIdentityColumn = true;
                    }
                }
            }
            else if (mappingType == 5) {
                this.externalFKStmtExprIndex = this.processExternalMapping(m, this.statementMappings, this.externalFKStmtExprIndex);
            }
            else if (mappingType == 6) {
                this.externalFKDiscrimStmtExprIndex = this.processExternalMapping(m, this.statementMappings, this.externalFKDiscrimStmtExprIndex);
            }
            else if (mappingType == 4) {
                this.externalOrderStmtExprIndex = this.processExternalMapping(m, this.statementMappings, this.externalOrderStmtExprIndex);
            }
            else if (mappingType == 7) {
                final JavaTypeMapping tenantMapping = InsertRequest.this.table.getMultitenancyMapping();
                final String val2 = ((AbstractDatastoreMapping)tenantMapping.getDatastoreMapping(0)).getUpdateInputParameter();
                if (this.columnNames.length() > 0) {
                    this.columnNames.append(',');
                    this.columnValues.append(',');
                }
                this.columnNames.append(tenantMapping.getDatastoreMapping(0).getColumn().getIdentifier());
                this.columnValues.append(val2);
                this.multitenancyStatementMapping = new StatementMappingIndex(tenantMapping);
                final int[] param2 = { this.paramIndex++ };
                this.multitenancyStatementMapping.addParameterOccurrence(param2);
            }
        }
        
        @Override
        public void consumeUnmappedColumn(final Column col) {
            if (this.columnNames.length() > 0) {
                this.columnNames.append(',');
                this.columnValues.append(',');
            }
            this.columnNames.append(col.getIdentifier());
            final ColumnMetaData colmd = col.getColumnMetaData();
            String value = colmd.getInsertValue();
            if (value != null && value.equalsIgnoreCase("#NULL")) {
                value = null;
            }
            if (colmd.getJdbcType().equals("VARCHAR") || colmd.getJdbcType().equals("CHAR")) {
                if (value != null) {
                    value = "'" + value + "'";
                }
                else if (!col.isNullable()) {
                    value = "''";
                }
            }
            this.columnValues.append(value);
        }
        
        private StatementMappingIndex[] processExternalMapping(final JavaTypeMapping mapping, final StatementMappingIndex[] fieldStmtExprIndex, StatementMappingIndex[] stmtExprIndex) {
            for (int i = 0; i < fieldStmtExprIndex.length; ++i) {
                if (fieldStmtExprIndex[i] != null && fieldStmtExprIndex[i].getMapping() == mapping) {
                    return stmtExprIndex;
                }
            }
            int pos = 0;
            if (stmtExprIndex == null) {
                stmtExprIndex = new StatementMappingIndex[] { null };
                pos = 0;
            }
            else {
                for (int j = 0; j < stmtExprIndex.length; ++j) {
                    if (stmtExprIndex[j].getMapping() == mapping) {
                        return stmtExprIndex;
                    }
                }
                final StatementMappingIndex[] tmpStmtExprIndex = stmtExprIndex;
                stmtExprIndex = new StatementMappingIndex[tmpStmtExprIndex.length + 1];
                for (int k = 0; k < tmpStmtExprIndex.length; ++k) {
                    stmtExprIndex[k] = tmpStmtExprIndex[k];
                }
                pos = tmpStmtExprIndex.length;
            }
            stmtExprIndex[pos] = new StatementMappingIndex(mapping);
            final int[] param = new int[mapping.getNumberOfDatastoreMappings()];
            for (int k = 0; k < mapping.getNumberOfDatastoreMappings(); ++k) {
                if (this.columnNames.length() > 0) {
                    this.columnNames.append(',');
                    this.columnValues.append(',');
                }
                this.columnNames.append(mapping.getDatastoreMapping(k).getColumn().getIdentifier());
                this.columnValues.append(((AbstractDatastoreMapping)mapping.getDatastoreMapping(k)).getUpdateInputParameter());
                param[k] = this.paramIndex++;
            }
            stmtExprIndex[pos].addParameterOccurrence(param);
            return stmtExprIndex;
        }
        
        public List getMappingCallbacks() {
            return this.mc;
        }
        
        public int[] getInsertFieldNumbers() {
            final int[] fieldNumbers = new int[this.insertFields.size()];
            for (int i = 0; i < this.insertFields.size(); ++i) {
                fieldNumbers[i] = this.insertFields.get(i);
            }
            return fieldNumbers;
        }
        
        public int[] getPrimaryKeyFieldNumbers() {
            final int[] fieldNumbers = new int[this.pkFields.size()];
            for (int i = 0; i < this.pkFields.size(); ++i) {
                fieldNumbers[i] = this.pkFields.get(i);
            }
            return fieldNumbers;
        }
        
        public int[] getReachableFieldNumbers() {
            final int[] fieldNumbers = new int[this.reachableFields.size()];
            for (int i = 0; i < this.reachableFields.size(); ++i) {
                fieldNumbers[i] = this.reachableFields.get(i);
            }
            return fieldNumbers;
        }
        
        public int[] getRelationFieldNumbers() {
            final int[] fieldNumbers = new int[this.relationFields.size()];
            for (int i = 0; i < this.relationFields.size(); ++i) {
                fieldNumbers[i] = this.relationFields.get(i);
            }
            return fieldNumbers;
        }
        
        public StatementMappingIndex[] getStatementMappings() {
            return this.statementMappings;
        }
        
        public StatementMappingIndex[] getReachableStatementMappings() {
            return this.retrievedStatementMappings;
        }
        
        public StatementMappingIndex getVersionStatementMapping() {
            return this.versionStatementMapping;
        }
        
        public StatementMappingIndex getDiscriminatorStatementMapping() {
            return this.discriminatorStatementMapping;
        }
        
        public StatementMappingIndex getMultitenancyStatementMapping() {
            return this.multitenancyStatementMapping;
        }
        
        public StatementMappingIndex[] getExternalFKStatementMapping() {
            return this.externalFKStmtExprIndex;
        }
        
        public StatementMappingIndex[] getExternalFKDiscrimStatementMapping() {
            return this.externalFKDiscrimStmtExprIndex;
        }
        
        public StatementMappingIndex[] getExternalOrderStatementMapping() {
            return this.externalOrderStmtExprIndex;
        }
        
        public String getInsertStmt() {
            if (this.columnNames.length() > 0 && this.columnValues.length() > 0) {
                return "INSERT INTO " + InsertRequest.this.table.toString() + " (" + (Object)this.columnNames + ") VALUES (" + (Object)this.columnValues + ")";
            }
            return InsertRequest.this.table.getStoreManager().getDatastoreAdapter().getInsertStatementForNoColumns(InsertRequest.this.table);
        }
    }
}
