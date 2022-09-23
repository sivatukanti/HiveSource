// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.listener;

public interface ClearLifecycleListener extends InstanceLifecycleListener
{
    void preClear(final InstanceLifecycleEvent p0);
    
    void postClear(final InstanceLifecycleEvent p0);
}
