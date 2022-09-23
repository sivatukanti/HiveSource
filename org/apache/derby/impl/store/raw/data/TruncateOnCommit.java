// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.error.StandardException;
import java.util.Observer;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import java.util.Observable;
import org.apache.derby.iapi.store.raw.ContainerKey;

public class TruncateOnCommit extends ContainerHandleActionOnCommit
{
    private boolean commitAsWell;
    
    public TruncateOnCommit(final ContainerKey containerKey, final boolean commitAsWell) {
        super(containerKey);
        this.commitAsWell = commitAsWell;
    }
    
    public void update(final Observable observable, final Object o) {
        if (o.equals(RawTransaction.ABORT) || o.equals(RawTransaction.SAVEPOINT_ROLLBACK) || (this.commitAsWell && o.equals(RawTransaction.COMMIT))) {
            this.openContainerAndDoIt((RawTransaction)observable);
        }
        if (o.equals(RawTransaction.COMMIT) || o.equals(RawTransaction.ABORT) || o.equals(this.identity)) {
            observable.deleteObserver(this);
        }
    }
    
    protected void doIt(final BaseContainerHandle baseContainerHandle) throws StandardException {
        baseContainerHandle.container.truncate(baseContainerHandle);
    }
    
    public boolean equals(final Object o) {
        return o instanceof TruncateOnCommit && ((TruncateOnCommit)o).commitAsWell == this.commitAsWell && super.equals(o);
    }
}
