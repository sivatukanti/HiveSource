// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.ConstantAction;

class SetRoleConstantAction implements ConstantAction
{
    private final String roleName;
    private final int type;
    
    SetRoleConstantAction(final String roleName, final int type) {
        this.roleName = roleName;
        this.type = type;
    }
    
    public String toString() {
        return "SET ROLE " + ((this.type == 1 && this.roleName == null) ? "?" : this.roleName);
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        String anObject = this.roleName;
        languageConnectionContext.getCurrentUserId(activation);
        languageConnectionContext.getDataDictionary().getAuthorizationDatabaseOwner();
        if (!languageConnectionContext.getTransactionExecute().isIdle()) {
            throw StandardException.newException("25001.S.1");
        }
        if (this.type == 1) {
            final String string = activation.getParameterValueSet().getParameter(0).getString();
            if (string == null) {
                throw StandardException.newException("XCXA0.S");
            }
            anObject = IdUtil.parseRoleId(string);
        }
        Provider provider = null;
        try {
            final String currentRoleId = languageConnectionContext.getCurrentRoleId(activation);
            if (currentRoleId != null && !currentRoleId.equals(anObject)) {
                provider = dataDictionary.getRoleDefinitionDescriptor(currentRoleId);
                if (provider != null) {
                    dataDictionary.getDependencyManager().invalidateFor(provider, 48, languageConnectionContext);
                }
            }
            if (anObject != null) {
                provider = dataDictionary.getRoleDefinitionDescriptor(anObject);
                if (provider == null) {
                    throw StandardException.newException("0P000", anObject);
                }
                if (!languageConnectionContext.roleIsSettable(activation, anObject)) {
                    throw StandardException.newException("0P000.S.1", anObject);
                }
            }
        }
        finally {
            languageConnectionContext.userCommit();
        }
        languageConnectionContext.setCurrentRole(activation, (provider != null) ? anObject : null);
    }
}
