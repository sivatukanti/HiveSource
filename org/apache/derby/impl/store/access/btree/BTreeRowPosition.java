// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.impl.store.access.conglomerate.RowPosition;

public class BTreeRowPosition extends RowPosition
{
    public DataValueDescriptor[] current_positionKey;
    public LeafControlRow current_leaf;
    protected LeafControlRow next_leaf;
    public DataValueDescriptor[] current_lock_template;
    public RowLocation current_lock_row_loc;
    private final BTreeScan parent;
    long versionWhenSaved;
    private DataValueDescriptor[] positionKey_template;
    private FetchDescriptor savedFetchDescriptor;
    
    public BTreeRowPosition(final BTreeScan parent) {
        this.parent = parent;
    }
    
    public void init() {
        super.init();
        this.current_leaf = null;
        this.current_positionKey = null;
    }
    
    public final void unlatch() {
        if (this.current_leaf != null) {
            this.current_leaf.release();
            this.current_leaf = null;
        }
        this.current_slot = -1;
    }
    
    public void saveMeAndReleasePage() throws StandardException {
        this.parent.savePositionAndReleasePage();
    }
    
    DataValueDescriptor[] getKeyTemplate() throws StandardException {
        if (this.positionKey_template == null) {
            this.positionKey_template = this.parent.getRuntimeMem().get_row_for_export(this.parent.getRawTran());
        }
        return this.positionKey_template;
    }
    
    FetchDescriptor getFetchDescriptorForSaveKey(final int[] array, final int n) {
        if (this.savedFetchDescriptor == null) {
            final FormatableBitSet set = new FormatableBitSet(n);
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == 0) {
                    set.set(i);
                }
            }
            for (int j = array.length; j < n; ++j) {
                set.set(j);
            }
            this.savedFetchDescriptor = new FetchDescriptor(n, set, null);
        }
        return this.savedFetchDescriptor;
    }
    
    public final String toString() {
        return null;
    }
}
