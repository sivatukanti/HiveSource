// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.listener;

public interface DetachLifecycleListener extends InstanceLifecycleListener
{
    void preDetach(final InstanceLifecycleEvent p0);
    
    void postDetach(final InstanceLifecycleEvent p0);
}
