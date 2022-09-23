// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.util.Observer;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import java.util.Observable;
import org.apache.derby.iapi.store.raw.ContainerKey;

public class DropOnCommit extends ContainerActionOnCommit
{
    protected boolean isStreamContainer;
    
    public DropOnCommit(final ContainerKey containerKey) {
        super(containerKey);
        this.isStreamContainer = false;
    }
    
    public DropOnCommit(final ContainerKey containerKey, final boolean isStreamContainer) {
        super(containerKey);
        this.isStreamContainer = false;
        this.isStreamContainer = isStreamContainer;
    }
    
    public void update(final Observable observable, final Object o) {
        if (o.equals(RawTransaction.COMMIT) || o.equals(RawTransaction.ABORT)) {
            final RawTransaction rawTransaction = (RawTransaction)observable;
            try {
                if (this.isStreamContainer) {
                    rawTransaction.dropStreamContainer(this.identity.getSegmentId(), this.identity.getContainerId());
                }
                else {
                    rawTransaction.dropContainer(this.identity);
                }
            }
            catch (StandardException observerException) {
                rawTransaction.setObserverException(observerException);
            }
            observable.deleteObserver(this);
        }
    }
}
