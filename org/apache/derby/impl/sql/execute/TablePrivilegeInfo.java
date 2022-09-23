// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.util.Iterator;
import org.apache.derby.iapi.sql.dictionary.TablePermsDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.PermissionsDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColPermsDescriptor;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import org.apache.derby.iapi.sql.dictionary.ViewDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import java.util.List;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;

public class TablePrivilegeInfo extends PrivilegeInfo
{
    public static final int SELECT_ACTION = 0;
    public static final int DELETE_ACTION = 1;
    public static final int INSERT_ACTION = 2;
    public static final int UPDATE_ACTION = 3;
    public static final int REFERENCES_ACTION = 4;
    public static final int TRIGGER_ACTION = 5;
    public static final int ACTION_COUNT = 6;
    private static final String YES_WITH_GRANT_OPTION = "Y";
    private static final String YES_WITHOUT_GRANT_OPTION = "y";
    private static final String NO = "N";
    private static final String[][] actionString;
    private TableDescriptor td;
    private boolean[] actionAllowed;
    private FormatableBitSet[] columnBitSets;
    private List descriptorList;
    
    public TablePrivilegeInfo(final TableDescriptor td, final boolean[] actionAllowed, final FormatableBitSet[] columnBitSets, final List descriptorList) {
        this.actionAllowed = actionAllowed;
        this.columnBitSets = columnBitSets;
        this.td = td;
        this.descriptorList = descriptorList;
    }
    
    protected void checkOwnership(final String s, final TableDescriptor tableDescriptor, final SchemaDescriptor schemaDescriptor, final DataDictionary dataDictionary, final LanguageConnectionContext languageConnectionContext, final boolean b) throws StandardException {
        super.checkOwnership(s, tableDescriptor, schemaDescriptor, dataDictionary);
        if (b) {
            this.checkPrivileges(s, tableDescriptor, schemaDescriptor, dataDictionary, languageConnectionContext);
        }
    }
    
    private void checkPrivileges(final String s, final TableDescriptor tableDescriptor, final SchemaDescriptor schemaDescriptor, final DataDictionary dataDictionary, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        if (s.equals(dataDictionary.getAuthorizationDatabaseOwner())) {
            return;
        }
        if (tableDescriptor.getTableType() == 2 && this.descriptorList != null) {
            final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
            for (int size = this.descriptorList.size(), i = 0; i < size; ++i) {
                SchemaDescriptor schemaDescriptor2 = null;
                final TupleDescriptor tupleDescriptor = this.descriptorList.get(i);
                if (tupleDescriptor instanceof TableDescriptor) {
                    schemaDescriptor2 = ((TableDescriptor)tupleDescriptor).getSchemaDescriptor();
                }
                else if (tupleDescriptor instanceof ViewDescriptor) {
                    schemaDescriptor2 = dataDictionary.getSchemaDescriptor(((ViewDescriptor)tupleDescriptor).getCompSchemaId(), transactionExecute);
                }
                else if (tupleDescriptor instanceof AliasDescriptor) {
                    schemaDescriptor2 = dataDictionary.getSchemaDescriptor(((AliasDescriptor)tupleDescriptor).getSchemaUUID(), transactionExecute);
                }
                if (schemaDescriptor2 != null && !s.equals(schemaDescriptor2.getAuthorizationId())) {
                    throw StandardException.newException("4250A", s, "grant", schemaDescriptor.getSchemaName(), tableDescriptor.getName());
                }
            }
        }
    }
    
    public void executeGrantRevoke(final Activation activation, final boolean b, final List list) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final String currentUserId = languageConnectionContext.getCurrentUserId(activation);
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        this.checkOwnership(currentUserId, this.td, this.td.getSchemaDescriptor(), dataDictionary, languageConnectionContext, b);
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        final TablePermsDescriptor tablePermsDescriptor = dataDescriptorGenerator.newTablePermsDescriptor(this.td, this.getPermString(0, false), this.getPermString(1, false), this.getPermString(2, false), this.getPermString(3, false), this.getPermString(4, false), this.getPermString(5, false), currentUserId);
        final ColPermsDescriptor[] array = new ColPermsDescriptor[this.columnBitSets.length];
        for (int i = 0; i < this.columnBitSets.length; ++i) {
            if (this.columnBitSets[i] != null || (!b && this.hasColumnPermissions(i) && this.actionAllowed[i])) {
                array[i] = dataDescriptorGenerator.newColPermsDescriptor(this.td, this.getActionString(i, false), this.columnBitSets[i], currentUserId);
            }
        }
        dataDictionary.startWriting(languageConnectionContext);
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            boolean b2 = false;
            final String s = iterator.next();
            if (tablePermsDescriptor != null && dataDictionary.addRemovePermissionsDescriptor(b, tablePermsDescriptor, s, transactionExecute)) {
                b2 = true;
                dataDictionary.getDependencyManager().invalidateFor(tablePermsDescriptor, 44, languageConnectionContext);
                dataDictionary.getDependencyManager().invalidateFor(this.td, 23, languageConnectionContext);
            }
            for (int j = 0; j < this.columnBitSets.length; ++j) {
                if (array[j] != null && dataDictionary.addRemovePermissionsDescriptor(b, array[j], s, transactionExecute)) {
                    b2 = true;
                    dataDictionary.getDependencyManager().invalidateFor(array[j], 44, languageConnectionContext);
                    dataDictionary.getDependencyManager().invalidateFor(this.td, 23, languageConnectionContext);
                }
            }
            this.addWarningIfPrivilegeNotRevoked(activation, b, b2, s);
        }
    }
    
    private String getPermString(final int n, final boolean b) {
        if (this.actionAllowed[n] && this.columnBitSets[n] == null) {
            return b ? "Y" : "y";
        }
        return "N";
    }
    
    private String getActionString(final int n, final boolean b) {
        return TablePrivilegeInfo.actionString[n][b];
    }
    
    private boolean hasColumnPermissions(final int n) {
        return n == 0 || n == 3 || n == 4;
    }
    
    static {
        actionString = new String[][] { { "s", "S" }, { "d", "D" }, { "i", "I" }, { "u", "U" }, { "r", "R" }, { "t", "T" } };
    }
}
