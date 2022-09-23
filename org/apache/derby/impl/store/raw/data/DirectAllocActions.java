// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;

public class DirectAllocActions implements AllocationActions
{
    public void actionAllocatePage(final RawTransaction rawTransaction, final BasePage basePage, final long n, final int n2, final int n3) throws StandardException {
        ((AllocPage)basePage).setPageStatus(null, n, n2);
    }
    
    public void actionChainAllocPage(final RawTransaction rawTransaction, final BasePage basePage, final long n, final long n2) throws StandardException {
        ((AllocPage)basePage).chainNextAllocPage(null, n, n2);
    }
    
    public void actionCompressSpaceOperation(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2) throws StandardException {
        ((AllocPage)basePage).compressSpace(null, n, n2);
    }
}
