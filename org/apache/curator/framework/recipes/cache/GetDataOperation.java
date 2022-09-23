// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.cache;

import org.apache.curator.utils.PathUtils;

class GetDataOperation implements Operation
{
    private final PathChildrenCache cache;
    private final String fullPath;
    
    GetDataOperation(final PathChildrenCache cache, final String fullPath) {
        this.cache = cache;
        this.fullPath = PathUtils.validatePath(fullPath);
    }
    
    @Override
    public void invoke() throws Exception {
        this.cache.getDataAndStat(this.fullPath);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final GetDataOperation that = (GetDataOperation)o;
        return this.fullPath.equals(that.fullPath);
    }
    
    @Override
    public int hashCode() {
        return this.fullPath.hashCode();
    }
    
    @Override
    public String toString() {
        return "GetDataOperation{fullPath='" + this.fullPath + '\'' + '}';
    }
}
