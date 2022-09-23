// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.cache;

import org.datanucleus.query.compiler.QueryCompilation;
import java.util.Map;
import org.datanucleus.util.SoftValueMap;
import org.datanucleus.NucleusContext;

public class SoftQueryCompilationCache extends AbstractQueryCompilationCache implements QueryCompilationCache
{
    public SoftQueryCompilationCache(final NucleusContext nucleusCtx) {
        this.cache = (Map<String, QueryCompilation>)new SoftValueMap();
    }
}
