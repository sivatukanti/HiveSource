// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.GenericDescriptorList;
import java.sql.Timestamp;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.SPSDescriptor;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;

class CreateTriggerConstantAction extends DDLSingleTableConstantAction
{
    private String triggerName;
    private String triggerSchemaName;
    private TableDescriptor triggerTable;
    private UUID triggerTableId;
    private int eventMask;
    private boolean isBefore;
    private boolean isRow;
    private boolean isEnabled;
    private boolean referencingOld;
    private boolean referencingNew;
    private UUID whenSPSId;
    private String whenText;
    private UUID actionSPSId;
    private String actionText;
    private String originalActionText;
    private String oldReferencingName;
    private String newReferencingName;
    private UUID spsCompSchemaId;
    private int[] referencedCols;
    private int[] referencedColsInTriggerAction;
    
    CreateTriggerConstantAction(final String triggerSchemaName, final String triggerName, final int eventMask, final boolean isBefore, final boolean isRow, final boolean isEnabled, final TableDescriptor triggerTable, final UUID whenSPSId, final String whenText, final UUID actionSPSId, final String actionText, final UUID spsCompSchemaId, final int[] referencedCols, final int[] referencedColsInTriggerAction, final String originalActionText, final boolean referencingOld, final boolean referencingNew, final String oldReferencingName, final String newReferencingName) {
        super(triggerTable.getUUID());
        this.triggerName = triggerName;
        this.triggerSchemaName = triggerSchemaName;
        this.triggerTable = triggerTable;
        this.eventMask = eventMask;
        this.isBefore = isBefore;
        this.isRow = isRow;
        this.isEnabled = isEnabled;
        this.whenSPSId = whenSPSId;
        this.whenText = whenText;
        this.actionSPSId = actionSPSId;
        this.actionText = actionText;
        this.spsCompSchemaId = spsCompSchemaId;
        this.referencedCols = referencedCols;
        this.referencedColsInTriggerAction = referencedColsInTriggerAction;
        this.originalActionText = originalActionText;
        this.referencingOld = referencingOld;
        this.referencingNew = referencingNew;
        this.oldReferencingName = oldReferencingName;
        this.newReferencingName = newReferencingName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        SPSDescriptor sps = null;
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dataDictionary.startWriting(languageConnectionContext);
        final SchemaDescriptor schemaDescriptorForCreate = DDLConstantAction.getSchemaDescriptorForCreate(dataDictionary, activation, this.triggerSchemaName);
        if (this.spsCompSchemaId == null) {
            SchemaDescriptor schemaDescriptor = languageConnectionContext.getDefaultSchema();
            if (schemaDescriptor.getUUID() == null) {
                schemaDescriptor = dataDictionary.getSchemaDescriptor(schemaDescriptor.getDescriptorName(), transactionExecute, false);
            }
            if (schemaDescriptor != null) {
                this.spsCompSchemaId = schemaDescriptor.getUUID();
            }
        }
        String s;
        if (this.triggerTable != null) {
            this.triggerTableId = this.triggerTable.getUUID();
            s = this.triggerTable.getName();
        }
        else {
            s = "with UUID " + this.triggerTableId;
        }
        this.triggerTable = dataDictionary.getTableDescriptor(this.triggerTableId);
        if (this.triggerTable == null) {
            throw StandardException.newException("X0X05.S", s);
        }
        this.lockTableForDDL(transactionExecute, this.triggerTable.getHeapConglomerateId(), true);
        this.triggerTable = dataDictionary.getTableDescriptor(this.triggerTableId);
        if (this.triggerTable == null) {
            throw StandardException.newException("X0X05.S", s);
        }
        dependencyManager.invalidateFor(this.triggerTable, 28, languageConnectionContext);
        final UUID uuid = dataDictionary.getUUIDFactory().createUUID();
        this.actionSPSId = ((this.actionSPSId == null) ? dataDictionary.getUUIDFactory().createUUID() : this.actionSPSId);
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        final TriggerDescriptor triggerDescriptor = dataDescriptorGenerator.newTriggerDescriptor(schemaDescriptorForCreate, uuid, this.triggerName, this.eventMask, this.isBefore, this.isRow, this.isEnabled, this.triggerTable, (sps == null) ? null : sps.getUUID(), this.actionSPSId, this.makeCreationTimestamp(dataDictionary), this.referencedCols, this.referencedColsInTriggerAction, this.originalActionText, this.referencingOld, this.referencingNew, this.oldReferencingName, this.newReferencingName);
        dataDictionary.addDescriptor(triggerDescriptor, schemaDescriptorForCreate, 13, false, transactionExecute);
        if (this.whenText != null) {
            sps = this.createSPS(languageConnectionContext, dataDescriptorGenerator, dataDictionary, transactionExecute, uuid, schemaDescriptorForCreate, this.whenSPSId, this.spsCompSchemaId, this.whenText, true, this.triggerTable);
        }
        final SPSDescriptor sps2 = this.createSPS(languageConnectionContext, dataDescriptorGenerator, dataDictionary, transactionExecute, uuid, schemaDescriptorForCreate, this.actionSPSId, this.spsCompSchemaId, this.actionText, false, this.triggerTable);
        if (sps != null) {
            dependencyManager.addDependency(triggerDescriptor, sps, languageConnectionContext.getContextManager());
        }
        dependencyManager.addDependency(triggerDescriptor, sps2, languageConnectionContext.getContextManager());
        dependencyManager.addDependency(triggerDescriptor, this.triggerTable, languageConnectionContext.getContextManager());
        this.storeViewTriggerDependenciesOnPrivileges(activation, triggerDescriptor);
    }
    
    private SPSDescriptor createSPS(final LanguageConnectionContext languageConnectionContext, final DataDescriptorGenerator dataDescriptorGenerator, final DataDictionary dataDictionary, final TransactionController transactionController, final UUID obj, final SchemaDescriptor schemaDescriptor, final UUID uuid, final UUID uuid2, final String s, final boolean b, final TableDescriptor tableDescriptor) throws StandardException {
        if (s == null) {
            return null;
        }
        final SPSDescriptor spsDescriptor = new SPSDescriptor(dataDictionary, "TRIGGER" + (b ? "WHEN_" : "ACTN_") + obj + "_" + tableDescriptor.getUUID().toString(), (uuid == null) ? dataDictionary.getUUIDFactory().createUUID() : uuid, schemaDescriptor.getUUID(), (uuid2 == null) ? languageConnectionContext.getDefaultSchema().getUUID() : uuid2, 'T', true, s, true);
        spsDescriptor.prepareAndRelease(languageConnectionContext, tableDescriptor);
        dataDictionary.addSPSDescriptor(spsDescriptor, transactionController);
        return spsDescriptor;
    }
    
    public String toString() {
        return this.constructToString("CREATE TRIGGER ", this.triggerName);
    }
    
    private Timestamp makeCreationTimestamp(final DataDictionary dataDictionary) throws StandardException {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        final GenericDescriptorList triggerDescriptors = dataDictionary.getTriggerDescriptors(this.triggerTable);
        final int size = triggerDescriptors.size();
        if (size == 0) {
            return timestamp;
        }
        final Timestamp creationTimestamp = triggerDescriptors.get(size - 1).getCreationTimestamp();
        if (timestamp.after(creationTimestamp)) {
            return timestamp;
        }
        timestamp.setTime(creationTimestamp.getTime() + 1L);
        return timestamp;
    }
}
