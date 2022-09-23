// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import java.util.List;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.catalog.types.SynonymAliasInfo;
import org.apache.derby.catalog.types.RoutineAliasInfo;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.catalog.AliasInfo;

class CreateAliasConstantAction extends DDLConstantAction
{
    private final String aliasName;
    private final String schemaName;
    private final String javaClassName;
    private final char aliasType;
    private final char nameSpace;
    private final AliasInfo aliasInfo;
    
    CreateAliasConstantAction(final String aliasName, final String schemaName, final String javaClassName, final AliasInfo aliasInfo, final char aliasType) {
        this.aliasName = aliasName;
        this.schemaName = schemaName;
        this.javaClassName = javaClassName;
        this.aliasInfo = aliasInfo;
        switch (this.aliasType = aliasType) {
            case 'G': {
                this.nameSpace = 'G';
                break;
            }
            case 'P': {
                this.nameSpace = 'P';
                break;
            }
            case 'F': {
                this.nameSpace = 'F';
                break;
            }
            case 'S': {
                this.nameSpace = 'S';
                break;
            }
            case 'A': {
                this.nameSpace = 'A';
                break;
            }
            default: {
                this.nameSpace = '\0';
                break;
            }
        }
    }
    
    public String toString() {
        String str = null;
        switch (this.aliasType) {
            case 'G': {
                str = "CREATE DERBY AGGREGATE ";
                break;
            }
            case 'P': {
                str = "CREATE PROCEDURE ";
                break;
            }
            case 'F': {
                str = "CREATE FUNCTION ";
                break;
            }
            case 'S': {
                str = "CREATE SYNONYM ";
                break;
            }
            case 'A': {
                str = "CREATE TYPE ";
                break;
            }
        }
        return str + this.aliasName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dataDictionary.startWriting(languageConnectionContext);
        final SchemaDescriptor schemaDescriptorForCreate = DDLConstantAction.getSchemaDescriptorForCreate(dataDictionary, activation, this.schemaName);
        final AliasDescriptor aliasDescriptor = new AliasDescriptor(dataDictionary, dataDictionary.getUUIDFactory().createUUID(), this.aliasName, schemaDescriptorForCreate.getUUID(), this.javaClassName, this.aliasType, this.nameSpace, false, this.aliasInfo, null);
        switch (this.aliasType) {
            case 'G': {
                if (dataDictionary.getAliasDescriptor(schemaDescriptorForCreate.getUUID().toString(), this.aliasName, this.nameSpace) != null) {
                    throw StandardException.newException("X0Y68.S", aliasDescriptor.getDescriptorType(), this.aliasName);
                }
                final List routineList = dataDictionary.getRoutineList(schemaDescriptorForCreate.getUUID().toString(), this.aliasName, 'F');
                for (int i = 0; i < routineList.size(); ++i) {
                    if (((RoutineAliasInfo)routineList.get(i).getAliasInfo()).getParameterCount() == 1) {
                        throw StandardException.newException("X0Y87.S", this.schemaName, this.aliasName);
                    }
                }
                break;
            }
            case 'A': {
                if (dataDictionary.getAliasDescriptor(schemaDescriptorForCreate.getUUID().toString(), this.aliasName, this.nameSpace) != null) {
                    throw StandardException.newException("X0Y68.S", aliasDescriptor.getDescriptorType(), this.aliasName);
                }
                break;
            }
            case 'P': {
                this.vetRoutine(dataDictionary, schemaDescriptorForCreate, aliasDescriptor);
                break;
            }
            case 'F': {
                this.vetRoutine(dataDictionary, schemaDescriptorForCreate, aliasDescriptor);
                if (((RoutineAliasInfo)this.aliasInfo).getParameterCount() != 1) {
                    break;
                }
                if (dataDictionary.getAliasDescriptor(schemaDescriptorForCreate.getUUID().toString(), this.aliasName, 'G') != null) {
                    throw StandardException.newException("X0Y87.S", this.schemaName, this.aliasName);
                }
                break;
            }
            case 'S': {
                TableDescriptor tableDescriptor = dataDictionary.getTableDescriptor(this.aliasName, schemaDescriptorForCreate, transactionExecute);
                if (tableDescriptor != null) {
                    throw StandardException.newException("X0Y68.S", tableDescriptor.getDescriptorType(), tableDescriptor.getDescriptorName());
                }
                String s = ((SynonymAliasInfo)this.aliasInfo).getSynonymTable();
                String s2 = ((SynonymAliasInfo)this.aliasInfo).getSynonymSchema();
                SchemaDescriptor schemaDescriptor;
                while (true) {
                    schemaDescriptor = dataDictionary.getSchemaDescriptor(s2, transactionExecute, false);
                    if (schemaDescriptor == null) {
                        break;
                    }
                    final AliasDescriptor aliasDescriptor2 = dataDictionary.getAliasDescriptor(schemaDescriptor.getUUID().toString(), s, this.nameSpace);
                    if (aliasDescriptor2 == null) {
                        break;
                    }
                    final SynonymAliasInfo synonymAliasInfo = (SynonymAliasInfo)aliasDescriptor2.getAliasInfo();
                    s = synonymAliasInfo.getSynonymTable();
                    s2 = synonymAliasInfo.getSynonymSchema();
                    if (this.aliasName.equals(s) && this.schemaName.equals(s2)) {
                        throw StandardException.newException("42916", this.aliasName, ((SynonymAliasInfo)this.aliasInfo).getSynonymTable());
                    }
                }
                if (schemaDescriptor != null) {
                    tableDescriptor = dataDictionary.getTableDescriptor(s, schemaDescriptor, transactionExecute);
                }
                if (schemaDescriptor == null || tableDescriptor == null) {
                    activation.addWarning(StandardException.newWarning("01522", this.aliasName, s2 + "." + s));
                }
                dataDictionary.addDescriptor(dataDictionary.getDataDescriptorGenerator().newTableDescriptor(this.aliasName, schemaDescriptorForCreate, 4, 'R'), schemaDescriptorForCreate, 1, false, transactionExecute);
                break;
            }
        }
        dataDictionary.addDescriptor(aliasDescriptor, null, 7, false, transactionExecute);
        this.adjustUDTDependencies(languageConnectionContext, dataDictionary, aliasDescriptor, true);
    }
    
    private void vetRoutine(final DataDictionary dataDictionary, final SchemaDescriptor schemaDescriptor, final AliasDescriptor aliasDescriptor) throws StandardException {
        final List routineList = dataDictionary.getRoutineList(schemaDescriptor.getUUID().toString(), this.aliasName, this.aliasType);
        for (int i = routineList.size() - 1; i >= 0; --i) {
            if (((RoutineAliasInfo)routineList.get(i).getAliasInfo()).getParameterCount() == ((RoutineAliasInfo)this.aliasInfo).getParameterCount()) {
                throw StandardException.newException("X0Y68.S", aliasDescriptor.getDescriptorType(), this.aliasName);
            }
        }
    }
}
