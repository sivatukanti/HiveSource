// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.PageTimeStamp;

public class PageVersion implements PageTimeStamp
{
    private long pageNumber;
    private long pageVersion;
    
    public PageVersion(final long pageNumber, final long pageVersion) {
        this.pageNumber = pageNumber;
        this.pageVersion = pageVersion;
    }
    
    public long getPageVersion() {
        return this.pageVersion;
    }
    
    public long getPageNumber() {
        return this.pageNumber;
    }
    
    public void setPageVersion(final long pageVersion) {
        this.pageVersion = pageVersion;
    }
    
    public void setPageNumber(final long pageNumber) {
        this.pageNumber = pageNumber;
    }
}
