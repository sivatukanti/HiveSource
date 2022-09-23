// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query.cache;

import java.util.Map;
import org.datanucleus.util.SoftValueMap;
import org.datanucleus.NucleusContext;

public class SoftQueryDatastoreCompilationCache extends AbstractQueryDatastoreCompilationCache
{
    public SoftQueryDatastoreCompilationCache(final NucleusContext nucleusCtx) {
        this.cache = (Map<String, Object>)new SoftValueMap();
    }
}
