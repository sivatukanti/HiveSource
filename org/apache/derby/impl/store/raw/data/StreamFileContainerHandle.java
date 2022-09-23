// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.util.Observable;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.catalog.UUID;
import java.util.Observer;
import org.apache.derby.iapi.store.raw.StreamContainerHandle;

final class StreamFileContainerHandle implements StreamContainerHandle, Observer
{
    private final UUID rawStoreId;
    protected final ContainerKey identity;
    protected boolean active;
    protected StreamFileContainer container;
    protected RawTransaction xact;
    private boolean hold;
    
    public StreamFileContainerHandle(final UUID rawStoreId, final RawTransaction xact, final ContainerKey identity, final boolean hold) {
        this.identity = identity;
        this.xact = xact;
        this.rawStoreId = rawStoreId;
        this.hold = hold;
    }
    
    public StreamFileContainerHandle(final UUID rawStoreId, final RawTransaction xact, final StreamFileContainer container, final boolean hold) {
        this.identity = container.getIdentity();
        this.xact = xact;
        this.rawStoreId = rawStoreId;
        this.hold = hold;
        this.container = container;
    }
    
    public void getContainerProperties(final Properties properties) throws StandardException {
        this.container.getContainerProperties(properties);
    }
    
    public boolean fetchNext(final DataValueDescriptor[] array) throws StandardException {
        return this.container.fetchNext(array);
    }
    
    public void close() {
        if (this.xact == null) {
            return;
        }
        this.active = false;
        this.container.close();
        this.container = null;
        this.xact.deleteObserver(this);
        this.xact = null;
    }
    
    public void removeContainer() throws StandardException {
        this.container.removeContainer();
    }
    
    public ContainerKey getId() {
        return this.identity;
    }
    
    public void update(final Observable observable, final Object o) {
        if (this.xact == null) {
            return;
        }
        if (o.equals(RawTransaction.COMMIT) || o.equals(RawTransaction.ABORT) || o.equals(this.identity)) {
            this.close();
            return;
        }
        if (o.equals(RawTransaction.SAVEPOINT_ROLLBACK)) {
            return;
        }
    }
    
    public boolean useContainer() throws StandardException {
        if (!this.container.use(this)) {
            this.container = null;
            return false;
        }
        this.active = true;
        if (!this.hold) {
            this.xact.addObserver(this);
            this.xact.addObserver(new DropOnCommit(this.identity, true));
        }
        return true;
    }
    
    public final RawTransaction getTransaction() {
        return this.xact;
    }
    
    public String toString() {
        return super.toString();
    }
}
