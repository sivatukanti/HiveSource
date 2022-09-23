// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;

public interface AllocationActions
{
    void actionAllocatePage(final RawTransaction p0, final BasePage p1, final long p2, final int p3, final int p4) throws StandardException;
    
    void actionChainAllocPage(final RawTransaction p0, final BasePage p1, final long p2, final long p3) throws StandardException;
    
    void actionCompressSpaceOperation(final RawTransaction p0, final BasePage p1, final int p2, final int p3) throws StandardException;
}
