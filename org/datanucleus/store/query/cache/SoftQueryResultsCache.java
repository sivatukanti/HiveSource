// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query.cache;

import java.util.List;
import java.util.Map;
import org.datanucleus.util.SoftValueMap;
import org.datanucleus.NucleusContext;

public class SoftQueryResultsCache extends AbstractQueryResultsCache
{
    public SoftQueryResultsCache(final NucleusContext ctx) {
        super(ctx);
        this.cache = (Map<String, List<Object>>)new SoftValueMap();
    }
}
