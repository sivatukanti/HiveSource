// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.store.raw.data.DataFactory;
import org.apache.derby.iapi.store.raw.log.LogFactory;

public class InternalXact extends Xact
{
    protected InternalXact(final XactFactory xactFactory, final LogFactory logFactory, final DataFactory dataFactory, final DataValueFactory dataValueFactory) {
        super(xactFactory, logFactory, dataFactory, dataValueFactory, false, null, false);
        this.setPostComplete();
    }
    
    public int setSavePoint(final String s, final Object o) throws StandardException {
        throw StandardException.newException("40XT7");
    }
    
    public void checkLogicalOperationOk() throws StandardException {
        throw StandardException.newException("40XT7");
    }
    
    public boolean recoveryRollbackFirst() {
        return true;
    }
    
    protected void doComplete(final Integer n) throws StandardException {
        if (n.equals(InternalXact.ABORT)) {
            super.doComplete(n);
        }
    }
    
    protected void setIdleState() {
        super.setIdleState();
        if (this.countObservers() != 0) {
            try {
                super.setActiveState();
            }
            catch (StandardException ex) {}
        }
    }
}
