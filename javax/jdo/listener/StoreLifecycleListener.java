// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.listener;

public interface StoreLifecycleListener extends InstanceLifecycleListener
{
    void preStore(final InstanceLifecycleEvent p0);
    
    void postStore(final InstanceLifecycleEvent p0);
}
