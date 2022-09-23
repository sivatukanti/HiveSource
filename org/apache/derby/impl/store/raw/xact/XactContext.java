// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.store.raw.RawStoreFactory;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.services.context.ContextImpl;

final class XactContext extends ContextImpl
{
    private RawTransaction xact;
    private RawStoreFactory factory;
    private boolean abortAll;
    
    XactContext(final ContextManager contextManager, final String s, final Xact xact, final boolean abortAll, final RawStoreFactory factory) {
        super(contextManager, s);
        this.xact = xact;
        this.abortAll = abortAll;
        this.factory = factory;
        xact.xc = this;
    }
    
    public void cleanupOnError(final Throwable t) throws StandardException {
        boolean b = false;
        if (t instanceof StandardException) {
            final StandardException ex = (StandardException)t;
            if (this.abortAll) {
                if (ex.getSeverity() < 30000) {
                    throw StandardException.newException("40XT5", t);
                }
                b = true;
            }
            else {
                if (ex.getSeverity() < 30000) {
                    return;
                }
                if (ex.getSeverity() >= 40000) {
                    b = true;
                }
            }
        }
        else {
            b = true;
        }
        try {
            if (this.xact != null) {
                this.xact.abort();
            }
        }
        catch (StandardException ex2) {
            b = true;
            if (ex2.getSeverity() <= 40000 && ex2.getSeverity() >= ((StandardException)t).getSeverity()) {
                throw this.factory.markCorrupt(StandardException.newException("XSTB0.M", ex2));
            }
        }
        finally {
            if (b) {
                this.xact.close();
                this.xact = null;
            }
        }
    }
    
    RawTransaction getTransaction() {
        return this.xact;
    }
    
    RawStoreFactory getFactory() {
        return this.factory;
    }
    
    void substituteTransaction(final Xact xact) {
        final Xact xact2 = (Xact)this.xact;
        if (xact2.xc == this) {
            xact2.xc = null;
        }
        this.xact = xact;
        ((Xact)this.xact).xc = this;
    }
}
