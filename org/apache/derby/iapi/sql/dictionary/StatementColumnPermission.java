// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.io.FormatableBitSet;

public class StatementColumnPermission extends StatementTablePermission
{
    private FormatableBitSet columns;
    
    public StatementColumnPermission(final UUID uuid, final int n, final FormatableBitSet columns) {
        super(uuid, n);
        this.columns = columns;
    }
    
    public FormatableBitSet getColumns() {
        return this.columns;
    }
    
    public boolean equals(final Object o) {
        return o instanceof StatementColumnPermission && this.columns.equals(((StatementColumnPermission)o).columns) && super.equals(o);
    }
    
    public void check(final LanguageConnectionContext languageConnectionContext, final boolean b, final Activation activation) throws StandardException {
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final ExecPreparedStatement preparedStatement = activation.getPreparedStatement();
        if (this.hasPermissionOnTable(languageConnectionContext, activation, b, preparedStatement)) {
            return;
        }
        final String currentUserId = languageConnectionContext.getCurrentUserId(activation);
        FormatableBitSet addPermittedColumns = null;
        if (!b) {
            addPermittedColumns = this.addPermittedColumns(dataDictionary, false, currentUserId, this.addPermittedColumns(dataDictionary, false, "PUBLIC", addPermittedColumns));
        }
        final FormatableBitSet addPermittedColumns2 = this.addPermittedColumns(dataDictionary, true, currentUserId, this.addPermittedColumns(dataDictionary, true, "PUBLIC", addPermittedColumns));
        if (this.privType == 8 && addPermittedColumns2 != null) {
            return;
        }
        final FormatableBitSet set = (FormatableBitSet)this.columns.clone();
        for (int i = set.anySetBit(); i >= 0; i = set.anySetBit(i)) {
            if (addPermittedColumns2 != null && addPermittedColumns2.get(i)) {
                set.clear(i);
            }
        }
        if (set.anySetBit() < 0) {
            return;
        }
        final String currentRoleId = languageConnectionContext.getCurrentRoleId(activation);
        if (currentRoleId != null) {
            final String authorizationDatabaseOwner = dataDictionary.getAuthorizationDatabaseOwner();
            RoleGrantDescriptor roleGrantDescriptor = dataDictionary.getRoleGrantDescriptor(currentRoleId, currentUserId, authorizationDatabaseOwner);
            if (roleGrantDescriptor == null) {
                roleGrantDescriptor = dataDictionary.getRoleGrantDescriptor(currentRoleId, "PUBLIC", authorizationDatabaseOwner);
            }
            if (roleGrantDescriptor == null) {
                languageConnectionContext.setCurrentRole(activation, null);
            }
            else {
                final RoleClosureIterator roleClosureIterator = dataDictionary.createRoleClosureIterator(activation.getTransactionController(), currentRoleId, true);
                String next;
                while (set.anySetBit() >= 0 && (next = roleClosureIterator.next()) != null) {
                    final FormatableBitSet tryRole = this.tryRole(languageConnectionContext, dataDictionary, b, next);
                    if (this.privType == 8 && tryRole != null) {
                        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
                        final RoleGrantDescriptor roleDefinitionDescriptor = dataDictionary.getRoleDefinitionDescriptor(currentRoleId);
                        final ContextManager contextManager = languageConnectionContext.getContextManager();
                        dependencyManager.addDependency(preparedStatement, roleDefinitionDescriptor, contextManager);
                        dependencyManager.addDependency(activation, roleDefinitionDescriptor, contextManager);
                        return;
                    }
                    for (int j = set.anySetBit(); j >= 0; j = set.anySetBit(j)) {
                        if (tryRole != null && tryRole.get(j)) {
                            set.clear(j);
                        }
                    }
                }
            }
        }
        final TableDescriptor tableDescriptor = this.getTableDescriptor(dataDictionary);
        if (this.privType == 8) {
            throw StandardException.newException(b ? "42501" : "42500", currentUserId, this.getPrivName(), tableDescriptor.getSchemaName(), tableDescriptor.getName());
        }
        final int anySetBit = set.anySetBit();
        if (anySetBit < 0) {
            final DependencyManager dependencyManager2 = dataDictionary.getDependencyManager();
            final RoleGrantDescriptor roleDefinitionDescriptor2 = dataDictionary.getRoleDefinitionDescriptor(currentRoleId);
            final ContextManager contextManager2 = languageConnectionContext.getContextManager();
            dependencyManager2.addDependency(preparedStatement, roleDefinitionDescriptor2, contextManager2);
            dependencyManager2.addDependency(activation, roleDefinitionDescriptor2, contextManager2);
            return;
        }
        final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(anySetBit + 1);
        if (columnDescriptor == null) {
            throw StandardException.newException("4250E", "column");
        }
        throw StandardException.newException(b ? "42503" : "42502", currentUserId, this.getPrivName(), columnDescriptor.getColumnName(), tableDescriptor.getSchemaName(), tableDescriptor.getName());
    }
    
    private FormatableBitSet addPermittedColumns(final DataDictionary dataDictionary, final boolean b, final String s, final FormatableBitSet set) throws StandardException {
        if (set != null && set.getNumBitsSet() == set.size()) {
            return set;
        }
        final ColPermsDescriptor columnPermissions = dataDictionary.getColumnPermissions(this.tableUUID, this.privType, false, s);
        if (columnPermissions != null) {
            if (set == null) {
                return columnPermissions.getColumns();
            }
            set.or(columnPermissions.getColumns());
        }
        return set;
    }
    
    public PermissionsDescriptor getPermissionDescriptor(final String s, final DataDictionary dataDictionary) throws StandardException {
        if (this.oneAuthHasPermissionOnTable(dataDictionary, s, false)) {
            return dataDictionary.getTablePermissions(this.tableUUID, s);
        }
        if (this.oneAuthHasPermissionOnTable(dataDictionary, "PUBLIC", false)) {
            return dataDictionary.getTablePermissions(this.tableUUID, "PUBLIC");
        }
        final ColPermsDescriptor columnPermissions = dataDictionary.getColumnPermissions(this.tableUUID, this.privType, false, s);
        if (columnPermissions != null && columnPermissions.getColumns() != null) {
            final FormatableBitSet columns = columnPermissions.getColumns();
            for (int i = this.columns.anySetBit(); i >= 0; i = this.columns.anySetBit(i)) {
                if (columns.get(i)) {
                    return columnPermissions;
                }
            }
        }
        return null;
    }
    
    public PermissionsDescriptor getPUBLIClevelColPermsDescriptor(final String s, final DataDictionary dataDictionary) throws StandardException {
        final FormatableBitSet columns = dataDictionary.getColumnPermissions(this.tableUUID, this.privType, false, s).getColumns();
        int n = 1;
        for (int n2 = this.columns.anySetBit(); n2 >= 0 && n != 0; n2 = this.columns.anySetBit(n2)) {
            if (!columns.get(n2)) {
                n = 0;
            }
        }
        if (n != 0) {
            return null;
        }
        return dataDictionary.getColumnPermissions(this.tableUUID, this.privType, false, "PUBLIC");
    }
    
    public boolean allColumnsCoveredByUserOrPUBLIC(final String s, final DataDictionary dataDictionary) throws StandardException {
        final FormatableBitSet columns = dataDictionary.getColumnPermissions(this.tableUUID, this.privType, false, s).getColumns();
        final FormatableBitSet set = (FormatableBitSet)this.columns.clone();
        boolean b = true;
        if (columns != null) {
            for (int i = set.anySetBit(); i >= 0; i = set.anySetBit(i)) {
                if (columns.get(i)) {
                    set.clear(i);
                }
            }
        }
        if (set.anySetBit() >= 0) {
            final FormatableBitSet columns2 = dataDictionary.getColumnPermissions(this.tableUUID, this.privType, false, "PUBLIC").getColumns();
            if (columns2 != null) {
                for (int j = set.anySetBit(); j >= 0; j = set.anySetBit(j)) {
                    if (columns2.get(j)) {
                        set.clear(j);
                    }
                }
            }
            if (set.anySetBit() >= 0) {
                b = false;
            }
        }
        return b;
    }
    
    private FormatableBitSet tryRole(final LanguageConnectionContext languageConnectionContext, final DataDictionary dataDictionary, final boolean b, final String s) throws StandardException {
        FormatableBitSet addPermittedColumns = null;
        if (!b) {
            addPermittedColumns = this.addPermittedColumns(dataDictionary, false, s, null);
        }
        return this.addPermittedColumns(dataDictionary, true, s, addPermittedColumns);
    }
    
    public String toString() {
        return "StatementColumnPermission: " + this.getPrivName() + " " + this.tableUUID + " columns: " + this.columns;
    }
}
