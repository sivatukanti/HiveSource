// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas.callback;

import javax.security.auth.callback.Callback;

public class ObjectCallback implements Callback
{
    protected Object _object;
    
    public void setObject(final Object o) {
        this._object = o;
    }
    
    public Object getObject() {
        return this._object;
    }
    
    public void clearObject() {
        this._object = null;
    }
}
