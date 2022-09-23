// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.sql.execute.ExecutionContext;
import org.apache.derby.iapi.services.context.ContextImpl;

class GenericExecutionContext extends ContextImpl implements ExecutionContext
{
    private ExecutionFactory execFactory;
    
    public ExecutionFactory getExecutionFactory() {
        return this.execFactory;
    }
    
    public void cleanupOnError(final Throwable t) throws StandardException {
        if (!(t instanceof StandardException)) {
            return;
        }
        final int severity = ((StandardException)t).getSeverity();
        if (severity >= 40000) {
            this.popMe();
            return;
        }
        if (severity > 20000) {
            return;
        }
    }
    
    GenericExecutionContext(final ContextManager contextManager, final ExecutionFactory execFactory) {
        super(contextManager, "ExecutionContext");
        this.execFactory = execFactory;
    }
}
