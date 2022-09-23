// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.RoleGrantDescriptor;
import java.util.Iterator;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import java.util.List;

class GrantRoleConstantAction extends DDLConstantAction
{
    private List roleNames;
    private List grantees;
    private final boolean withAdminOption = false;
    
    public GrantRoleConstantAction(final List roleNames, final List grantees) {
        this.roleNames = roleNames;
        this.grantees = grantees;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
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
                    throw StandardException.newException("4251A", "GRANT role");
                }
                final RoleGrantDescriptor roleGrantDescriptor = dataDictionary.getRoleGrantDescriptor(s, s2, currentUserId);
                if (roleGrantDescriptor != null) {}
                if (roleGrantDescriptor != null) {
                    continue;
                }
                if (dataDictionary.getRoleDefinitionDescriptor(s2) != null) {
                    this.checkCircularity(s, s2, currentUserId, transactionExecute, dataDictionary);
                }
                dataDictionary.addDescriptor(dataDescriptorGenerator.newRoleGrantDescriptor(dataDictionary.getUUIDFactory().createUUID(), s, s2, currentUserId, false, false), null, 19, false, transactionExecute);
            }
        }
    }
    
    private void checkCircularity(final String s, final String s2, final String s3, final TransactionController transactionController, final DataDictionary dataDictionary) throws StandardException {
        String next;
        while ((next = dataDictionary.createRoleClosureIterator(transactionController, s2, false).next()) != null) {
            if (s.equals(next)) {
                throw StandardException.newException("4251C", s, s2);
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
        return "GRANT " + sb.toString() + " TO: " + sb2.toString() + "\n";
    }
}
