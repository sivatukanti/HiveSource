// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

class ConcurrentTransactionMapFactory extends TransactionMapFactory
{
    @Override
    Map newMap() {
        return new ConcurrentHashMap();
    }
    
    @Override
    void visitEntries(final Map map, final TransactionTable.EntryVisitor entryVisitor) {
        final Iterator<TransactionTableEntry> iterator = map.values().iterator();
        while (iterator.hasNext() && entryVisitor.visit(iterator.next())) {}
    }
}
