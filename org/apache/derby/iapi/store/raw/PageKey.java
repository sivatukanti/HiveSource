// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;

public final class PageKey
{
    private final ContainerKey container;
    private final long pageNumber;
    
    public PageKey(final ContainerKey container, final long pageNumber) {
        this.container = container;
        this.pageNumber = pageNumber;
    }
    
    public long getPageNumber() {
        return this.pageNumber;
    }
    
    public ContainerKey getContainerId() {
        return this.container;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        this.container.writeExternal(objectOutput);
        CompressedNumber.writeLong(objectOutput, this.pageNumber);
    }
    
    public static PageKey read(final ObjectInput objectInput) throws IOException {
        return new PageKey(ContainerKey.read(objectInput), CompressedNumber.readLong(objectInput));
    }
    
    public boolean equals(final Object o) {
        if (o instanceof PageKey) {
            final PageKey pageKey = (PageKey)o;
            return this.pageNumber == pageKey.pageNumber && this.container.equals(pageKey.container);
        }
        return false;
    }
    
    public int hashCode() {
        return 79 * (79 * 7 + this.container.hashCode()) + (int)(this.pageNumber ^ this.pageNumber >>> 32);
    }
    
    public String toString() {
        return "Page(" + this.pageNumber + "," + this.container.toString() + ")";
    }
}
