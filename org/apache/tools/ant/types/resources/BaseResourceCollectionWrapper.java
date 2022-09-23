// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.util.Iterator;
import org.apache.tools.ant.types.Resource;
import java.util.Collection;

public abstract class BaseResourceCollectionWrapper extends AbstractResourceCollectionWrapper
{
    private Collection<Resource> coll;
    
    public BaseResourceCollectionWrapper() {
        this.coll = null;
    }
    
    @Override
    protected Iterator<Resource> createIterator() {
        return this.cacheCollection().iterator();
    }
    
    @Override
    protected int getSize() {
        return this.cacheCollection().size();
    }
    
    protected abstract Collection<Resource> getCollection();
    
    private synchronized Collection<Resource> cacheCollection() {
        if (this.coll == null || !this.isCache()) {
            this.coll = this.getCollection();
        }
        return this.coll;
    }
}
