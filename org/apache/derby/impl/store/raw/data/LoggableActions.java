// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Loggable;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;

public class LoggableActions implements PageActions
{
    public void actionDelete(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2, final boolean b, final LogicalUndo logicalUndo) throws StandardException {
        this.doAction(rawTransaction, basePage, new DeleteOperation(rawTransaction, basePage, n, n2, b, logicalUndo));
    }
    
    public int actionUpdate(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2, final Object[] array, final FormatableBitSet set, final int n3, final DynamicByteArrayOutputStream dynamicByteArrayOutputStream, final int n4, final RecordHandle recordHandle) throws StandardException {
        final UpdateOperation updateOperation = new UpdateOperation(rawTransaction, basePage, n, n2, array, set, n3, dynamicByteArrayOutputStream, n4, recordHandle);
        this.doAction(rawTransaction, basePage, updateOperation);
        return updateOperation.getNextStartColumn();
    }
    
    public void actionPurge(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2, final int[] array, final boolean b) throws StandardException {
        this.doAction(rawTransaction, basePage, new PurgeOperation(rawTransaction, basePage, n, n2, array, b));
    }
    
    public void actionUpdateField(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2, final int n3, final Object o, final LogicalUndo logicalUndo) throws StandardException {
        this.doAction(rawTransaction, basePage, new UpdateFieldOperation(rawTransaction, basePage, n, n2, n3, o, logicalUndo));
    }
    
    public int actionInsert(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2, final Object[] array, final FormatableBitSet set, final LogicalUndo logicalUndo, final byte b, final int n3, final boolean b2, final int n4, final DynamicByteArrayOutputStream dynamicByteArrayOutputStream, final int n5, final int n6) throws StandardException {
        final InsertOperation insertOperation = new InsertOperation(rawTransaction, basePage, n, n2, array, set, logicalUndo, b, n3, b2, n4, dynamicByteArrayOutputStream, n5, n6);
        this.doAction(rawTransaction, basePage, insertOperation);
        return insertOperation.getNextStartColumn();
    }
    
    public void actionCopyRows(final RawTransaction rawTransaction, final BasePage basePage, final BasePage basePage2, final int n, final int n2, final int n3, final int[] array) throws StandardException {
        this.doAction(rawTransaction, basePage, new CopyRowsOperation(rawTransaction, basePage, basePage2, n, n2, n3, array));
    }
    
    public void actionInvalidatePage(final RawTransaction rawTransaction, final BasePage basePage) throws StandardException {
        this.doAction(rawTransaction, basePage, new InvalidatePageOperation(basePage));
    }
    
    public void actionInitPage(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2, final long n3) throws StandardException {
        this.doAction(rawTransaction, basePage, new InitPageOperation(basePage, n, n2, n3));
    }
    
    public void actionShrinkReservedSpace(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2, final int n3, final int n4) throws StandardException {
        this.doAction(rawTransaction, basePage, new SetReservedSpaceOperation(basePage, n, n2, n3, n4));
    }
    
    private void doAction(final RawTransaction rawTransaction, final BasePage basePage, final Loggable loggable) throws StandardException {
        basePage.preDirty();
        rawTransaction.logAndDo(loggable);
    }
}
