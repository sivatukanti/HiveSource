// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query.cache;

import java.util.HashMap;
import org.datanucleus.NucleusContext;

public class StrongQueryDatastoreCompilationCache extends AbstractQueryDatastoreCompilationCache
{
    public StrongQueryDatastoreCompilationCache(final NucleusContext nucleusCtx) {
        this.cache = new HashMap<String, Object>();
    }
}
