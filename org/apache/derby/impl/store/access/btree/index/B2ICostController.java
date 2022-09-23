// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree.index;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.impl.store.access.btree.BTree;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.impl.store.access.btree.BTreeCostController;

public class B2ICostController extends BTreeCostController
{
    B2ICostController() {
    }
    
    void init(final TransactionManager transactionManager, final B2I b2I, final Transaction transaction) throws StandardException {
        super.init(transactionManager, b2I, transaction);
    }
}
