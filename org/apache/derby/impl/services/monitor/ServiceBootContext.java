// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.monitor;

import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.context.ContextImpl;

final class ServiceBootContext extends ContextImpl
{
    ServiceBootContext(final ContextManager contextManager) {
        super(contextManager, "ServiceBoot");
    }
    
    public void cleanupOnError(final Throwable t) {
        this.popMe();
    }
    
    public boolean isLastHandler(final int n) {
        return n == 0 || n == 45000 || n == 50000;
    }
}
