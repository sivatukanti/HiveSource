// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$Maps;
import com.google.inject.spi.Dependency;
import java.util.Map;

final class InternalContext
{
    private Map<Object, ConstructionContext<?>> constructionContexts;
    private Dependency dependency;
    
    InternalContext() {
        this.constructionContexts = (Map<Object, ConstructionContext<?>>)$Maps.newHashMap();
    }
    
    public <T> ConstructionContext<T> getConstructionContext(final Object key) {
        ConstructionContext<T> constructionContext = (ConstructionContext<T>)this.constructionContexts.get(key);
        if (constructionContext == null) {
            constructionContext = new ConstructionContext<T>();
            this.constructionContexts.put(key, constructionContext);
        }
        return constructionContext;
    }
    
    public Dependency getDependency() {
        return this.dependency;
    }
    
    public Dependency setDependency(final Dependency dependency) {
        final Dependency previous = this.dependency;
        this.dependency = dependency;
        return previous;
    }
}
