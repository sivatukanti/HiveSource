// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.RoleGrantDescriptor;
import java.util.Iterator;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import java.util.List;

class RevokeRoleConstantAction extends DDLConstantAction
{
    private List roleNames;
    private List grantees;
    private final boolean withAdminOption = false;
    
    public RevokeRoleConstantAction(final List roleNames, final List grantees) {
        this.roleNames = roleNames;
        this.grantees = grantees;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        languageConnectionContext.getTransactionExecute();
        final String currentUserId = languageConnectionContext.getCurrentUserId(activation);
        dataDictionary.startWriting(languageConnectionContext);
        for (final String s : this.roleNames) {
            if (s.equals("PUBLIC")) {
                throw StandardException.newException("4251B");
            }
            for (final String s2 : this.grantees) {
                if (dataDictionary.getRoleDefinitionDescriptor(s) == null) {
                    throw StandardException.newException("0P000", s);
                }
                if (!currentUserId.equals(languageConnectionContext.getDataDictionary().getAuthorizationDatabaseOwner())) {
                    throw StandardException.newException("4251A", "REVOKE role");
                }
                final RoleGrantDescriptor roleGrantDescriptor = dataDictionary.getRoleGrantDescriptor(s, s2, currentUserId);
                if (roleGrantDescriptor != null) {}
                if (roleGrantDescriptor != null) {
                    String next;
                    while ((next = dataDictionary.createRoleClosureIterator(activation.getTransactionController(), s, false).next()) != null) {
                        dataDictionary.getDependencyManager().invalidateFor(dataDictionary.getRoleDefinitionDescriptor(next), 47, languageConnectionContext);
                    }
                    roleGrantDescriptor.drop(languageConnectionContext);
                }
                else {
                    activation.addWarning(StandardException.newWarning("01007", s, s2));
                }
            }
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final Iterator<Object> iterator = this.roleNames.iterator();
        while (iterator.hasNext()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(iterator.next().toString());
        }
        final StringBuffer sb2 = new StringBuffer();
        final Iterator<Object> iterator2 = this.grantees.iterator();
        while (iterator2.hasNext()) {
            if (sb2.length() > 0) {
                sb2.append(", ");
            }
            sb2.append(iterator2.next().toString());
        }
        return "REVOKE " + sb.toString() + " FROM: " + sb2.toString() + "\n";
    }
}
