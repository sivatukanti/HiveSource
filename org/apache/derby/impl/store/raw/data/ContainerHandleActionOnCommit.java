// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.store.raw.ContainerKey;

public abstract class ContainerHandleActionOnCommit extends ContainerActionOnCommit
{
    public ContainerHandleActionOnCommit(final ContainerKey containerKey) {
        super(containerKey);
    }
    
    public void openContainerAndDoIt(final RawTransaction rawTransaction) {
        BaseContainerHandle baseContainerHandle = null;
        try {
            baseContainerHandle = (BaseContainerHandle)rawTransaction.openContainer(this.identity, null, 1028);
            if (baseContainerHandle != null) {
                try {
                    this.doIt(baseContainerHandle);
                }
                catch (StandardException observerException) {
                    rawTransaction.setObserverException(observerException);
                }
            }
        }
        catch (StandardException observerException2) {
            if (this.identity.getSegmentId() != -1L) {
                rawTransaction.setObserverException(observerException2);
            }
        }
        finally {
            if (baseContainerHandle != null) {
                baseContainerHandle.close();
            }
        }
    }
    
    protected abstract void doIt(final BaseContainerHandle p0) throws StandardException;
}
