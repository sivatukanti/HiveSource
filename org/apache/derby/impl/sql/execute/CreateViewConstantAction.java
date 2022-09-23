// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.ViewDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.ProviderInfo;

class CreateViewConstantAction extends DDLConstantAction
{
    private final String tableName;
    private final String schemaName;
    private final String viewText;
    private final int tableType;
    private final int checkOption;
    private final ColumnInfo[] columnInfo;
    private final ProviderInfo[] providerInfo;
    private final UUID compSchemaId;
    
    CreateViewConstantAction(final String schemaName, final String tableName, final int tableType, final String viewText, final int checkOption, final ColumnInfo[] columnInfo, final ProviderInfo[] providerInfo, final UUID compSchemaId) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.tableType = tableType;
        this.viewText = viewText;
        this.checkOption = checkOption;
        this.columnInfo = columnInfo;
        this.providerInfo = providerInfo;
        this.compSchemaId = compSchemaId;
    }
    
    public String toString() {
        return this.constructToString("CREATE VIEW ", this.tableName);
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dataDictionary.startWriting(languageConnectionContext);
        final SchemaDescriptor schemaDescriptorForCreate = DDLConstantAction.getSchemaDescriptorForCreate(dataDictionary, activation, this.schemaName);
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        final TableDescriptor tableDescriptor = dataDescriptorGenerator.newTableDescriptor(this.tableName, schemaDescriptorForCreate, this.tableType, 'R');
        dataDictionary.addDescriptor(tableDescriptor, schemaDescriptorForCreate, 1, false, transactionExecute);
        final UUID uuid = tableDescriptor.getUUID();
        final ColumnDescriptor[] array = new ColumnDescriptor[this.columnInfo.length];
        int n = 1;
        for (int i = 0; i < this.columnInfo.length; ++i) {
            array[i] = new ColumnDescriptor(this.columnInfo[i].name, n++, this.columnInfo[i].dataType, this.columnInfo[i].defaultValue, this.columnInfo[i].defaultInfo, tableDescriptor, null, this.columnInfo[i].autoincStart, this.columnInfo[i].autoincInc);
        }
        dataDictionary.addDescriptorArray(array, tableDescriptor, 2, false, transactionExecute);
        final ColumnDescriptorList columnDescriptorList = tableDescriptor.getColumnDescriptorList();
        for (int j = 0; j < array.length; ++j) {
            columnDescriptorList.add(array[j]);
        }
        final ViewDescriptor viewDescriptor = dataDescriptorGenerator.newViewDescriptor(uuid, this.tableName, this.viewText, this.checkOption, (this.compSchemaId == null) ? languageConnectionContext.getDefaultSchema().getUUID() : this.compSchemaId);
        for (int k = 0; k < this.providerInfo.length; ++k) {
            dependencyManager.addDependency(viewDescriptor, (Provider)this.providerInfo[k].getDependableFinder().getDependable(dataDictionary, this.providerInfo[k].getObjectId()), languageConnectionContext.getContextManager());
        }
        this.storeViewTriggerDependenciesOnPrivileges(activation, viewDescriptor);
        dataDictionary.addDescriptor(viewDescriptor, schemaDescriptorForCreate, 8, true, transactionExecute);
    }
}
