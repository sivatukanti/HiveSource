// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.access.SpaceInfo;

public class SpaceInformation implements SpaceInfo
{
    private long numAllocatedPages;
    private long numFreePages;
    private long numUnfilledPages;
    private int pageSize;
    
    public SpaceInformation(final long numAllocatedPages, final long numFreePages, final long numUnfilledPages) {
        this.numAllocatedPages = numAllocatedPages;
        this.numFreePages = numFreePages;
        this.numUnfilledPages = numUnfilledPages;
    }
    
    public long getNumAllocatedPages() {
        return this.numAllocatedPages;
    }
    
    public long getNumFreePages() {
        return this.numFreePages;
    }
    
    public long getNumUnfilledPages() {
        return this.numUnfilledPages;
    }
    
    public int getPageSize() {
        return this.pageSize;
    }
    
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }
}
