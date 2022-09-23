// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.depend.Provider;

public final class DefaultDescriptor extends TupleDescriptor implements UniqueTupleDescriptor, Provider, Dependent
{
    private final int columnNumber;
    private final UUID defaultUUID;
    private final UUID tableUUID;
    
    public DefaultDescriptor(final DataDictionary dataDictionary, final UUID defaultUUID, final UUID tableUUID, final int columnNumber) {
        super(dataDictionary);
        this.defaultUUID = defaultUUID;
        this.tableUUID = tableUUID;
        this.columnNumber = columnNumber;
    }
    
    public UUID getUUID() {
        return this.defaultUUID;
    }
    
    public UUID getTableUUID() {
        return this.tableUUID;
    }
    
    public int getColumnNumber() {
        return this.columnNumber;
    }
    
    public String toString() {
        return "";
    }
    
    public DependableFinder getDependableFinder() {
        return this.getDependableFinder(325);
    }
    
    public String getObjectName() {
        return "default";
    }
    
    public UUID getObjectID() {
        return this.defaultUUID;
    }
    
    public String getClassType() {
        return "Default";
    }
    
    public synchronized boolean isValid() {
        return true;
    }
    
    public void prepareToInvalidate(final Provider provider, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        final DependencyManager dependencyManager = this.getDataDictionary().getDependencyManager();
        final DataDictionary dataDictionary = this.getDataDictionary();
        final ColumnDescriptor columnDescriptorByDefaultId = dataDictionary.getColumnDescriptorByDefaultId(this.defaultUUID);
        throw StandardException.newException("X0Y25.S", dependencyManager.getActionString(n), provider.getObjectName(), "DEFAULT", dataDictionary.getTableDescriptor(columnDescriptorByDefaultId.getReferencingUUID()).getQualifiedName() + "." + columnDescriptorByDefaultId.getColumnName());
    }
    
    public void makeInvalid(final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
    }
}
