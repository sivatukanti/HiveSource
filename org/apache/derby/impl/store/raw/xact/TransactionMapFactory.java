// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import java.util.Iterator;
import java.util.Hashtable;
import java.util.Map;

class TransactionMapFactory
{
    Map newMap() {
        return new Hashtable();
    }
    
    void visitEntries(final Map map, final TransactionTable.EntryVisitor entryVisitor) {
        synchronized (map) {
            final Iterator<TransactionTableEntry> iterator = map.values().iterator();
            while (iterator.hasNext() && entryVisitor.visit(iterator.next())) {}
        }
    }
}
