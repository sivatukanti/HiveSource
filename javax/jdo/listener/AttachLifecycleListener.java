// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.listener;

public interface AttachLifecycleListener extends InstanceLifecycleListener
{
    void preAttach(final InstanceLifecycleEvent p0);
    
    void postAttach(final InstanceLifecycleEvent p0);
}
