// 
// Decompiled by Procyon v0.5.36
// 

package javax.transaction;

public interface Synchronization
{
    void beforeCompletion();
    
    void afterCompletion(final int p0);
}
