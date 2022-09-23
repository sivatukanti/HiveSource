// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Loggable;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;

public class LoggableAllocActions implements AllocationActions
{
    public void actionAllocatePage(final RawTransaction rawTransaction, final BasePage basePage, final long n, final int n2, final int n3) throws StandardException {
        final AllocPageOperation allocPageOperation = new AllocPageOperation((AllocPage)basePage, n, n2, n3);
        basePage.preDirty();
        rawTransaction.logAndDo(allocPageOperation);
    }
    
    public void actionChainAllocPage(final RawTransaction rawTransaction, final BasePage basePage, final long n, final long n2) throws StandardException {
        final ChainAllocPageOperation chainAllocPageOperation = new ChainAllocPageOperation((AllocPage)basePage, n, n2);
        basePage.preDirty();
        rawTransaction.logAndDo(chainAllocPageOperation);
    }
    
    public void actionCompressSpaceOperation(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2) throws StandardException {
        CompressSpacePageOperation compressSpacePageOperation;
        if (rawTransaction.getLogFactory().checkVersion(10, 3, null)) {
            compressSpacePageOperation = new CompressSpacePageOperation((AllocPage)basePage, n, n2);
        }
        else {
            compressSpacePageOperation = new CompressSpacePageOperation10_2((AllocPage)basePage, n, n2);
        }
        basePage.preDirty();
        rawTransaction.logAndDo(compressSpacePageOperation);
    }
}
