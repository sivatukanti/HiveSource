// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import java.util.Iterator;
import org.apache.derby.iapi.sql.dictionary.RoutinePermsDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.PermissionsDescriptor;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import java.util.List;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;

public class RoutinePrivilegeInfo extends PrivilegeInfo
{
    private AliasDescriptor aliasDescriptor;
    
    public RoutinePrivilegeInfo(final AliasDescriptor aliasDescriptor) {
        this.aliasDescriptor = aliasDescriptor;
    }
    
    public void executeGrantRevoke(final Activation activation, final boolean b, final List list) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final String currentUserId = languageConnectionContext.getCurrentUserId(activation);
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        this.checkOwnership(currentUserId, this.aliasDescriptor, dataDictionary.getSchemaDescriptor(this.aliasDescriptor.getSchemaUUID(), transactionExecute), dataDictionary);
        final RoutinePermsDescriptor routinePermsDescriptor = dataDictionary.getDataDescriptorGenerator().newRoutinePermsDescriptor(this.aliasDescriptor, currentUserId);
        dataDictionary.startWriting(languageConnectionContext);
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            boolean b2 = false;
            final String s = iterator.next();
            if (dataDictionary.addRemovePermissionsDescriptor(b, routinePermsDescriptor, s, transactionExecute)) {
                b2 = true;
                dataDictionary.getDependencyManager().invalidateFor(routinePermsDescriptor, 45, languageConnectionContext);
                dataDictionary.getDependencyManager().invalidateFor(this.aliasDescriptor, 23, languageConnectionContext);
            }
            this.addWarningIfPrivilegeNotRevoked(activation, b, b2, s);
        }
    }
}
