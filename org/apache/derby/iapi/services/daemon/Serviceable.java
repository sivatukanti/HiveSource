// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.daemon;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager;

public interface Serviceable
{
    public static final int DONE = 1;
    public static final int REQUEUE = 2;
    
    int performWork(final ContextManager p0) throws StandardException;
    
    boolean serviceASAP();
    
    boolean serviceImmediately();
}
