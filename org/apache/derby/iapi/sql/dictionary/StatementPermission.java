// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;

public abstract class StatementPermission
{
    StatementPermission() {
    }
    
    public abstract void check(final LanguageConnectionContext p0, final boolean p1, final Activation p2) throws StandardException;
    
    public abstract PermissionsDescriptor getPermissionDescriptor(final String p0, final DataDictionary p1) throws StandardException;
    
    public boolean isCorrectPermission(final PermissionsDescriptor permissionsDescriptor) throws StandardException {
        return false;
    }
    
    public PrivilegedSQLObject getPrivilegedObject(final DataDictionary dataDictionary) throws StandardException {
        return null;
    }
    
    public String getObjectType() {
        return null;
    }
    
    public void genericCheck(final LanguageConnectionContext languageConnectionContext, final boolean b, final Activation activation, final String s) throws StandardException {
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        languageConnectionContext.getTransactionExecute();
        final ExecPreparedStatement preparedStatement = activation.getPreparedStatement();
        PermissionsDescriptor permissionsDescriptor = this.getPermissionDescriptor(languageConnectionContext.getCurrentUserId(activation), dataDictionary);
        if (!this.isCorrectPermission(permissionsDescriptor)) {
            permissionsDescriptor = this.getPermissionDescriptor("PUBLIC", dataDictionary);
        }
        if (this.isCorrectPermission(permissionsDescriptor)) {
            return;
        }
        int n = 0;
        final String currentRoleId = languageConnectionContext.getCurrentRoleId(activation);
        if (currentRoleId != null) {
            final String authorizationDatabaseOwner = dataDictionary.getAuthorizationDatabaseOwner();
            RoleGrantDescriptor roleGrantDescriptor = dataDictionary.getRoleGrantDescriptor(currentRoleId, languageConnectionContext.getCurrentUserId(activation), authorizationDatabaseOwner);
            if (roleGrantDescriptor == null) {
                roleGrantDescriptor = dataDictionary.getRoleGrantDescriptor(currentRoleId, "PUBLIC", authorizationDatabaseOwner);
            }
            if (roleGrantDescriptor == null) {
                languageConnectionContext.setCurrentRole(activation, null);
            }
            else {
                String next;
                for (RoleClosureIterator roleClosureIterator = dataDictionary.createRoleClosureIterator(activation.getTransactionController(), currentRoleId, true); n == 0 && (next = roleClosureIterator.next()) != null; n = 1) {
                    if (this.isCorrectPermission(this.getPermissionDescriptor(next, dataDictionary))) {}
                }
            }
            if (n != 0) {
                final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
                final RoleGrantDescriptor roleDefinitionDescriptor = dataDictionary.getRoleDefinitionDescriptor(currentRoleId);
                final ContextManager contextManager = languageConnectionContext.getContextManager();
                dependencyManager.addDependency(preparedStatement, roleDefinitionDescriptor, contextManager);
                dependencyManager.addDependency(activation, roleDefinitionDescriptor, contextManager);
            }
        }
        if (n != 0) {
            return;
        }
        final PrivilegedSQLObject privilegedObject = this.getPrivilegedObject(dataDictionary);
        if (privilegedObject == null) {
            throw StandardException.newException("4250E", this.getObjectType());
        }
        final SchemaDescriptor schemaDescriptor = privilegedObject.getSchemaDescriptor();
        if (schemaDescriptor == null) {
            throw StandardException.newException("4250E", "SCHEMA");
        }
        throw StandardException.newException(b ? "42505" : "42504", languageConnectionContext.getCurrentUserId(activation), s, this.getObjectType(), schemaDescriptor.getSchemaName(), privilegedObject.getName());
    }
}
