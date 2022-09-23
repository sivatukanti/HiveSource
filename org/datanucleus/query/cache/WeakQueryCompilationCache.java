// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.cache;

import org.datanucleus.query.compiler.QueryCompilation;
import java.util.Map;
import org.datanucleus.util.WeakValueMap;
import org.datanucleus.NucleusContext;

public class WeakQueryCompilationCache extends AbstractQueryCompilationCache implements QueryCompilationCache
{
    public WeakQueryCompilationCache(final NucleusContext nucleusCtx) {
        this.cache = (Map<String, QueryCompilation>)new WeakValueMap();
    }
}
