// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.UUID;

public class StatementTablePermission extends StatementPermission
{
    UUID tableUUID;
    int privType;
    
    public StatementTablePermission(final UUID tableUUID, final int privType) {
        this.tableUUID = tableUUID;
        this.privType = privType;
    }
    
    public int getPrivType() {
        return this.privType;
    }
    
    public UUID getTableUUID() {
        return this.tableUUID;
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (this.getClass().equals(o.getClass())) {
            final StatementTablePermission statementTablePermission = (StatementTablePermission)o;
            return this.privType == statementTablePermission.privType && this.tableUUID.equals(statementTablePermission.tableUUID);
        }
        return false;
    }
    
    public int hashCode() {
        return this.privType + this.tableUUID.hashCode();
    }
    
    public void check(final LanguageConnectionContext languageConnectionContext, final boolean b, final Activation activation) throws StandardException {
        if (!this.hasPermissionOnTable(languageConnectionContext, activation, b, activation.getPreparedStatement())) {
            final TableDescriptor tableDescriptor = this.getTableDescriptor(languageConnectionContext.getDataDictionary());
            throw StandardException.newException(b ? "42501" : "42500", languageConnectionContext.getCurrentUserId(activation), this.getPrivName(), tableDescriptor.getSchemaName(), tableDescriptor.getName());
        }
    }
    
    protected TableDescriptor getTableDescriptor(final DataDictionary dataDictionary) throws StandardException {
        final TableDescriptor tableDescriptor = dataDictionary.getTableDescriptor(this.tableUUID);
        if (tableDescriptor == null) {
            throw StandardException.newException("4250E", "table");
        }
        return tableDescriptor;
    }
    
    protected boolean hasPermissionOnTable(final LanguageConnectionContext languageConnectionContext, final Activation activation, final boolean b, final ExecPreparedStatement execPreparedStatement) throws StandardException {
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final String currentUserId = languageConnectionContext.getCurrentUserId(activation);
        boolean oneAuthHasPermissionOnTable = this.oneAuthHasPermissionOnTable(dataDictionary, "PUBLIC", b) || this.oneAuthHasPermissionOnTable(dataDictionary, currentUserId, b);
        if (!oneAuthHasPermissionOnTable) {
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
                    String next;
                    for (RoleClosureIterator roleClosureIterator = dataDictionary.createRoleClosureIterator(activation.getTransactionController(), currentRoleId, true); !oneAuthHasPermissionOnTable && (next = roleClosureIterator.next()) != null; oneAuthHasPermissionOnTable = this.oneAuthHasPermissionOnTable(dataDictionary, next, b)) {}
                    if (oneAuthHasPermissionOnTable) {
                        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
                        final RoleGrantDescriptor roleDefinitionDescriptor = dataDictionary.getRoleDefinitionDescriptor(currentRoleId);
                        final ContextManager contextManager = languageConnectionContext.getContextManager();
                        dependencyManager.addDependency(execPreparedStatement, roleDefinitionDescriptor, contextManager);
                        dependencyManager.addDependency(activation, roleDefinitionDescriptor, contextManager);
                    }
                }
            }
        }
        return oneAuthHasPermissionOnTable;
    }
    
    protected boolean oneAuthHasPermissionOnTable(final DataDictionary dataDictionary, final String s, final boolean b) throws StandardException {
        final TablePermsDescriptor tablePermissions = dataDictionary.getTablePermissions(this.tableUUID, s);
        if (tablePermissions == null) {
            return false;
        }
        Object o = null;
        switch (this.privType) {
            case 0:
            case 8: {
                o = tablePermissions.getSelectPriv();
                break;
            }
            case 1: {
                o = tablePermissions.getUpdatePriv();
                break;
            }
            case 2: {
                o = tablePermissions.getReferencesPriv();
                break;
            }
            case 3: {
                o = tablePermissions.getInsertPriv();
                break;
            }
            case 4: {
                o = tablePermissions.getDeletePriv();
                break;
            }
            case 5: {
                o = tablePermissions.getTriggerPriv();
                break;
            }
        }
        return "Y".equals(o) || (!b && "y".equals(o));
    }
    
    public PermissionsDescriptor getPermissionDescriptor(final String s, final DataDictionary dataDictionary) throws StandardException {
        if (this.oneAuthHasPermissionOnTable(dataDictionary, s, false)) {
            return dataDictionary.getTablePermissions(this.tableUUID, s);
        }
        return null;
    }
    
    public String getPrivName() {
        switch (this.privType) {
            case 0:
            case 8: {
                return "SELECT";
            }
            case 1: {
                return "UPDATE";
            }
            case 2: {
                return "REFERENCES";
            }
            case 3: {
                return "INSERT";
            }
            case 4: {
                return "DELETE";
            }
            case 5: {
                return "TRIGGER";
            }
            default: {
                return "?";
            }
        }
    }
    
    public String toString() {
        return "StatementTablePermission: " + this.getPrivName() + " " + this.tableUUID;
    }
}
