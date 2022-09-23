// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.catalog.types.RoutineAliasInfo;
import org.apache.derby.catalog.types.AggregateAliasInfo;
import org.apache.derby.iapi.sql.dictionary.DependencyDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import java.util.HashSet;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import java.util.HashMap;
import org.apache.derby.iapi.sql.dictionary.DefaultDescriptor;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.RoleClosureIterator;
import org.apache.derby.iapi.sql.dictionary.PermissionsDescriptor;
import java.util.Iterator;
import java.util.List;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.ColPermsDescriptor;
import org.apache.derby.iapi.sql.dictionary.StatementRoutinePermission;
import org.apache.derby.iapi.sql.dictionary.StatementGenericPermission;
import org.apache.derby.iapi.sql.dictionary.StatementRolePermission;
import org.apache.derby.iapi.sql.dictionary.StatementSchemaPermission;
import org.apache.derby.iapi.sql.dictionary.StatementColumnPermission;
import org.apache.derby.iapi.sql.dictionary.StatementTablePermission;
import org.apache.derby.iapi.sql.dictionary.StatementPermission;
import org.apache.derby.iapi.sql.depend.ProviderInfo;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.execute.ConstantAction;

abstract class DDLConstantAction implements ConstantAction
{
    static SchemaDescriptor getAndCheckSchemaDescriptor(final DataDictionary dataDictionary, final UUID uuid, final String s) throws StandardException {
        return dataDictionary.getSchemaDescriptor(uuid, null);
    }
    
    static SchemaDescriptor getSchemaDescriptorForCreate(final DataDictionary dataDictionary, final Activation activation, final String s) throws StandardException {
        final TransactionController transactionExecute = activation.getLanguageConnectionContext().getTransactionExecute();
        SchemaDescriptor schemaDescriptor = dataDictionary.getSchemaDescriptor(s, transactionExecute, false);
        if (schemaDescriptor == null || schemaDescriptor.getUUID() == null) {
            final CreateSchemaConstantAction createSchemaConstantAction = new CreateSchemaConstantAction(s, null);
            if (activation.getLanguageConnectionContext().isInitialDefaultSchema(s)) {
                executeCAPreferSubTrans(createSchemaConstantAction, transactionExecute, activation);
            }
            else {
                try {
                    createSchemaConstantAction.executeConstantAction(activation);
                }
                catch (StandardException ex) {
                    if (!ex.getMessageId().equals("X0Y68.S")) {
                        throw ex;
                    }
                }
            }
            schemaDescriptor = dataDictionary.getSchemaDescriptor(s, transactionExecute, true);
        }
        return schemaDescriptor;
    }
    
    private static void executeCAPreferSubTrans(final CreateSchemaConstantAction createSchemaConstantAction, final TransactionController transactionController, final Activation activation) throws StandardException {
        TransactionController transactionController2 = null;
        TransactionController startNestedUserTransaction;
        try {
            transactionController2 = (startNestedUserTransaction = transactionController.startNestedUserTransaction(false, true));
        }
        catch (StandardException ex2) {
            startNestedUserTransaction = transactionController;
        }
    Label_0107:
        while (true) {
            try {
                createSchemaConstantAction.executeConstantAction(activation, startNestedUserTransaction);
            }
            catch (StandardException ex) {
                if (ex.isLockTimeout()) {
                    if (!ex.getMessageId().equals("40XL1.T.1") && startNestedUserTransaction == transactionController2) {
                        startNestedUserTransaction = transactionController;
                        transactionController2.destroy();
                        continue;
                    }
                }
                else if (ex.getMessageId().equals("X0Y68.S")) {
                    break Label_0107;
                }
                if (startNestedUserTransaction == transactionController2) {
                    transactionController2.destroy();
                }
                throw ex;
            }
            break;
        }
        if (startNestedUserTransaction == transactionController2) {
            transactionController2.commit();
            transactionController2.destroy();
        }
    }
    
    final void lockTableForDDL(final TransactionController transactionController, final long n, final boolean b) throws StandardException {
        transactionController.openConglomerate(n, false, b ? 68 : 64, 7, 5).close();
    }
    
    protected String constructToString(final String str, final String str2) {
        return str + str2;
    }
    
    protected void storeConstraintDependenciesOnPrivileges(final Activation activation, final Dependent dependent, final UUID obj, final ProviderInfo[] array) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        dataDictionary.getAuthorizationDatabaseOwner();
        final String currentUserId = languageConnectionContext.getCurrentUserId(activation);
        final SettableBoolean settableBoolean = new SettableBoolean();
        if (!currentUserId.equals(dataDictionary.getAuthorizationDatabaseOwner())) {
            final List requiredPermissionsList = activation.getPreparedStatement().getRequiredPermissionsList();
            if (requiredPermissionsList != null && !requiredPermissionsList.isEmpty()) {
                for (final StatementPermission statementPermission : requiredPermissionsList) {
                    if (statementPermission instanceof StatementTablePermission) {
                        final StatementColumnPermission statementColumnPermission = (StatementColumnPermission)statementPermission;
                        if (statementColumnPermission.getPrivType() != 2) {
                            continue;
                        }
                        if (!statementColumnPermission.getTableUUID().equals(obj)) {
                            continue;
                        }
                    }
                    else {
                        if (statementPermission instanceof StatementSchemaPermission || statementPermission instanceof StatementRolePermission) {
                            continue;
                        }
                        if (statementPermission instanceof StatementGenericPermission) {
                            continue;
                        }
                        if (!this.inProviderSet(array, ((StatementRoutinePermission)statementPermission).getRoutineUUID())) {
                            continue;
                        }
                    }
                    final PermissionsDescriptor permissionDescriptor = statementPermission.getPermissionDescriptor(currentUserId, dataDictionary);
                    if (permissionDescriptor == null) {
                        PermissionsDescriptor permissionsDescriptor = statementPermission.getPermissionDescriptor("PUBLIC", dataDictionary);
                        boolean b = false;
                        if (permissionsDescriptor == null || (permissionsDescriptor instanceof ColPermsDescriptor && !((StatementColumnPermission)statementPermission).allColumnsCoveredByUserOrPUBLIC(currentUserId, dataDictionary))) {
                            b = true;
                            permissionsDescriptor = findRoleUsage(activation, statementPermission);
                        }
                        if (!permissionsDescriptor.checkOwner(currentUserId)) {
                            dependencyManager.addDependency(dependent, permissionsDescriptor, languageConnectionContext.getContextManager());
                            if (b) {
                                trackRoleDependency(activation, dependent, settableBoolean);
                            }
                        }
                    }
                    else if (!permissionDescriptor.checkOwner(currentUserId)) {
                        dependencyManager.addDependency(dependent, permissionDescriptor, languageConnectionContext.getContextManager());
                        if (permissionDescriptor instanceof ColPermsDescriptor) {
                            final StatementColumnPermission statementColumnPermission2 = (StatementColumnPermission)statementPermission;
                            final PermissionsDescriptor publiClevelColPermsDescriptor = statementColumnPermission2.getPUBLIClevelColPermsDescriptor(currentUserId, dataDictionary);
                            if (publiClevelColPermsDescriptor != null && publiClevelColPermsDescriptor.getObjectID() != null) {
                                dependencyManager.addDependency(dependent, publiClevelColPermsDescriptor, languageConnectionContext.getContextManager());
                            }
                            if (!statementColumnPermission2.allColumnsCoveredByUserOrPUBLIC(currentUserId, dataDictionary)) {
                                trackRoleDependency(activation, dependent, settableBoolean);
                            }
                        }
                    }
                    if (!(statementPermission instanceof StatementRoutinePermission)) {
                        break;
                    }
                }
            }
        }
    }
    
    private static PermissionsDescriptor findRoleUsage(final Activation activation, final StatementPermission statementPermission) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final String currentRoleId = languageConnectionContext.getCurrentRoleId(activation);
        final String authorizationDatabaseOwner = dataDictionary.getAuthorizationDatabaseOwner();
        final String currentUserId = languageConnectionContext.getCurrentUserId(activation);
        PermissionsDescriptor permissionDescriptor = null;
        if (dataDictionary.getRoleGrantDescriptor(currentRoleId, currentUserId, authorizationDatabaseOwner) == null) {
            dataDictionary.getRoleGrantDescriptor(currentRoleId, "PUBLIC", authorizationDatabaseOwner);
        }
        String next;
        for (RoleClosureIterator roleClosureIterator = dataDictionary.createRoleClosureIterator(activation.getTransactionController(), currentRoleId, true); permissionDescriptor == null && (next = roleClosureIterator.next()) != null; permissionDescriptor = statementPermission.getPermissionDescriptor(next, dataDictionary)) {}
        return permissionDescriptor;
    }
    
    private static void trackRoleDependency(final Activation activation, final Dependent dependent, final SettableBoolean settableBoolean) throws StandardException {
        if (!settableBoolean.get()) {
            final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
            final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
            dataDictionary.getDependencyManager().addDependency(dependent, dataDictionary.getRoleDefinitionDescriptor(languageConnectionContext.getCurrentRoleId(activation)), languageConnectionContext.getContextManager());
            settableBoolean.set(true);
        }
    }
    
    protected void storeViewTriggerDependenciesOnPrivileges(final Activation activation, final Dependent dependent) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final String authorizationDatabaseOwner = dataDictionary.getAuthorizationDatabaseOwner();
        final String currentUserId = languageConnectionContext.getCurrentUserId(activation);
        final SettableBoolean settableBoolean = new SettableBoolean();
        if (!currentUserId.equals(authorizationDatabaseOwner)) {
            final List requiredPermissionsList = activation.getPreparedStatement().getRequiredPermissionsList();
            if (requiredPermissionsList != null && !requiredPermissionsList.isEmpty()) {
                for (final StatementPermission statementPermission : requiredPermissionsList) {
                    if (!(statementPermission instanceof StatementSchemaPermission)) {
                        if (statementPermission instanceof StatementRolePermission) {
                            continue;
                        }
                        final PermissionsDescriptor permissionDescriptor = statementPermission.getPermissionDescriptor(currentUserId, dataDictionary);
                        if (permissionDescriptor == null) {
                            PermissionsDescriptor permissionsDescriptor = statementPermission.getPermissionDescriptor("PUBLIC", dataDictionary);
                            boolean b = false;
                            if (permissionsDescriptor == null || (permissionsDescriptor instanceof ColPermsDescriptor && !((StatementColumnPermission)statementPermission).allColumnsCoveredByUserOrPUBLIC(currentUserId, dataDictionary))) {
                                b = true;
                                permissionsDescriptor = findRoleUsage(activation, statementPermission);
                            }
                            if (permissionsDescriptor.checkOwner(currentUserId)) {
                                continue;
                            }
                            dependencyManager.addDependency(dependent, permissionsDescriptor, languageConnectionContext.getContextManager());
                            if (!b) {
                                continue;
                            }
                            trackRoleDependency(activation, dependent, settableBoolean);
                        }
                        else {
                            if (permissionDescriptor.checkOwner(currentUserId)) {
                                continue;
                            }
                            dependencyManager.addDependency(dependent, permissionDescriptor, languageConnectionContext.getContextManager());
                            if (!(permissionDescriptor instanceof ColPermsDescriptor)) {
                                continue;
                            }
                            final StatementColumnPermission statementColumnPermission = (StatementColumnPermission)statementPermission;
                            final PermissionsDescriptor publiClevelColPermsDescriptor = statementColumnPermission.getPUBLIClevelColPermsDescriptor(currentUserId, dataDictionary);
                            if (publiClevelColPermsDescriptor != null && publiClevelColPermsDescriptor.getObjectID() != null) {
                                dependencyManager.addDependency(dependent, publiClevelColPermsDescriptor, languageConnectionContext.getContextManager());
                            }
                            if (statementColumnPermission.allColumnsCoveredByUserOrPUBLIC(currentUserId, dataDictionary)) {
                                continue;
                            }
                            trackRoleDependency(activation, dependent, settableBoolean);
                        }
                    }
                }
            }
        }
    }
    
    private boolean inProviderSet(final ProviderInfo[] array, final UUID obj) {
        if (array == null) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i].getObjectId().equals(obj)) {
                return true;
            }
        }
        return false;
    }
    
    protected void addColumnDependencies(final LanguageConnectionContext languageConnectionContext, final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final ColumnInfo columnInfo) throws StandardException {
        final ProviderInfo[] providers = columnInfo.providers;
        if (providers != null) {
            final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
            final ContextManager contextManager = languageConnectionContext.getContextManager();
            final int length = providers.length;
            final DefaultDescriptor defaultDescriptor = tableDescriptor.getColumnDescriptor(columnInfo.name).getDefaultDescriptor(dataDictionary);
            for (final ProviderInfo providerInfo : providers) {
                dependencyManager.addDependency(defaultDescriptor, (Provider)providerInfo.getDependableFinder().getDependable(dataDictionary, providerInfo.getObjectId()), contextManager);
            }
        }
    }
    
    protected void adjustUDTDependencies(final LanguageConnectionContext languageConnectionContext, final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final ColumnInfo[] array, final boolean b) throws StandardException {
        if (!b && array == null) {
            return;
        }
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        final int n = (array == null) ? 0 : array.length;
        final HashMap hashMap = new HashMap<String, AliasDescriptor>();
        final HashMap hashMap2 = new HashMap<String, AliasDescriptor>();
        final HashSet set = new HashSet<String>();
        final HashSet set2 = new HashSet<String>();
        for (int i = 0; i < n; ++i) {
            final ColumnInfo columnInfo = array[i];
            final AliasDescriptor aliasDescriptorForUDT = dataDictionary.getAliasDescriptorForUDT(transactionExecute, array[i].dataType);
            if (aliasDescriptorForUDT != null) {
                final String string = aliasDescriptorForUDT.getObjectID().toString();
                if (columnInfo.action == 0) {
                    set.add(columnInfo.name);
                    if (hashMap.get(string) == null) {
                        hashMap.put(string, aliasDescriptorForUDT);
                    }
                }
                else if (columnInfo.action == 1) {
                    set2.add(columnInfo.name);
                    hashMap2.put(string, aliasDescriptorForUDT);
                }
            }
        }
        if (!b && hashMap.size() == 0 && hashMap2.size() == 0) {
            return;
        }
        final ColumnDescriptorList columnDescriptorList = tableDescriptor.getColumnDescriptorList();
        for (int size = columnDescriptorList.size(), j = 0; j < size; ++j) {
            final ColumnDescriptor element = columnDescriptorList.elementAt(j);
            if (!set.contains(element.getColumnName())) {
                if (!set2.contains(element.getColumnName())) {
                    final AliasDescriptor aliasDescriptorForUDT2 = dataDictionary.getAliasDescriptorForUDT(transactionExecute, element.getType());
                    if (aliasDescriptorForUDT2 != null) {
                        final String string2 = aliasDescriptorForUDT2.getObjectID().toString();
                        if (b) {
                            hashMap2.put(string2, aliasDescriptorForUDT2);
                        }
                        else {
                            if (hashMap.get(string2) != null) {
                                hashMap.remove(string2);
                            }
                            if (hashMap2.get(string2) != null) {
                                hashMap2.remove(string2);
                            }
                        }
                    }
                }
            }
        }
        this.adjustUDTDependencies(languageConnectionContext, dataDictionary, tableDescriptor, hashMap, hashMap2);
    }
    
    private void adjustUDTDependencies(final LanguageConnectionContext languageConnectionContext, final DataDictionary dataDictionary, final Dependent dependent, final HashMap hashMap, final HashMap hashMap2) throws StandardException {
        if (hashMap.size() == 0 && hashMap2.size() == 0) {
            return;
        }
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final ContextManager contextManager = languageConnectionContext.getContextManager();
        final Iterator<AliasDescriptor> iterator = hashMap.values().iterator();
        while (iterator.hasNext()) {
            dependencyManager.addDependency(dependent, iterator.next(), contextManager);
        }
        final Iterator<AliasDescriptor> iterator2 = hashMap2.values().iterator();
        while (iterator2.hasNext()) {
            dataDictionary.dropStoredDependency(new DependencyDescriptor(dependent, iterator2.next()), transactionExecute);
        }
    }
    
    protected void adjustUDTDependencies(final LanguageConnectionContext languageConnectionContext, final DataDictionary dataDictionary, final AliasDescriptor aliasDescriptor, final boolean b) throws StandardException {
        RoutineAliasInfo routineAliasInfo = null;
        AggregateAliasInfo aggregateAliasInfo = null;
        switch (aliasDescriptor.getAliasType()) {
            case 'G': {
                aggregateAliasInfo = (AggregateAliasInfo)aliasDescriptor.getAliasInfo();
                break;
            }
            case 'F':
            case 'P': {
                routineAliasInfo = (RoutineAliasInfo)aliasDescriptor.getAliasInfo();
                break;
            }
            default: {
                return;
            }
        }
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        final HashMap<String, AliasDescriptor> hashMap = new HashMap<String, AliasDescriptor>();
        final HashMap<String, AliasDescriptor> hashMap2 = new HashMap<String, AliasDescriptor>();
        final HashMap<String, AliasDescriptor> hashMap3 = b ? hashMap : hashMap2;
        final TypeDescriptor typeDescriptor = (aggregateAliasInfo != null) ? aggregateAliasInfo.getReturnType() : routineAliasInfo.getReturnType();
        if (typeDescriptor != null) {
            final AliasDescriptor aliasDescriptorForUDT = dataDictionary.getAliasDescriptorForUDT(transactionExecute, DataTypeDescriptor.getType(typeDescriptor));
            if (aliasDescriptorForUDT != null) {
                hashMap3.put(aliasDescriptorForUDT.getObjectID().toString(), aliasDescriptorForUDT);
            }
        }
        if (typeDescriptor != null && typeDescriptor.isRowMultiSet()) {
            final TypeDescriptor[] rowTypes = typeDescriptor.getRowTypes();
            for (int length = rowTypes.length, i = 0; i < length; ++i) {
                final AliasDescriptor aliasDescriptorForUDT2 = dataDictionary.getAliasDescriptorForUDT(transactionExecute, DataTypeDescriptor.getType(rowTypes[i]));
                if (aliasDescriptorForUDT2 != null) {
                    hashMap3.put(aliasDescriptorForUDT2.getObjectID().toString(), aliasDescriptorForUDT2);
                }
            }
        }
        final TypeDescriptor[] array = (aggregateAliasInfo != null) ? new TypeDescriptor[] { aggregateAliasInfo.getForType() } : routineAliasInfo.getParameterTypes();
        if (array != null) {
            for (int length2 = array.length, j = 0; j < length2; ++j) {
                final AliasDescriptor aliasDescriptorForUDT3 = dataDictionary.getAliasDescriptorForUDT(transactionExecute, DataTypeDescriptor.getType(array[j]));
                if (aliasDescriptorForUDT3 != null) {
                    hashMap3.put(aliasDescriptorForUDT3.getObjectID().toString(), aliasDescriptorForUDT3);
                }
            }
        }
        this.adjustUDTDependencies(languageConnectionContext, dataDictionary, aliasDescriptor, hashMap, hashMap2);
    }
    
    private class SettableBoolean
    {
        boolean value;
        
        SettableBoolean() {
            this.value = false;
        }
        
        void set(final boolean value) {
            this.value = value;
        }
        
        boolean get() {
            return this.value;
        }
    }
}
