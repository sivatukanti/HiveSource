// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.listener;

public interface AttachCallback
{
    void jdoPreAttach();
    
    void jdoPostAttach(final Object p0);
}
