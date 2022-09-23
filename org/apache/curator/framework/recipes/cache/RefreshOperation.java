// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.cache;

class RefreshOperation implements Operation
{
    private final PathChildrenCache cache;
    private final PathChildrenCache.RefreshMode mode;
    
    RefreshOperation(final PathChildrenCache cache, final PathChildrenCache.RefreshMode mode) {
        this.cache = cache;
        this.mode = mode;
    }
    
    @Override
    public void invoke() throws Exception {
        this.cache.refresh(this.mode);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final RefreshOperation that = (RefreshOperation)o;
        return this.mode == that.mode;
    }
    
    @Override
    public int hashCode() {
        return this.mode.hashCode();
    }
    
    @Override
    public String toString() {
        return "RefreshOperation(" + this.mode + "){}";
    }
}
