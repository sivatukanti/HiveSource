// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.error.StandardException;

class NoSpaceOnPage extends StandardException
{
    private final boolean onOverflowPage;
    
    protected NoSpaceOnPage(final boolean onOverflowPage) {
        super("nospc.U");
        this.onOverflowPage = onOverflowPage;
    }
    
    protected boolean onOverflowPage() {
        return this.onOverflowPage;
    }
}
