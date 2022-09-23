// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query.cache;

import java.util.List;
import java.util.HashMap;
import org.datanucleus.NucleusContext;

public class StrongQueryResultsCache extends AbstractQueryResultsCache
{
    public StrongQueryResultsCache(final NucleusContext ctx) {
        super(ctx);
        this.cache = new HashMap<String, List<Object>>();
    }
}
