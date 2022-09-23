// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.store.raw.data.RawContainerHandle;
import org.apache.derby.iapi.store.raw.Loggable;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.PageKey;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;

public class ReclaimSpaceHelper
{
    public static int reclaimSpace(final BaseDataFileFactory baseDataFileFactory, final RawTransaction rawTransaction, final ReclaimSpace reclaimSpace) throws StandardException {
        if (reclaimSpace.reclaimWhat() == 1) {
            return reclaimContainer(baseDataFileFactory, rawTransaction, reclaimSpace);
        }
        final LockingPolicy lockingPolicy = rawTransaction.newLockingPolicy(1, 5, true);
        final ContainerHandle openContainerNW = openContainerNW(rawTransaction, lockingPolicy, reclaimSpace.getContainerId());
        if (openContainerNW == null) {
            rawTransaction.abort();
            if (reclaimSpace.incrAttempts() < 3) {
                return 2;
            }
            return 1;
        }
        else {
            if (reclaimSpace.reclaimWhat() == 2) {
                final Page pageNoWait = openContainerNW.getPageNoWait(reclaimSpace.getPageId().getPageNumber());
                if (pageNoWait != null) {
                    openContainerNW.removePage(pageNoWait);
                }
                rawTransaction.commit();
                return 1;
            }
            final RecordHandle headRowHandle = reclaimSpace.getHeadRowHandle();
            if (!lockingPolicy.lockRecordForWrite(rawTransaction, headRowHandle, false, false)) {
                rawTransaction.abort();
                if (reclaimSpace.incrAttempts() < 3) {
                    return 2;
                }
                return 1;
            }
            else {
                if (reclaimSpace.reclaimWhat() == 3) {
                    openContainerNW.compactRecord(headRowHandle);
                    rawTransaction.commitNoSync(1);
                    return 1;
                }
                final StoredPage storedPage = (StoredPage)openContainerNW.getPage(((PageKey)headRowHandle.getPageId()).getPageNumber());
                if (storedPage == null) {
                    rawTransaction.abort();
                    return 1;
                }
                try {
                    storedPage.removeOrphanedColumnChain(reclaimSpace, openContainerNW);
                }
                finally {
                    storedPage.unlatch();
                }
                rawTransaction.commitNoSync(1);
                return 1;
            }
        }
    }
    
    private static int reclaimContainer(final BaseDataFileFactory baseDataFileFactory, final RawTransaction rawTransaction, final ReclaimSpace reclaimSpace) throws StandardException {
        final RawContainerHandle openDroppedContainer = rawTransaction.openDroppedContainer(reclaimSpace.getContainerId(), rawTransaction.newLockingPolicy(2, 5, true));
        if (openDroppedContainer == null || openDroppedContainer.getContainerStatus() == 1 || openDroppedContainer.getContainerStatus() == 4) {
            if (openDroppedContainer != null) {
                openDroppedContainer.close();
            }
            rawTransaction.abort();
        }
        else {
            final ContainerOperation containerOperation = new ContainerOperation(openDroppedContainer, (byte)4);
            openDroppedContainer.preDirty(true);
            try {
                rawTransaction.logAndDo(containerOperation);
            }
            finally {
                openDroppedContainer.preDirty(false);
            }
            openDroppedContainer.close();
            rawTransaction.commit();
        }
        return 1;
    }
    
    private static ContainerHandle openContainerNW(final Transaction transaction, final LockingPolicy lockingPolicy, final ContainerKey containerKey) throws StandardException {
        ContainerHandle openContainer = null;
        try {
            openContainer = transaction.openContainer(containerKey, lockingPolicy, 132);
        }
        catch (StandardException ex) {
            if (!ex.isLockTimeout()) {
                throw ex;
            }
        }
        return openContainer;
    }
}
