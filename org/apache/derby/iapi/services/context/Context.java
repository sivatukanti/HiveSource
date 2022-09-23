// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.context;

import org.apache.derby.iapi.error.StandardException;

public interface Context
{
    ContextManager getContextManager();
    
    String getIdName();
    
    void cleanupOnError(final Throwable p0) throws StandardException;
    
    void pushMe();
    
    void popMe();
    
    boolean isLastHandler(final int p0);
}
