// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import java.io.ObjectOutput;
import org.apache.derby.iapi.services.context.ContextService;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;
import java.sql.Timestamp;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.depend.Provider;

public class TriggerDescriptor extends TupleDescriptor implements UniqueSQLObjectDescriptor, Provider, Dependent, Formatable
{
    public static final int SYSTRIGGERS_STATE_FIELD = 8;
    public static final int TRIGGER_EVENT_UPDATE = 1;
    public static final int TRIGGER_EVENT_DELETE = 2;
    public static final int TRIGGER_EVENT_INSERT = 4;
    private UUID id;
    private String name;
    private String oldReferencingName;
    private String newReferencingName;
    private String triggerDefinition;
    private SchemaDescriptor sd;
    private int eventMask;
    private boolean isBefore;
    private boolean isRow;
    private boolean referencingOld;
    private boolean referencingNew;
    private TableDescriptor td;
    private UUID actionSPSId;
    private SPSDescriptor actionSPS;
    private UUID whenSPSId;
    private SPSDescriptor whenSPS;
    private boolean isEnabled;
    private int[] referencedCols;
    private int[] referencedColsInTriggerAction;
    private Timestamp creationTimestamp;
    private UUID triggerSchemaId;
    private UUID triggerTableId;
    
    public TriggerDescriptor() {
    }
    
    public TriggerDescriptor(final DataDictionary dataDictionary, final SchemaDescriptor sd, final UUID id, final String name, final int eventMask, final boolean isBefore, final boolean isRow, final boolean isEnabled, final TableDescriptor td, final UUID whenSPSId, final UUID actionSPSId, final Timestamp creationTimestamp, final int[] referencedCols, final int[] referencedColsInTriggerAction, final String triggerDefinition, final boolean referencingOld, final boolean referencingNew, final String oldReferencingName, final String newReferencingName) {
        super(dataDictionary);
        this.id = id;
        this.sd = sd;
        this.name = name;
        this.eventMask = eventMask;
        this.isBefore = isBefore;
        this.isRow = isRow;
        this.td = td;
        this.actionSPSId = actionSPSId;
        this.whenSPSId = whenSPSId;
        this.isEnabled = isEnabled;
        this.referencedCols = referencedCols;
        this.referencedColsInTriggerAction = referencedColsInTriggerAction;
        this.creationTimestamp = creationTimestamp;
        this.triggerDefinition = triggerDefinition;
        this.referencingOld = referencingOld;
        this.referencingNew = referencingNew;
        this.oldReferencingName = oldReferencingName;
        this.newReferencingName = newReferencingName;
        this.triggerSchemaId = sd.getUUID();
        this.triggerTableId = td.getUUID();
    }
    
    public UUID getUUID() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public UUID getTableId() {
        return this.triggerTableId;
    }
    
    public SchemaDescriptor getSchemaDescriptor() throws StandardException {
        if (this.sd == null) {
            this.sd = this.getDataDictionary().getSchemaDescriptor(this.triggerSchemaId, null);
        }
        return this.sd;
    }
    
    public boolean listensForEvent(final int n) {
        return (n & this.eventMask) == n;
    }
    
    public int getTriggerEventMask() {
        return this.eventMask;
    }
    
    public Timestamp getCreationTimestamp() {
        return this.creationTimestamp;
    }
    
    public boolean isBeforeTrigger() {
        return this.isBefore;
    }
    
    public boolean isRowTrigger() {
        return this.isRow;
    }
    
    public UUID getActionId() {
        return this.actionSPSId;
    }
    
    public SPSDescriptor getActionSPS(final LanguageConnectionContext languageConnectionContext) throws StandardException {
        if (this.actionSPS == null) {
            languageConnectionContext.beginNestedTransaction(true);
            this.actionSPS = this.getDataDictionary().getSPSDescriptor(this.actionSPSId);
            languageConnectionContext.commitNestedTransaction();
        }
        final DataDictionary dataDictionary = this.getDataDictionary();
        final boolean b = dataDictionary.checkVersion(210, null) ? (this.referencedColsInTriggerAction != null) : (this.referencingOld || this.referencingNew);
        if ((!this.actionSPS.isValid() || this.actionSPS.getPreparedStatement() == null) && this.isRow && b) {
            final CompilerContext pushCompilerContext = languageConnectionContext.pushCompilerContext(dataDictionary.getSchemaDescriptor(this.actionSPS.getCompSchemaId(), null));
            final Visitable statement = pushCompilerContext.getParser().parseStatement(this.triggerDefinition);
            languageConnectionContext.popCompilerContext(pushCompilerContext);
            this.actionSPS.setText(dataDictionary.getTriggerActionString(statement, this.oldReferencingName, this.newReferencingName, this.triggerDefinition, this.referencedCols, this.referencedColsInTriggerAction, 0, this.td, -1, false));
        }
        return this.actionSPS;
    }
    
    public UUID getWhenClauseId() {
        return this.whenSPSId;
    }
    
    public SPSDescriptor getWhenClauseSPS() throws StandardException {
        if (this.whenSPS == null) {
            this.whenSPS = this.getDataDictionary().getSPSDescriptor(this.whenSPSId);
        }
        return this.whenSPS;
    }
    
    public TableDescriptor getTableDescriptor() throws StandardException {
        if (this.td == null) {
            this.td = this.getDataDictionary().getTableDescriptor(this.triggerTableId);
        }
        return this.td;
    }
    
    public int[] getReferencedCols() {
        return this.referencedCols;
    }
    
    public int[] getReferencedColsInTriggerAction() {
        return this.referencedColsInTriggerAction;
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    public void setEnabled() {
        this.isEnabled = true;
    }
    
    public void setDisabled() {
        this.isEnabled = false;
    }
    
    public boolean needsToFire(final int n, final int[] array) throws StandardException {
        if (!this.isEnabled) {
            return false;
        }
        if (n == 1) {
            return (this.eventMask & 0x4) == this.eventMask;
        }
        if (n == 4) {
            return (this.eventMask & 0x2) == this.eventMask;
        }
        if (n == 2) {
            throw StandardException.newException("42Z08", this.getTableDescriptor().getQualifiedName(), this.name);
        }
        return (this.eventMask & 0x1) == this.eventMask && ConstraintDescriptor.doColumnsIntersect(array, this.referencedCols);
    }
    
    public String getTriggerDefinition() {
        return this.triggerDefinition;
    }
    
    public boolean getReferencingOld() {
        return this.referencingOld;
    }
    
    public boolean getReferencingNew() {
        return this.referencingNew;
    }
    
    public String getOldReferencingName() {
        return this.oldReferencingName;
    }
    
    public String getNewReferencingName() {
        return this.newReferencingName;
    }
    
    public String toString() {
        return "";
    }
    
    public DependableFinder getDependableFinder() {
        return this.getDependableFinder(320);
    }
    
    public String getObjectName() {
        return this.name;
    }
    
    public UUID getObjectID() {
        return this.id;
    }
    
    public String getClassType() {
        return "Trigger";
    }
    
    public synchronized boolean isValid() {
        return true;
    }
    
    public void prepareToInvalidate(final Provider provider, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        switch (n) {
            case 1:
            case 13:
            case 34:
            case 43:
            case 45: {
                throw StandardException.newException("X0Y25.S", this.getDataDictionary().getDependencyManager().getActionString(n), provider.getObjectName(), "TRIGGER", this.name);
            }
            default: {}
        }
    }
    
    public void makeInvalid(final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        switch (n) {
            case 14: {
                this.getDataDictionary().getDependencyManager().invalidateFor(this, 11, languageConnectionContext);
                break;
            }
            case 44:
            case 47: {
                this.drop(languageConnectionContext);
                languageConnectionContext.getLastActivation().addWarning(StandardException.newWarning("01502", this.getObjectName()));
                break;
            }
        }
    }
    
    public void drop(final LanguageConnectionContext languageConnectionContext) throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        final DependencyManager dependencyManager = this.getDataDictionary().getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dependencyManager.invalidateFor(this, 27, languageConnectionContext);
        dataDictionary.dropTriggerDescriptor(this, transactionExecute);
        dependencyManager.clearDependencies(languageConnectionContext, this);
        final SPSDescriptor spsDescriptor = dataDictionary.getSPSDescriptor(this.getActionId());
        dependencyManager.invalidateFor(spsDescriptor, 27, languageConnectionContext);
        dependencyManager.clearDependencies(languageConnectionContext, spsDescriptor);
        dataDictionary.dropSPSDescriptor(spsDescriptor, transactionExecute);
        if (this.getWhenClauseId() != null) {
            final SPSDescriptor spsDescriptor2 = dataDictionary.getSPSDescriptor(this.getWhenClauseId());
            dependencyManager.invalidateFor(spsDescriptor2, 27, languageConnectionContext);
            dependencyManager.clearDependencies(languageConnectionContext, spsDescriptor2);
            dataDictionary.dropSPSDescriptor(spsDescriptor2, transactionExecute);
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.id = (UUID)objectInput.readObject();
        this.name = (String)objectInput.readObject();
        this.triggerSchemaId = (UUID)objectInput.readObject();
        this.triggerTableId = (UUID)objectInput.readObject();
        this.eventMask = objectInput.readInt();
        this.isBefore = objectInput.readBoolean();
        this.isRow = objectInput.readBoolean();
        this.isEnabled = objectInput.readBoolean();
        this.whenSPSId = (UUID)objectInput.readObject();
        this.actionSPSId = (UUID)objectInput.readObject();
        final int int1 = objectInput.readInt();
        if (int1 != 0) {
            this.referencedCols = new int[int1];
            for (int i = 0; i < int1; ++i) {
                this.referencedCols[i] = objectInput.readInt();
            }
        }
        final int int2 = objectInput.readInt();
        if (int2 != 0) {
            this.referencedColsInTriggerAction = new int[int2];
            for (int j = 0; j < int2; ++j) {
                this.referencedColsInTriggerAction[j] = objectInput.readInt();
            }
        }
        this.triggerDefinition = (String)objectInput.readObject();
        this.referencingOld = objectInput.readBoolean();
        this.referencingNew = objectInput.readBoolean();
        this.oldReferencingName = (String)objectInput.readObject();
        this.newReferencingName = (String)objectInput.readObject();
    }
    
    protected DataDictionary getDataDictionary() {
        DataDictionary dataDictionary = super.getDataDictionary();
        if (dataDictionary == null) {
            dataDictionary = ((LanguageConnectionContext)ContextService.getContext("LanguageConnectionContext")).getDataDictionary();
            this.setDataDictionary(dataDictionary);
        }
        return dataDictionary;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.id);
        objectOutput.writeObject(this.name);
        objectOutput.writeObject(this.triggerSchemaId);
        objectOutput.writeObject(this.triggerTableId);
        objectOutput.writeInt(this.eventMask);
        objectOutput.writeBoolean(this.isBefore);
        objectOutput.writeBoolean(this.isRow);
        objectOutput.writeBoolean(this.isEnabled);
        objectOutput.writeObject(this.whenSPSId);
        objectOutput.writeObject(this.actionSPSId);
        if (this.referencedCols == null) {
            objectOutput.writeInt(0);
        }
        else {
            objectOutput.writeInt(this.referencedCols.length);
            for (int i = 0; i < this.referencedCols.length; ++i) {
                objectOutput.writeInt(this.referencedCols[i]);
            }
        }
        if (this.referencedColsInTriggerAction == null) {
            objectOutput.writeInt(0);
        }
        else {
            objectOutput.writeInt(this.referencedColsInTriggerAction.length);
            for (int j = 0; j < this.referencedColsInTriggerAction.length; ++j) {
                objectOutput.writeInt(this.referencedColsInTriggerAction[j]);
            }
        }
        objectOutput.writeObject(this.triggerDefinition);
        objectOutput.writeBoolean(this.referencingOld);
        objectOutput.writeBoolean(this.referencingNew);
        objectOutput.writeObject(this.oldReferencingName);
        objectOutput.writeObject(this.newReferencingName);
    }
    
    public int getTypeFormatId() {
        return 316;
    }
    
    public String getDescriptorType() {
        return "Trigger";
    }
    
    public String getDescriptorName() {
        return this.name;
    }
}
