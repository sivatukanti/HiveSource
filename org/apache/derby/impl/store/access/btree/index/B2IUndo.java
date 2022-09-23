// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree.index;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import org.apache.derby.impl.store.access.btree.BTree;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.impl.store.access.btree.SearchParameters;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.impl.store.access.btree.BTreeLockingPolicy;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.impl.store.access.btree.OpenBTree;
import org.apache.derby.impl.store.access.btree.ControlRow;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.LogicalUndoable;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;

public class B2IUndo implements LogicalUndo, Formatable
{
    public Page findUndo(final Transaction transaction, final LogicalUndoable logicalUndoable, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        ControlRow value = null;
        ControlRow controlRow = null;
        DataValueDescriptor[] template = null;
        DataValueDescriptor[] template2 = null;
        Page page = null;
        final ContainerHandle container = logicalUndoable.getContainer();
        final RecordHandle recordHandle = logicalUndoable.getRecordHandle();
        BTree bTree = null;
        try {
            value = ControlRow.get(container, 1L);
            bTree = value.getConglom(470);
            template = bTree.createTemplate(transaction);
            template2 = bTree.createTemplate(transaction);
        }
        finally {
            if (value != null) {
                value.release();
            }
        }
        logicalUndoable.restoreLoggedRow(template, limitObjectInput);
        boolean b = false;
        try {
            final OpenBTree openBTree = new OpenBTree();
            openBTree.init(null, null, logicalUndoable.getContainer(), transaction, false, 4, 5, null, bTree, null, null);
            controlRow = ControlRow.get(openBTree, recordHandle.getPageNumber());
            int compareIndexRowToKey = 1;
            if (controlRow.getPage().recordExists(recordHandle, true)) {
                controlRow.getPage().fetchFromSlot(null, controlRow.getPage().getSlotNumber(recordHandle), template2, null, true);
                compareIndexRowToKey = ControlRow.compareIndexRowToKey(template2, template, template.length, 1, openBTree.getColumnSortOrderInfo());
            }
            if (compareIndexRowToKey == 0) {
                page = controlRow.getPage();
            }
            else {
                final SearchParameters searchParameters = new SearchParameters(template, 1, template2, openBTree, false);
                controlRow.release();
                controlRow = null;
                controlRow = ControlRow.get(openBTree, 1L).search(searchParameters);
                if (!searchParameters.resultExact) {
                    throw StandardException.newException("XSCB5.S");
                }
                logicalUndoable.resetRecordHandle(controlRow.getPage().fetchFromSlot(null, searchParameters.resultSlot, new DataValueDescriptor[0], null, true));
                page = controlRow.getPage();
            }
            b = true;
        }
        finally {
            if (!b && controlRow != null) {
                controlRow.release();
            }
        }
        return page;
    }
    
    public int getTypeFormatId() {
        return 95;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
    }
}
