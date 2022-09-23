// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

public interface CallbackHandler
{
    void setValidationListener(final CallbackHandler p0);
    
    void postCreate(final Object p0);
    
    void prePersist(final Object p0);
    
    void preStore(final Object p0);
    
    void postStore(final Object p0);
    
    void preClear(final Object p0);
    
    void postClear(final Object p0);
    
    void preDelete(final Object p0);
    
    void postDelete(final Object p0);
    
    void preDirty(final Object p0);
    
    void postDirty(final Object p0);
    
    void postLoad(final Object p0);
    
    void postRefresh(final Object p0);
    
    void preDetach(final Object p0);
    
    void postDetach(final Object p0, final Object p1);
    
    void preAttach(final Object p0);
    
    void postAttach(final Object p0, final Object p1);
    
    void addListener(final Object p0, final Class[] p1);
    
    void removeListener(final Object p0);
    
    void close();
}
