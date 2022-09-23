// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.heap;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.impl.store.access.conglomerate.OpenConglomerate;

class OpenHeap extends OpenConglomerate
{
    public int[] getFormatIds() {
        return ((Heap)this.getConglomerate()).format_ids;
    }
    
    public RowLocation newRowLocationTemplate() throws StandardException {
        if (this.getContainer() == null) {
            throw StandardException.newException("XSCH6.S", this.getConglomerate().getId());
        }
        return new HeapRowLocation();
    }
}
