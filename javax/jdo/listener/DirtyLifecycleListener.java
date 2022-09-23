// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.listener;

public interface DirtyLifecycleListener extends InstanceLifecycleListener
{
    void preDirty(final InstanceLifecycleEvent p0);
    
    void postDirty(final InstanceLifecycleEvent p0);
}
