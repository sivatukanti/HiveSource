// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.cache;

class EventOperation implements Operation
{
    private final PathChildrenCache cache;
    private final PathChildrenCacheEvent event;
    
    EventOperation(final PathChildrenCache cache, final PathChildrenCacheEvent event) {
        this.cache = cache;
        this.event = event;
    }
    
    @Override
    public void invoke() {
        this.cache.callListeners(this.event);
    }
    
    @Override
    public String toString() {
        return "EventOperation{event=" + this.event + '}';
    }
}
