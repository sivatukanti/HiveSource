// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import org.datanucleus.state.CallbackHandler;

public class NullCallbackHandler implements CallbackHandler
{
    @Override
    public void setValidationListener(final CallbackHandler handler) {
    }
    
    @Override
    public void postCreate(final Object pc) {
    }
    
    @Override
    public void prePersist(final Object pc) {
    }
    
    @Override
    public void preStore(final Object pc) {
    }
    
    @Override
    public void postStore(final Object pc) {
    }
    
    @Override
    public void preClear(final Object pc) {
    }
    
    @Override
    public void postClear(final Object pc) {
    }
    
    @Override
    public void preDelete(final Object pc) {
    }
    
    @Override
    public void postDelete(final Object pc) {
    }
    
    @Override
    public void preDirty(final Object pc) {
    }
    
    @Override
    public void postDirty(final Object pc) {
    }
    
    @Override
    public void postLoad(final Object pc) {
    }
    
    @Override
    public void postRefresh(final Object pc) {
    }
    
    @Override
    public void preDetach(final Object pc) {
    }
    
    @Override
    public void postDetach(final Object pc, final Object detachedPC) {
    }
    
    @Override
    public void preAttach(final Object detachedPC) {
    }
    
    @Override
    public void postAttach(final Object pc, final Object detachedPC) {
    }
    
    @Override
    public void addListener(final Object listener, final Class[] classes) {
    }
    
    @Override
    public void removeListener(final Object listener) {
    }
    
    @Override
    public void close() {
    }
}
