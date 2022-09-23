// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query.cache;

import java.util.Map;
import org.datanucleus.util.WeakValueMap;
import org.datanucleus.NucleusContext;

public class WeakQueryDatastoreCompilationCache extends AbstractQueryDatastoreCompilationCache
{
    public WeakQueryDatastoreCompilationCache(final NucleusContext nucleusCtx) {
        this.cache = (Map<String, Object>)new WeakValueMap();
    }
}
