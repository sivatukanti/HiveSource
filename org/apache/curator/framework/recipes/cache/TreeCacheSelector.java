// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.cache;

public interface TreeCacheSelector
{
    boolean traverseChildren(final String p0);
    
    boolean acceptChild(final String p0);
}
