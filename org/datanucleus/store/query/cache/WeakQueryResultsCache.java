// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query.cache;

import java.util.List;
import java.util.Map;
import org.datanucleus.util.WeakValueMap;
import org.datanucleus.NucleusContext;

public class WeakQueryResultsCache extends AbstractQueryResultsCache
{
    public WeakQueryResultsCache(final NucleusContext ctx) {
        super(ctx);
        this.cache = (Map<String, List<Object>>)new WeakValueMap();
    }
}
