// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree.index;

import org.apache.derby.impl.store.access.btree.OpenBTree;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.Transaction;

class B2ITableLocking3 extends B2INoLocking
{
    public B2ITableLocking3(final Transaction transaction, final int n, final LockingPolicy lockingPolicy, final ConglomerateController conglomerateController, final OpenBTree openBTree) {
    }
    
    private B2ITableLocking3() {
    }
}
