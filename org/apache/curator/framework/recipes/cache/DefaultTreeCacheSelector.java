// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.cache;

public class DefaultTreeCacheSelector implements TreeCacheSelector
{
    @Override
    public boolean traverseChildren(final String fullPath) {
        return true;
    }
    
    @Override
    public boolean acceptChild(final String fullPath) {
        return true;
    }
}
