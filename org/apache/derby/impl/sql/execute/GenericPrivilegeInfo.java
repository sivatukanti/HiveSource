// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import java.util.Iterator;
import org.apache.derby.iapi.sql.dictionary.PermDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.PermissionsDescriptor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import java.util.List;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.PrivilegedSQLObject;

public class GenericPrivilegeInfo extends PrivilegeInfo
{
    private PrivilegedSQLObject _tupleDescriptor;
    private String _privilege;
    private boolean _restrict;
    
    public GenericPrivilegeInfo(final PrivilegedSQLObject tupleDescriptor, final String privilege, final boolean restrict) {
        this._tupleDescriptor = tupleDescriptor;
        this._privilege = privilege;
        this._restrict = restrict;
    }
    
    public void executeGrantRevoke(final Activation activation, final boolean b, final List list) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final String currentUserId = languageConnectionContext.getCurrentUserId(activation);
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        final SchemaDescriptor schemaDescriptor = this._tupleDescriptor.getSchemaDescriptor();
        final UUID uuid = this._tupleDescriptor.getUUID();
        final String objectTypeName = this._tupleDescriptor.getObjectTypeName();
        this.checkOwnership(currentUserId, (TupleDescriptor)this._tupleDescriptor, schemaDescriptor, dataDictionary);
        final PermDescriptor permDescriptor = dataDictionary.getDataDescriptorGenerator().newPermDescriptor(null, objectTypeName, uuid, this._privilege, currentUserId, null, false);
        dataDictionary.startWriting(languageConnectionContext);
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            boolean b2 = false;
            final String s = iterator.next();
            if (dataDictionary.addRemovePermissionsDescriptor(b, permDescriptor, s, transactionExecute)) {
                b2 = true;
                final int n = this._restrict ? 45 : 44;
                dataDictionary.getDependencyManager().invalidateFor(permDescriptor, n, languageConnectionContext);
                dataDictionary.getDependencyManager().invalidateFor(this._tupleDescriptor, n, languageConnectionContext);
            }
            this.addWarningIfPrivilegeNotRevoked(activation, b, b2, s);
        }
    }
}
