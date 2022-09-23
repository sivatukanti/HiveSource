// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.listener;

public interface DetachCallback
{
    void jdoPreDetach();
    
    void jdoPostDetach(final Object p0);
}
