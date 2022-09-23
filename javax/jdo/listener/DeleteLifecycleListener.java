// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.listener;

public interface DeleteLifecycleListener extends InstanceLifecycleListener
{
    void preDelete(final InstanceLifecycleEvent p0);
    
    void postDelete(final InstanceLifecycleEvent p0);
}
