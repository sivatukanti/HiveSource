// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.depend.Provider;

public class SequenceDescriptor extends TupleDescriptor implements Provider, Dependent, PrivilegedSQLObject
{
    private UUID sequenceUUID;
    private String sequenceName;
    private final SchemaDescriptor schemaDescriptor;
    private UUID schemaId;
    private DataTypeDescriptor dataType;
    private Long currentValue;
    private long startValue;
    private long minimumValue;
    private long maximumValue;
    private long increment;
    private boolean canCycle;
    
    public SequenceDescriptor(final DataDictionary dataDictionary, final SchemaDescriptor schemaDescriptor, final UUID sequenceUUID, final String sequenceName, final DataTypeDescriptor dataType, final Long currentValue, final long startValue, final long minimumValue, final long maximumValue, final long increment, final boolean canCycle) {
        super(dataDictionary);
        this.sequenceUUID = sequenceUUID;
        this.schemaDescriptor = schemaDescriptor;
        this.sequenceName = sequenceName;
        this.schemaId = schemaDescriptor.getUUID();
        this.dataType = dataType;
        this.currentValue = currentValue;
        this.startValue = startValue;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.increment = increment;
        this.canCycle = canCycle;
    }
    
    public UUID getUUID() {
        return this.sequenceUUID;
    }
    
    public String getObjectTypeName() {
        return "SEQUENCE";
    }
    
    public String toString() {
        return "";
    }
    
    public void drop(final LanguageConnectionContext languageConnectionContext) throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        final DependencyManager dependencyManager = this.getDataDictionary().getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dependencyManager.invalidateFor(this, 49, languageConnectionContext);
        dataDictionary.dropSequenceDescriptor(this, transactionExecute);
        dependencyManager.clearDependencies(languageConnectionContext, this);
    }
    
    public synchronized boolean isValid() {
        return true;
    }
    
    public void prepareToInvalidate(final Provider provider, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
    }
    
    public void makeInvalid(final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        switch (n) {
            case 14: {
                this.getDataDictionary().getDependencyManager().invalidateFor(this, 11, languageConnectionContext);
                break;
            }
        }
    }
    
    public String getName() {
        return this.sequenceName;
    }
    
    public SchemaDescriptor getSchemaDescriptor() throws StandardException {
        return this.schemaDescriptor;
    }
    
    public String getDescriptorType() {
        return "Sequence";
    }
    
    public String getDescriptorName() {
        return this.sequenceName;
    }
    
    public UUID getObjectID() {
        return this.sequenceUUID;
    }
    
    public boolean isPersistent() {
        return true;
    }
    
    public String getObjectName() {
        return this.sequenceName;
    }
    
    public String getClassType() {
        return "Sequence";
    }
    
    public DependableFinder getDependableFinder() {
        return this.getDependableFinder(472);
    }
    
    public String getSequenceName() {
        return this.sequenceName;
    }
    
    public UUID getSchemaId() {
        return this.schemaId;
    }
    
    public DataTypeDescriptor getDataType() {
        return this.dataType;
    }
    
    public Long getCurrentValue() {
        return this.currentValue;
    }
    
    public long getStartValue() {
        return this.startValue;
    }
    
    public long getMinimumValue() {
        return this.minimumValue;
    }
    
    public long getMaximumValue() {
        return this.maximumValue;
    }
    
    public long getIncrement() {
        return this.increment;
    }
    
    public boolean canCycle() {
        return this.canCycle;
    }
}
