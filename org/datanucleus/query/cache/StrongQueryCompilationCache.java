// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.cache;

import org.datanucleus.query.compiler.QueryCompilation;
import java.util.HashMap;
import org.datanucleus.NucleusContext;

public class StrongQueryCompilationCache extends AbstractQueryCompilationCache implements QueryCompilationCache
{
    public StrongQueryCompilationCache(final NucleusContext nucleusCtx) {
        this.cache = new HashMap<String, QueryCompilation>();
    }
}
