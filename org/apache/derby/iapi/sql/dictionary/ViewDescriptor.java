// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.depend.Dependent;

public final class ViewDescriptor extends TupleDescriptor implements UniqueTupleDescriptor, Dependent, Provider
{
    private final int checkOption;
    private String viewName;
    private final String viewText;
    private UUID uuid;
    private final UUID compSchemaId;
    public static final int NO_CHECK_OPTION = 0;
    
    public ViewDescriptor(final DataDictionary dataDictionary, final UUID uuid, final String viewName, final String viewText, final int checkOption, final UUID compSchemaId) {
        super(dataDictionary);
        this.uuid = uuid;
        this.viewText = viewText;
        this.viewName = viewName;
        this.checkOption = checkOption;
        this.compSchemaId = compSchemaId;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public void setUUID(final UUID uuid) {
        this.uuid = uuid;
    }
    
    public String getViewText() {
        return this.viewText;
    }
    
    public void setViewName(final String viewName) {
        this.viewName = viewName;
    }
    
    public int getCheckOptionType() {
        return this.checkOption;
    }
    
    public UUID getCompSchemaId() {
        return this.compSchemaId;
    }
    
    public DependableFinder getDependableFinder() {
        return this.getDependableFinder(145);
    }
    
    public String getObjectName() {
        return this.viewName;
    }
    
    public UUID getObjectID() {
        return this.uuid;
    }
    
    public String getClassType() {
        return "View";
    }
    
    public boolean isValid() {
        return true;
    }
    
    public void prepareToInvalidate(final Provider provider, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        switch (n) {
            case 2:
            case 3:
            case 12:
            case 15:
            case 20:
            case 22:
            case 27:
            case 28:
            case 29:
            case 33:
            case 37:
            case 39:
            case 40:
            case 41:
            case 42:
            case 44:
            case 47:
            case 48: {
                break;
            }
            case 23: {
                break;
            }
            default: {
                throw StandardException.newException("X0Y23.S", this.getDataDictionary().getDependencyManager().getActionString(n), provider.getObjectName(), this.viewName);
            }
        }
    }
    
    public void makeInvalid(final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        switch (n) {
            case 37:
            case 44:
            case 47: {
                final TableDescriptor tableDescriptor = this.getDataDictionary().getTableDescriptor(this.uuid);
                if (tableDescriptor == null) {
                    break;
                }
                this.drop(languageConnectionContext, tableDescriptor.getSchemaDescriptor(), tableDescriptor, n);
                languageConnectionContext.getLastActivation().addWarning(StandardException.newWarning("01501", this.getObjectName()));
                break;
            }
        }
    }
    
    public String toString() {
        return "";
    }
    
    public void drop(final LanguageConnectionContext languageConnectionContext, final SchemaDescriptor schemaDescriptor, final TableDescriptor tableDescriptor) throws StandardException {
        this.drop(languageConnectionContext, schemaDescriptor, tableDescriptor, 9);
    }
    
    private void drop(final LanguageConnectionContext languageConnectionContext, final SchemaDescriptor schemaDescriptor, final TableDescriptor tableDescriptor, final int n) throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dataDictionary.dropAllColumnDescriptors(tableDescriptor.getUUID(), transactionExecute);
        dependencyManager.invalidateFor(tableDescriptor, n, languageConnectionContext);
        dependencyManager.clearDependencies(languageConnectionContext, this);
        dataDictionary.dropViewDescriptor(this, transactionExecute);
        dataDictionary.dropAllTableAndColPermDescriptors(tableDescriptor.getUUID(), transactionExecute);
        dataDictionary.dropTableDescriptor(tableDescriptor, schemaDescriptor, transactionExecute);
    }
    
    public String getName() {
        return this.viewName;
    }
}
