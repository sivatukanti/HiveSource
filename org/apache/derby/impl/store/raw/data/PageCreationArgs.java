// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

class PageCreationArgs
{
    final int formatId;
    final int syncFlag;
    final int pageSize;
    final int spareSpace;
    final int minimumRecordSize;
    final int containerInfoSize;
    
    PageCreationArgs(final int formatId, final int syncFlag, final int pageSize, final int spareSpace, final int minimumRecordSize, final int containerInfoSize) {
        this.formatId = formatId;
        this.syncFlag = syncFlag;
        this.pageSize = pageSize;
        this.spareSpace = spareSpace;
        this.minimumRecordSize = minimumRecordSize;
        this.containerInfoSize = containerInfoSize;
    }
}
