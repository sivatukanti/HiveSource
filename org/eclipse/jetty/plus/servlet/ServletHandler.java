// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.servlet;

import org.eclipse.jetty.plus.annotation.LifeCycleCallbackCollection;
import org.eclipse.jetty.plus.annotation.InjectionCollection;

public class ServletHandler extends org.eclipse.jetty.servlet.ServletHandler
{
    private InjectionCollection _injections;
    private LifeCycleCallbackCollection _callbacks;
    
    public ServletHandler() {
        this._injections = null;
        this._callbacks = null;
    }
    
    public LifeCycleCallbackCollection getCallbacks() {
        return this._callbacks;
    }
    
    public void setCallbacks(final LifeCycleCallbackCollection callbacks) {
        this._callbacks = callbacks;
    }
    
    public InjectionCollection getInjections() {
        return this._injections;
    }
    
    public void setInjections(final InjectionCollection injections) {
        this._injections = injections;
    }
}
