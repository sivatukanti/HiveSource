// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.conglomerate;

import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.ConglomerateController;

public abstract class GenericConglomerateController extends GenericController implements ConglomerateController
{
    public void close() throws StandardException {
        super.close();
        if (this.open_conglom != null && this.open_conglom.getXactMgr() != null) {
            this.open_conglom.getXactMgr().closeMe(this);
        }
    }
    
    public boolean closeForEndTransaction(final boolean b) throws StandardException {
        super.close();
        if (!this.open_conglom.getHold() || b) {
            if (this.open_conglom != null && this.open_conglom.getXactMgr() != null) {
                this.open_conglom.getXactMgr().closeMe(this);
            }
            return true;
        }
        return false;
    }
    
    public boolean delete(final RowLocation rowLocation) throws StandardException {
        if (this.open_conglom.isClosed()) {
            if (!this.open_conglom.getHold()) {
                throw StandardException.newException("XSCH6.S", this.open_conglom.getConglomerate().getId());
            }
            if (this.open_conglom.isClosed()) {
                this.open_conglom.reopen();
            }
        }
        final RowPosition get_scratch_row_position = this.open_conglom.getRuntimeMem().get_scratch_row_position();
        this.getRowPositionFromRowLocation(rowLocation, get_scratch_row_position);
        if (!this.open_conglom.latchPage(get_scratch_row_position)) {
            return false;
        }
        this.open_conglom.lockPositionForWrite(get_scratch_row_position, true);
        boolean b = true;
        if (get_scratch_row_position.current_page.isDeletedAtSlot(get_scratch_row_position.current_slot)) {
            b = false;
        }
        else {
            get_scratch_row_position.current_page.deleteAtSlot(get_scratch_row_position.current_slot, true, null);
            if (get_scratch_row_position.current_page.shouldReclaimSpace((get_scratch_row_position.current_page.getPageNumber() == 1L) ? 1 : 0, get_scratch_row_position.current_slot)) {
                this.queueDeletePostCommitWork(get_scratch_row_position);
            }
        }
        get_scratch_row_position.current_page.unlatch();
        return b;
    }
    
    public boolean fetch(final RowLocation rowLocation, final DataValueDescriptor[] array, final FormatableBitSet set) throws StandardException {
        if (this.open_conglom.isClosed()) {
            if (!this.open_conglom.getHold()) {
                throw StandardException.newException("XSCH6.S", this.open_conglom.getConglomerate().getId());
            }
            if (this.open_conglom.isClosed()) {
                this.open_conglom.reopen();
            }
        }
        final RowPosition get_scratch_row_position = this.open_conglom.getRuntimeMem().get_scratch_row_position();
        this.getRowPositionFromRowLocation(rowLocation, get_scratch_row_position);
        if (!this.open_conglom.latchPage(get_scratch_row_position)) {
            return false;
        }
        if (this.open_conglom.isForUpdate()) {
            this.open_conglom.lockPositionForWrite(get_scratch_row_position, true);
        }
        else {
            this.open_conglom.lockPositionForRead(get_scratch_row_position, null, false, true);
        }
        if (get_scratch_row_position.current_page == null) {
            return false;
        }
        final boolean b = get_scratch_row_position.current_page.fetchFromSlot(get_scratch_row_position.current_rh, get_scratch_row_position.current_slot, array, new FetchDescriptor(array.length, set, null), false) != null;
        if (!this.open_conglom.isForUpdate()) {
            this.open_conglom.unlockPositionAfterRead(get_scratch_row_position);
        }
        get_scratch_row_position.current_page.unlatch();
        return b;
    }
    
    public boolean fetch(final RowLocation rowLocation, final DataValueDescriptor[] array, final FormatableBitSet set, final boolean b) throws StandardException {
        if (this.open_conglom.isClosed()) {
            if (!this.open_conglom.getHold()) {
                throw StandardException.newException("XSCH6.S", this.open_conglom.getConglomerate().getId());
            }
            if (this.open_conglom.isClosed()) {
                this.open_conglom.reopen();
            }
        }
        final RowPosition get_scratch_row_position = this.open_conglom.getRuntimeMem().get_scratch_row_position();
        this.getRowPositionFromRowLocation(rowLocation, get_scratch_row_position);
        if (!this.open_conglom.latchPage(get_scratch_row_position)) {
            return false;
        }
        if (this.open_conglom.isForUpdate()) {
            this.open_conglom.lockPositionForWrite(get_scratch_row_position, b);
        }
        else {
            this.open_conglom.lockPositionForRead(get_scratch_row_position, null, false, b);
        }
        if (get_scratch_row_position.current_page == null) {
            return false;
        }
        final boolean b2 = get_scratch_row_position.current_page.fetchFromSlot(get_scratch_row_position.current_rh, get_scratch_row_position.current_slot, array, new FetchDescriptor(array.length, set, null), false) != null;
        if (!this.open_conglom.isForUpdate()) {
            this.open_conglom.unlockPositionAfterRead(get_scratch_row_position);
        }
        get_scratch_row_position.current_page.unlatch();
        return b2;
    }
    
    public boolean replace(final RowLocation rowLocation, final DataValueDescriptor[] array, final FormatableBitSet set) throws StandardException {
        if (this.open_conglom.isClosed()) {
            if (!this.open_conglom.getHold()) {
                throw StandardException.newException("XSCH6.S", this.open_conglom.getConglomerate().getId());
            }
            if (this.open_conglom.isClosed()) {
                this.open_conglom.reopen();
            }
        }
        final RowPosition get_scratch_row_position = this.open_conglom.getRuntimeMem().get_scratch_row_position();
        this.getRowPositionFromRowLocation(rowLocation, get_scratch_row_position);
        if (!this.open_conglom.latchPage(get_scratch_row_position)) {
            return false;
        }
        this.open_conglom.lockPositionForWrite(get_scratch_row_position, true);
        boolean b = true;
        if (get_scratch_row_position.current_page.isDeletedAtSlot(get_scratch_row_position.current_slot)) {
            b = false;
        }
        else {
            get_scratch_row_position.current_page.updateAtSlot(get_scratch_row_position.current_slot, array, set);
        }
        get_scratch_row_position.current_page.unlatch();
        return b;
    }
}
