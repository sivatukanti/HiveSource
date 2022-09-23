// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.context;

public abstract class ContextImpl implements Context
{
    private final String myIdName;
    private final ContextManager myContextManager;
    
    protected ContextImpl(final ContextManager myContextManager, final String myIdName) {
        this.myIdName = myIdName;
        (this.myContextManager = myContextManager).pushContext(this);
    }
    
    public final ContextManager getContextManager() {
        return this.myContextManager;
    }
    
    public final String getIdName() {
        return this.myIdName;
    }
    
    public final void pushMe() {
        this.getContextManager().pushContext(this);
    }
    
    public final void popMe() {
        this.getContextManager().popContext(this);
    }
    
    public boolean isLastHandler(final int n) {
        return false;
    }
    
    public StringBuffer appendErrorInfo() {
        return null;
    }
}
