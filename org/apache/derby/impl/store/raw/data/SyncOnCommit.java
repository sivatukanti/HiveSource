// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.error.StandardException;
import java.util.Observer;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import java.util.Observable;
import org.apache.derby.iapi.store.raw.ContainerKey;

public class SyncOnCommit extends ContainerHandleActionOnCommit
{
    public SyncOnCommit(final ContainerKey containerKey) {
        super(containerKey);
    }
    
    public void update(final Observable observable, final Object o) {
        if (o.equals(RawTransaction.COMMIT)) {
            this.openContainerAndDoIt((RawTransaction)observable);
        }
        if (o.equals(RawTransaction.COMMIT) || o.equals(RawTransaction.ABORT) || o.equals(this.identity)) {
            observable.deleteObserver(this);
        }
    }
    
    protected void doIt(final BaseContainerHandle baseContainerHandle) throws StandardException {
        baseContainerHandle.container.flushAll();
    }
}
