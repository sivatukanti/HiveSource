// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.cache;

public class TreeCacheEvent
{
    private final Type type;
    private final ChildData data;
    
    public TreeCacheEvent(final Type type, final ChildData data) {
        this.type = type;
        this.data = data;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public ChildData getData() {
        return this.data;
    }
    
    @Override
    public String toString() {
        return TreeCacheEvent.class.getSimpleName() + "{" + "type=" + this.type + ", data=" + this.data + '}';
    }
    
    public enum Type
    {
        NODE_ADDED, 
        NODE_UPDATED, 
        NODE_REMOVED, 
        CONNECTION_SUSPENDED, 
        CONNECTION_RECONNECTED, 
        CONNECTION_LOST, 
        INITIALIZED;
    }
}
