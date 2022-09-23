// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.depend.ProviderInfo;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import org.apache.derby.iapi.sql.dictionary.ViewDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.impl.sql.execute.TablePrivilegeInfo;
import org.apache.derby.impl.sql.execute.PrivilegeInfo;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;

public class TablePrivilegesNode extends QueryTreeNode
{
    private boolean[] actionAllowed;
    private ResultColumnList[] columnLists;
    private FormatableBitSet[] columnBitSets;
    private TableDescriptor td;
    private List descriptorList;
    
    public TablePrivilegesNode() {
        this.actionAllowed = new boolean[6];
        this.columnLists = new ResultColumnList[6];
        this.columnBitSets = new FormatableBitSet[6];
    }
    
    public void addAll() {
        for (int i = 0; i < 6; ++i) {
            this.actionAllowed[i] = true;
            this.columnLists[i] = null;
        }
    }
    
    public void addAction(final int n, final ResultColumnList list) {
        this.actionAllowed[n] = true;
        if (list == null) {
            this.columnLists[n] = null;
        }
        else if (this.columnLists[n] == null) {
            this.columnLists[n] = list;
        }
        else {
            this.columnLists[n].appendResultColumns(list, false);
        }
    }
    
    public void bind(final TableDescriptor td, final boolean b) throws StandardException {
        this.td = td;
        for (int i = 0; i < 6; ++i) {
            if (this.columnLists[i] != null) {
                this.columnBitSets[i] = this.columnLists[i].bindResultColumnsByName(td, null);
            }
            if (td.getTableType() == 2 && i != 0 && this.actionAllowed[i]) {
                throw StandardException.newException("42509", td.getQualifiedName());
            }
        }
        if (b && td.getTableType() == 2) {
            this.bindPrivilegesForView(td);
        }
    }
    
    public PrivilegeInfo makePrivilegeInfo() {
        return new TablePrivilegeInfo(this.td, this.actionAllowed, this.columnBitSets, this.descriptorList);
    }
    
    private void bindPrivilegesForView(final TableDescriptor tableDescriptor) throws StandardException {
        final DataDictionary dataDictionary = this.getLanguageConnectionContext().getDataDictionary();
        final ProviderInfo[] persistentProviderInfos = dataDictionary.getDependencyManager().getPersistentProviderInfos(dataDictionary.getViewDescriptor(tableDescriptor));
        this.descriptorList = new ArrayList();
        for (int length = persistentProviderInfos.length, i = 0; i < length; ++i) {
            final Provider provider = (Provider)persistentProviderInfos[i].getDependableFinder().getDependable(dataDictionary, persistentProviderInfos[i].getObjectId());
            if (provider instanceof TableDescriptor || provider instanceof ViewDescriptor || provider instanceof AliasDescriptor) {
                this.descriptorList.add(provider);
            }
        }
    }
}
