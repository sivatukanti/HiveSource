// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.conglomerate;

import org.apache.derby.iapi.store.access.SpaceInfo;
import java.util.Properties;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.store.access.conglomerate.Conglomerate;

public abstract class OpenConglomerate
{
    private Conglomerate init_conglomerate;
    private TransactionManager init_xact_manager;
    private Transaction init_rawtran;
    private int init_openmode;
    private int init_lock_level;
    private DynamicCompiledOpenConglomInfo init_dynamic_info;
    private boolean init_hold;
    private LockingPolicy init_locking_policy;
    private boolean useUpdateLocks;
    private boolean forUpdate;
    private boolean getBaseTableLocks;
    private OpenConglomerateScratchSpace runtime_mem;
    private ContainerHandle container;
    
    protected abstract RowLocation newRowLocationTemplate() throws StandardException;
    
    public abstract int[] getFormatIds();
    
    public boolean latchPageAndRepositionScan(final RowPosition rowPosition) throws StandardException {
        boolean b = false;
        rowPosition.current_page = null;
        try {
            if (rowPosition.current_rh != null) {
                rowPosition.current_page = this.container.getPage(rowPosition.current_rh.getPageNumber());
            }
        }
        catch (Throwable t) {}
        if (rowPosition.current_page != null) {
            try {
                rowPosition.current_slot = rowPosition.current_page.getSlotNumber(rowPosition.current_rh);
            }
            catch (StandardException ex) {
                b = true;
                rowPosition.current_slot = rowPosition.current_page.getNextSlotNumber(rowPosition.current_rh);
                if (rowPosition.current_slot == -1) {
                    rowPosition.current_page.unlatch();
                    rowPosition.current_page = null;
                }
                else {
                    --rowPosition.current_slot;
                }
            }
        }
        if (rowPosition.current_page == null) {
            long n;
            if (rowPosition.current_rh != null) {
                n = rowPosition.current_rh.getPageNumber();
            }
            else {
                if (rowPosition.current_pageno == -1L) {
                    return false;
                }
                n = rowPosition.current_pageno;
            }
            rowPosition.current_page = this.container.getNextPage(n);
            rowPosition.current_slot = -1;
            rowPosition.current_pageno = -1L;
            b = true;
        }
        if (b) {
            rowPosition.current_rh = null;
        }
        return b;
    }
    
    public boolean latchPage(final RowPosition rowPosition) throws StandardException {
        rowPosition.current_page = null;
        try {
            rowPosition.current_page = this.container.getPage(rowPosition.current_rh.getPageNumber());
        }
        catch (Throwable t) {}
        if (rowPosition.current_page != null) {
            try {
                rowPosition.current_slot = rowPosition.current_page.getSlotNumber(rowPosition.current_rh);
                return true;
            }
            catch (Throwable t2) {
                rowPosition.current_page.unlatch();
                rowPosition.current_page = null;
            }
        }
        return false;
    }
    
    public boolean lockPositionForRead(final RowPosition rowPosition, final RowPosition rowPosition2, final boolean b, final boolean b2) throws StandardException {
        if (rowPosition.current_rh == null) {
            rowPosition.current_rh = rowPosition.current_page.getRecordHandleAtSlot(rowPosition.current_slot);
        }
        final boolean lockRecordForRead = this.container.getLockingPolicy().lockRecordForRead(this.init_rawtran, this.container, rowPosition.current_rh, false, this.forUpdate);
        if (!lockRecordForRead) {
            rowPosition.current_page.unlatch();
            rowPosition.current_page = null;
            if (rowPosition2 != null) {
                rowPosition2.current_page.unlatch();
                rowPosition2.current_page = null;
            }
            if (!b2) {
                throw StandardException.newException("40XL1");
            }
            this.container.getLockingPolicy().lockRecordForRead(this.init_rawtran, this.container, rowPosition.current_rh, true, this.forUpdate);
            if (b) {
                if (this.latchPageAndRepositionScan(rowPosition) && rowPosition.current_slot != -1) {
                    rowPosition.positionAtNextSlot();
                    this.lockPositionForRead(rowPosition, rowPosition2, true, true);
                }
            }
            else {
                this.latchPage(rowPosition);
            }
        }
        return lockRecordForRead;
    }
    
    public boolean lockPositionForWrite(final RowPosition rowPosition, final boolean b) throws StandardException {
        if (rowPosition.current_rh == null) {
            rowPosition.current_rh = rowPosition.current_page.fetchFromSlot(null, rowPosition.current_slot, RowUtil.EMPTY_ROW, RowUtil.EMPTY_ROW_FETCH_DESCRIPTOR, true);
        }
        final boolean lockRecordForWrite = this.container.getLockingPolicy().lockRecordForWrite(this.init_rawtran, rowPosition.current_rh, false, false);
        if (!lockRecordForWrite) {
            rowPosition.current_page.unlatch();
            rowPosition.current_page = null;
            if (!b) {
                throw StandardException.newException("40XL1");
            }
            this.container.getLockingPolicy().lockRecordForWrite(this.init_rawtran, rowPosition.current_rh, false, true);
            this.latchPage(rowPosition);
        }
        return lockRecordForWrite;
    }
    
    public void unlockPositionAfterRead(final RowPosition rowPosition) throws StandardException {
        if (!this.isClosed()) {
            this.container.getLockingPolicy().unlockRecordAfterRead(this.init_rawtran, this.container, rowPosition.current_rh, this.forUpdate, rowPosition.current_rh_qualified);
        }
    }
    
    public Properties getInternalTablePropertySet(final Properties properties) throws StandardException {
        final Properties rawStorePropertySet = ConglomerateUtil.createRawStorePropertySet(properties);
        this.getTableProperties(rawStorePropertySet);
        return rawStorePropertySet;
    }
    
    public void getTableProperties(final Properties properties) throws StandardException {
        this.container.getContainerProperties(properties);
    }
    
    public final TransactionManager getXactMgr() {
        return this.init_xact_manager;
    }
    
    public final Transaction getRawTran() {
        return this.init_rawtran;
    }
    
    public final ContainerHandle getContainer() {
        return this.container;
    }
    
    public final int getOpenMode() {
        return this.init_openmode;
    }
    
    public final Conglomerate getConglomerate() {
        return this.init_conglomerate;
    }
    
    public final boolean getHold() {
        return this.init_hold;
    }
    
    public final boolean isForUpdate() {
        return this.forUpdate;
    }
    
    public final boolean isClosed() {
        return this.container == null;
    }
    
    public final boolean isUseUpdateLocks() {
        return this.useUpdateLocks;
    }
    
    public final OpenConglomerateScratchSpace getRuntimeMem() {
        return this.runtime_mem;
    }
    
    public void checkConsistency() throws StandardException {
    }
    
    public void debugConglomerate() throws StandardException {
    }
    
    public SpaceInfo getSpaceInfo() throws StandardException {
        return this.container.getSpaceInfo();
    }
    
    protected boolean isKeyed() {
        return false;
    }
    
    protected boolean isTableLocked() {
        return this.init_lock_level == 7;
    }
    
    public ContainerHandle init(final ContainerHandle containerHandle, final Conglomerate init_conglomerate, final int[] array, final int[] array2, final TransactionManager init_xact_manager, final Transaction init_rawtran, final boolean init_hold, final int init_openmode, final int init_lock_level, final LockingPolicy init_locking_policy, final DynamicCompiledOpenConglomInfo init_dynamic_info) throws StandardException {
        this.init_conglomerate = init_conglomerate;
        this.init_xact_manager = init_xact_manager;
        this.init_rawtran = init_rawtran;
        this.init_openmode = init_openmode;
        this.init_lock_level = init_lock_level;
        this.init_dynamic_info = init_dynamic_info;
        this.init_hold = init_hold;
        this.init_locking_policy = init_locking_policy;
        this.runtime_mem = (OpenConglomerateScratchSpace)((init_dynamic_info != null) ? init_dynamic_info : ((OpenConglomerateScratchSpace)init_conglomerate.getDynamicCompiledConglomInfo()));
        this.forUpdate = ((init_openmode & 0x4) != 0x0);
        this.useUpdateLocks = ((init_openmode & 0x1000) != 0x0);
        this.getBaseTableLocks = ((init_openmode & 0x2000) == 0x0);
        if (init_conglomerate.isTemporary()) {
            this.init_openmode |= 0x800;
        }
        if (!this.getBaseTableLocks) {
            this.init_locking_policy = null;
        }
        return this.container = ((containerHandle != null) ? containerHandle : init_rawtran.openContainer(init_conglomerate.getId(), this.init_locking_policy, this.init_openmode));
    }
    
    public ContainerHandle reopen() throws StandardException {
        if (this.container == null) {
            this.container = this.init_rawtran.openContainer(this.init_conglomerate.getId(), this.init_locking_policy, this.init_openmode);
        }
        return this.container;
    }
    
    public void close() throws StandardException {
        if (this.container != null) {
            this.container.close();
            this.container = null;
        }
    }
}
