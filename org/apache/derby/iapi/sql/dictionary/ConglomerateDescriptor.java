// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.Provider;

public final class ConglomerateDescriptor extends TupleDescriptor implements UniqueTupleDescriptor, Provider
{
    private long conglomerateNumber;
    private String name;
    private transient String[] columnNames;
    private final boolean indexable;
    private final boolean forConstraint;
    private final IndexRowGenerator indexRowGenerator;
    private final UUID uuid;
    private final UUID tableID;
    private final UUID schemaID;
    
    ConglomerateDescriptor(final DataDictionary dataDictionary, final long conglomerateNumber, final String name, final boolean indexable, final IndexRowGenerator indexRowGenerator, final boolean forConstraint, UUID uuid, final UUID tableID, final UUID schemaID) {
        super(dataDictionary);
        this.conglomerateNumber = conglomerateNumber;
        this.name = name;
        this.indexable = indexable;
        this.indexRowGenerator = indexRowGenerator;
        this.forConstraint = forConstraint;
        if (uuid == null) {
            uuid = Monitor.getMonitor().getUUIDFactory().createUUID();
        }
        this.uuid = uuid;
        this.tableID = tableID;
        this.schemaID = schemaID;
    }
    
    public long getConglomerateNumber() {
        return this.conglomerateNumber;
    }
    
    public void setConglomerateNumber(final long conglomerateNumber) {
        this.conglomerateNumber = conglomerateNumber;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public UUID getTableID() {
        return this.tableID;
    }
    
    public UUID getSchemaID() {
        return this.schemaID;
    }
    
    public boolean isIndex() {
        return this.indexable;
    }
    
    public boolean isConstraint() {
        return this.forConstraint;
    }
    
    public String getConglomerateName() {
        return this.name;
    }
    
    public void setConglomerateName(final String name) {
        this.name = name;
    }
    
    public IndexRowGenerator getIndexDescriptor() {
        return this.indexRowGenerator;
    }
    
    public void setColumnNames(final String[] columnNames) {
        this.columnNames = columnNames;
    }
    
    public String[] getColumnNames() {
        return this.columnNames;
    }
    
    public DependableFinder getDependableFinder() {
        return this.getDependableFinder(135);
    }
    
    public String getObjectName() {
        return this.name;
    }
    
    public UUID getObjectID() {
        return this.uuid;
    }
    
    public String getClassType() {
        if (this.indexable) {
            return "Index";
        }
        return "Heap";
    }
    
    public String toString() {
        return "";
    }
    
    public String getDescriptorType() {
        if (this.indexable) {
            return "Index";
        }
        return "Table";
    }
    
    public String getDescriptorName() {
        return this.name;
    }
    
    public ConglomerateDescriptor drop(final LanguageConnectionContext languageConnectionContext, final TableDescriptor tableDescriptor) throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dependencyManager.invalidateFor(this, 2, languageConnectionContext);
        final ConglomerateDescriptor[] conglomerateDescriptors = dataDictionary.getConglomerateDescriptors(this.getConglomerateNumber());
        boolean b = false;
        ConglomerateDescriptor describeSharedConglomerate = null;
        if (conglomerateDescriptors.length == 1) {
            b = true;
        }
        else {
            describeSharedConglomerate = this.describeSharedConglomerate(conglomerateDescriptors, true);
            final IndexRowGenerator indexDescriptor = describeSharedConglomerate.getIndexDescriptor();
            if ((this.indexRowGenerator.isUnique() && !indexDescriptor.isUnique()) || (this.indexRowGenerator.isUniqueWithDuplicateNulls() && !indexDescriptor.isUniqueWithDuplicateNulls())) {
                b = true;
            }
            else {
                describeSharedConglomerate = null;
            }
        }
        dataDictionary.dropStatisticsDescriptors(tableDescriptor.getUUID(), this.getUUID(), transactionExecute);
        if (b) {
            transactionExecute.dropConglomerate(this.getConglomerateNumber());
        }
        dataDictionary.dropConglomerateDescriptor(this, transactionExecute);
        tableDescriptor.removeConglomerateDescriptor(this);
        return describeSharedConglomerate;
    }
    
    public ConglomerateDescriptor describeSharedConglomerate(final ConglomerateDescriptor[] array, final boolean b) throws StandardException {
        if (!this.isIndex()) {
            ConglomerateDescriptor conglomerateDescriptor = null;
            for (int i = 0; i < array.length; ++i) {
                if (this.getConglomerateNumber() == array[i].getConglomerateNumber()) {
                    conglomerateDescriptor = array[i];
                }
            }
            return conglomerateDescriptor;
        }
        ConglomerateDescriptor conglomerateDescriptor2 = null;
        for (int j = 0; j < array.length; ++j) {
            if (array[j].isIndex()) {
                if (this.getConglomerateNumber() == array[j].getConglomerateNumber()) {
                    if (!b || !this.getUUID().equals(array[j].getUUID()) || !this.getConglomerateName().equals(array[j].getConglomerateName())) {
                        if (array[j].getIndexDescriptor().isUnique()) {
                            conglomerateDescriptor2 = array[j];
                            break;
                        }
                        if (array[j].getIndexDescriptor().isUniqueWithDuplicateNulls()) {
                            conglomerateDescriptor2 = array[j];
                        }
                        else if (conglomerateDescriptor2 == null) {
                            conglomerateDescriptor2 = array[j];
                        }
                    }
                }
            }
        }
        return conglomerateDescriptor2;
    }
}
