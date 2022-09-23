// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.RoleGrantDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;

class DropRoleConstantAction extends DDLConstantAction
{
    private final String roleName;
    
    DropRoleConstantAction(final String roleName) {
        this.roleName = roleName;
    }
    
    public String toString() {
        return "DROP ROLE " + this.roleName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dataDictionary.startWriting(languageConnectionContext);
        final RoleGrantDescriptor roleDefinitionDescriptor = dataDictionary.getRoleDefinitionDescriptor(this.roleName);
        if (roleDefinitionDescriptor == null) {
            throw StandardException.newException("0P000", this.roleName);
        }
        String next;
        while ((next = dataDictionary.createRoleClosureIterator(activation.getTransactionController(), this.roleName, false).next()) != null) {
            dataDictionary.getDependencyManager().invalidateFor(dataDictionary.getRoleDefinitionDescriptor(next), 47, languageConnectionContext);
        }
        roleDefinitionDescriptor.drop(languageConnectionContext);
        dataDictionary.dropRoleGrantsByGrantee(this.roleName, transactionExecute);
        dataDictionary.dropRoleGrantsByName(this.roleName, transactionExecute);
        dataDictionary.dropAllPermsByGrantee(this.roleName, transactionExecute);
    }
}
