// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.io.StorageFile;
import java.io.IOException;
import org.apache.derby.iapi.util.Matchable;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.services.cache.Cacheable;

class TempRAFContainer extends RAFContainer
{
    protected int inUseCount;
    
    TempRAFContainer(final BaseDataFileFactory baseDataFileFactory) {
        super(baseDataFileFactory);
    }
    
    public Cacheable setIdentity(final Object o) throws StandardException {
        final ContainerKey containerKey = (ContainerKey)o;
        if (containerKey.getSegmentId() != -1L) {
            return ((FileContainer)this.dataFactory.newContainerObject()).setIdent(containerKey);
        }
        return super.setIdentity(containerKey);
    }
    
    public Cacheable createIdentity(final Object o, final Object o2) throws StandardException {
        final ContainerKey containerKey = (ContainerKey)o;
        if (containerKey.getSegmentId() != -1L) {
            return this.dataFactory.newContainerObject().createIdentity(containerKey, o2);
        }
        return this.createIdent(containerKey, o2);
    }
    
    public void removeContainer(final LogInstant logInstant, final boolean b) throws StandardException {
        this.pageCache.discard(this.identity);
        synchronized (this) {
            this.setDroppedState(true);
            this.setCommittedDropState(true);
            this.setDirty(false);
            this.needsSync = false;
        }
        this.removeFile(this.getFileName(this.identity, false, false, false));
    }
    
    protected int preAllocate(final long n, final int n2) {
        return 0;
    }
    
    protected void writePage(final long n, final byte[] array, final boolean b) throws IOException, StandardException {
        if (!this.getDroppedState()) {
            super.writePage(n, array, false);
        }
        this.needsSync = false;
    }
    
    StorageFile getFileName(final ContainerKey containerKey, final boolean b, final boolean b2, final boolean b3) {
        return this.privGetFileName(containerKey, b, b2, b3);
    }
    
    protected StorageFile privGetFileName(final ContainerKey containerKey, final boolean b, final boolean b2, final boolean b3) {
        return this.dataFactory.storageFactory.newStorageFile(this.dataFactory.storageFactory.getTempDir(), "T" + containerKey.getContainerId() + ".tmp");
    }
    
    public Page addPage(final BaseContainerHandle baseContainerHandle, final boolean b) throws StandardException {
        return this.newPage(baseContainerHandle, null, baseContainerHandle, b);
    }
    
    public void truncate(final BaseContainerHandle baseContainerHandle) throws StandardException {
        synchronized (this) {
            this.setDroppedState(true);
            this.setCommittedDropState(true);
            this.setDirty(false);
            this.needsSync = false;
        }
        while (!this.pageCache.discard(this.identity)) {}
        this.removeFile(this.getFileName(this.identity, false, true, false));
        this.createIdent(this.identity, this);
        this.addPage(baseContainerHandle, false).unlatch();
    }
    
    protected boolean use(final BaseContainerHandle baseContainerHandle, final boolean b, final boolean b2) throws StandardException {
        if (super.use(baseContainerHandle, b, b2)) {
            ++this.inUseCount;
            return true;
        }
        return false;
    }
    
    protected void letGo(final BaseContainerHandle baseContainerHandle) {
        --this.inUseCount;
        super.letGo(baseContainerHandle);
    }
    
    public boolean isSingleUser() {
        return this.inUseCount == 1;
    }
}
