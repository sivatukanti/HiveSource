// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.jdbc.AuthenticationService;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.impl.jdbc.authentication.BasicAuthenticationServiceImpl;
import org.apache.derby.iapi.sql.dictionary.RoleGrantDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;

class CreateRoleConstantAction extends DDLConstantAction
{
    private String roleName;
    
    public CreateRoleConstantAction(final String roleName) {
        this.roleName = roleName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        if (this.roleName.equals("PUBLIC")) {
            throw StandardException.newException("4251B");
        }
        final String currentUserId = languageConnectionContext.getCurrentUserId(activation);
        dataDictionary.startWriting(languageConnectionContext);
        final RoleGrantDescriptor roleDefinitionDescriptor = dataDictionary.getRoleDefinitionDescriptor(this.roleName);
        if (roleDefinitionDescriptor != null) {
            throw StandardException.newException("X0Y68.S", roleDefinitionDescriptor.getDescriptorType(), this.roleName);
        }
        if (this.knownUser(this.roleName, currentUserId, languageConnectionContext, dataDictionary, transactionExecute)) {
            throw StandardException.newException("X0Y68.S", "User", this.roleName);
        }
        dataDictionary.addDescriptor(dataDescriptorGenerator.newRoleGrantDescriptor(dataDictionary.getUUIDFactory().createUUID(), this.roleName, currentUserId, "_SYSTEM", true, true), null, 19, false, transactionExecute);
    }
    
    public String toString() {
        return "CREATE ROLE " + this.roleName;
    }
    
    private boolean knownUser(final String anObject, final String s, final LanguageConnectionContext languageConnectionContext, final DataDictionary dataDictionary, final TransactionController transactionController) throws StandardException {
        final AuthenticationService authenticationService = languageConnectionContext.getDatabase().getAuthenticationService();
        return s.equals(anObject) || (authenticationService instanceof BasicAuthenticationServiceImpl && PropertyUtil.existsBuiltinUser(transactionController, anObject)) || dataDictionary.existsGrantToAuthid(anObject, transactionController) || dataDictionary.existsSchemaOwnedBy(anObject, transactionController);
    }
}
