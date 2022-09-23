// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.services.context.ContextManager;
import java.util.Enumeration;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ReferencedKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import java.util.Hashtable;
import org.apache.derby.iapi.sql.dictionary.CheckConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ForeignKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptorList;

class SetConstraintsConstantAction extends DDLConstantAction
{
    private boolean enable;
    private boolean unconditionallyEnforce;
    private ConstraintDescriptorList cdl;
    private UUID[] cuuids;
    private UUID[] tuuids;
    
    SetConstraintsConstantAction(final ConstraintDescriptorList cdl, final boolean enable, final boolean unconditionallyEnforce) {
        this.cdl = cdl;
        this.enable = enable;
        this.unconditionallyEnforce = unconditionallyEnforce;
    }
    
    public String toString() {
        return "SET CONSTRAINTS";
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        ConstraintDescriptorList list = this.getConstraintDescriptorList(dataDictionary);
        final int[] array = { 6 };
        dataDictionary.startWriting(languageConnectionContext);
        this.publishToTargets(activation);
        boolean b = false;
        if (list == null) {
            b = true;
            list = dataDictionary.getConstraintDescriptors(null);
        }
        Hashtable<UUID, ConstraintDescriptorList> hashtable = null;
        for (int size = list.size(), i = 0; i < size; ++i) {
            final ConstraintDescriptor element = list.elementAt(i);
            if (this.unconditionallyEnforce || (this.enable && !element.isEnabled())) {
                if (element instanceof ForeignKeyConstraintDescriptor) {
                    this.validateFKConstraint((ForeignKeyConstraintDescriptor)element, dataDictionary, transactionExecute, languageConnectionContext.getContextManager());
                }
                else if (element instanceof CheckConstraintDescriptor) {
                    final TableDescriptor tableDescriptor = element.getTableDescriptor();
                    if (hashtable == null) {
                        hashtable = new Hashtable<UUID, ConstraintDescriptorList>(10);
                    }
                    ConstraintDescriptorList value = hashtable.get(tableDescriptor.getUUID());
                    if (value == null) {
                        value = new ConstraintDescriptorList();
                        hashtable.put(tableDescriptor.getUUID(), value);
                    }
                    value.add((ForeignKeyConstraintDescriptor)element);
                }
                dependencyManager.invalidateFor(element.getTableDescriptor(), 20, languageConnectionContext);
                element.setEnabled();
                dataDictionary.updateConstraintDescriptor(element, element.getUUID(), array, transactionExecute);
            }
            if (!b && element instanceof ReferencedKeyConstraintDescriptor) {
                final ConstraintDescriptorList foreignKeyConstraints = ((ReferencedKeyConstraintDescriptor)element).getForeignKeyConstraints(3);
                for (int size2 = foreignKeyConstraints.size(), j = 0; j < size2; ++j) {
                    final ForeignKeyConstraintDescriptor foreignKeyConstraintDescriptor = (ForeignKeyConstraintDescriptor)foreignKeyConstraints.elementAt(j);
                    if (this.enable && !foreignKeyConstraintDescriptor.isEnabled()) {
                        dependencyManager.invalidateFor(foreignKeyConstraintDescriptor.getTableDescriptor(), 20, languageConnectionContext);
                        this.validateFKConstraint(foreignKeyConstraintDescriptor, dataDictionary, transactionExecute, languageConnectionContext.getContextManager());
                        foreignKeyConstraintDescriptor.setEnabled();
                        dataDictionary.updateConstraintDescriptor(foreignKeyConstraintDescriptor, foreignKeyConstraintDescriptor.getUUID(), array, transactionExecute);
                    }
                    else if (!this.enable && foreignKeyConstraintDescriptor.isEnabled()) {
                        dependencyManager.invalidateFor(foreignKeyConstraintDescriptor, 21, languageConnectionContext);
                        foreignKeyConstraintDescriptor.setDisabled();
                        dataDictionary.updateConstraintDescriptor(foreignKeyConstraintDescriptor, foreignKeyConstraintDescriptor.getUUID(), array, transactionExecute);
                    }
                }
            }
            if (!this.enable && element.isEnabled()) {
                dependencyManager.invalidateFor(element, 21, languageConnectionContext);
                element.setDisabled();
                dataDictionary.updateConstraintDescriptor(element, element.getUUID(), array, transactionExecute);
            }
        }
        this.validateAllCheckConstraints(languageConnectionContext, hashtable);
    }
    
    private void validateAllCheckConstraints(final LanguageConnectionContext languageConnectionContext, final Hashtable hashtable) throws StandardException {
        ConstraintDescriptor element = null;
        if (hashtable == null) {
            return;
        }
        final Enumeration<ConstraintDescriptorList> elements = hashtable.elements();
        while (elements.hasMoreElements()) {
            final ConstraintDescriptorList list = elements.nextElement();
            StringBuffer append = null;
            StringBuffer sb = null;
            for (int size = list.size(), i = 0; i < size; ++i) {
                element = list.elementAt(i);
                if (append == null) {
                    append = new StringBuffer("(").append(element.getConstraintText()).append(") ");
                    sb = new StringBuffer(element.getConstraintName());
                }
                else {
                    append.append(" AND (").append(element.getConstraintText()).append(") ");
                    sb.append(", ").append(element.getConstraintName());
                }
            }
            ConstraintConstantAction.validateConstraint(sb.toString(), append.toString(), element.getTableDescriptor(), languageConnectionContext, true);
        }
    }
    
    private void validateFKConstraint(final ForeignKeyConstraintDescriptor foreignKeyConstraintDescriptor, final DataDictionary dataDictionary, final TransactionController transactionController, final ContextManager contextManager) throws StandardException {
        final IndexRowGenerator indexDescriptor = foreignKeyConstraintDescriptor.getIndexConglomerateDescriptor(dataDictionary).getIndexDescriptor();
        final ExecIndexRow indexRowTemplate = indexDescriptor.getIndexRowTemplate();
        final TableDescriptor tableDescriptor = foreignKeyConstraintDescriptor.getTableDescriptor();
        indexDescriptor.getIndexRow(tableDescriptor.getEmptyExecRow(), this.getRowLocation(dataDictionary, tableDescriptor, transactionController), indexRowTemplate, null);
        ConstraintConstantAction.validateFKConstraint(transactionController, dataDictionary, foreignKeyConstraintDescriptor, foreignKeyConstraintDescriptor.getReferencedConstraint(), indexRowTemplate);
    }
    
    private RowLocation getRowLocation(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final TransactionController transactionController) throws StandardException {
        final ConglomerateController openConglomerate = transactionController.openConglomerate(tableDescriptor.getHeapConglomerateId(), false, 0, 6, 2);
        RowLocation rowLocationTemplate;
        try {
            rowLocationTemplate = openConglomerate.newRowLocationTemplate();
        }
        finally {
            openConglomerate.close();
        }
        return rowLocationTemplate;
    }
    
    private ConstraintDescriptorList getConstraintDescriptorList(final DataDictionary dataDictionary) throws StandardException {
        if (this.cdl != null) {
            return this.cdl;
        }
        if (this.tuuids == null) {
            return null;
        }
        this.cdl = new ConstraintDescriptorList();
        for (int i = 0; i < this.tuuids.length; ++i) {
            this.cdl.add(dataDictionary.getConstraintDescriptorById(dataDictionary.getTableDescriptor(this.tuuids[i]), this.cuuids[i]));
        }
        return this.cdl;
    }
    
    protected void publishToTargets(final Activation activation) throws StandardException {
    }
}
