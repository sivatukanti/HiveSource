// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.error.StandardException;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import java.io.Externalizable;

public class AllocExtent implements Externalizable
{
    private long extentOffset;
    private long extentStart;
    private long extentEnd;
    private int extentLength;
    int extentStatus;
    private int preAllocLength;
    private int reserved1;
    private long reserved2;
    private long reserved3;
    private static final int HAS_DEALLOCATED = 1;
    private static final int HAS_FREE = 2;
    private static final int ALL_FREE = 4;
    private static final int HAS_UNFILLED_PAGES = 16;
    private static final int KEEP_UNFILLED_PAGES = 268435456;
    private static final int NO_DEALLOC_PAGE_MAP = 536870912;
    private static final int RETIRED = 8;
    protected static final int ALLOCATED_PAGE = 0;
    protected static final int DEALLOCATED_PAGE = 1;
    protected static final int FREE_PAGE = 2;
    FormatableBitSet freePages;
    FormatableBitSet unFilledPages;
    
    protected static int MAX_RANGE(int n) {
        n -= 56;
        n /= 3;
        if (n <= 0) {
            return 0;
        }
        return FormatableBitSet.maxBitsForSpace(n);
    }
    
    protected AllocExtent(final long extentOffset, final long extentStart, final int extentLength, final int n, final int n2) {
        this.extentOffset = extentOffset;
        this.extentStart = extentStart;
        this.extentEnd = extentStart + n2 - 1L;
        this.extentLength = extentLength;
        this.preAllocLength = this.extentLength;
        if (extentLength > 0) {
            this.extentStatus = 6;
        }
        else {
            this.extentStatus = 0;
        }
        this.extentStatus |= 0x10000000;
        this.extentStatus |= 0x20000000;
        int n3 = (1 + extentLength / 8) * 8;
        if (n3 > n2) {
            n3 = n2;
        }
        this.freePages = new FormatableBitSet(n3);
        this.unFilledPages = new FormatableBitSet(n3);
        for (int i = 0; i < extentLength; ++i) {
            this.freePages.set(i);
        }
    }
    
    protected AllocExtent(final AllocExtent allocExtent) {
        this.extentOffset = allocExtent.extentOffset;
        this.extentStart = allocExtent.extentStart;
        this.extentEnd = allocExtent.extentEnd;
        this.extentLength = allocExtent.extentLength;
        this.extentStatus = allocExtent.extentStatus;
        this.preAllocLength = allocExtent.preAllocLength;
        this.freePages = new FormatableBitSet(allocExtent.freePages);
        this.unFilledPages = new FormatableBitSet(allocExtent.unFilledPages);
    }
    
    public AllocExtent() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeLong(this.extentOffset);
        objectOutput.writeLong(this.extentStart);
        objectOutput.writeLong(this.extentEnd);
        objectOutput.writeInt(this.extentLength);
        objectOutput.writeInt(this.extentStatus);
        objectOutput.writeInt(this.preAllocLength);
        objectOutput.writeInt(0);
        objectOutput.writeLong(0L);
        objectOutput.writeLong(0L);
        this.freePages.writeExternal(objectOutput);
        this.unFilledPages.writeExternal(objectOutput);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.extentOffset = objectInput.readLong();
        this.extentStart = objectInput.readLong();
        this.extentEnd = objectInput.readLong();
        this.extentLength = objectInput.readInt();
        this.extentStatus = objectInput.readInt();
        this.preAllocLength = objectInput.readInt();
        this.reserved1 = objectInput.readInt();
        this.reserved2 = objectInput.readLong();
        this.reserved3 = objectInput.readLong();
        (this.freePages = new FormatableBitSet()).readExternal(objectInput);
        if ((this.extentStatus & 0x20000000) == 0x0) {
            final FormatableBitSet set = new FormatableBitSet();
            set.readExternal(objectInput);
            this.freePages.or(set);
            this.extentStatus |= 0x20000000;
        }
        if ((this.extentStatus & 0x10000000) == 0x10000000) {
            (this.unFilledPages = new FormatableBitSet()).readExternal(objectInput);
        }
        else {
            this.unFilledPages = new FormatableBitSet(this.freePages.getLength());
            this.extentStatus |= 0x10000000;
        }
    }
    
    protected void allocPage(final long n) throws StandardException {
        final int n2 = (int)(n - this.extentStart);
        if (n2 >= this.freePages.getLength()) {
            int n3 = (1 + n2 / 8) * 8;
            if (n3 > (int)(this.extentEnd - this.extentStart + 1L)) {
                n3 = (int)(this.extentEnd - this.extentStart + 1L);
            }
            this.freePages.grow(n3);
            this.unFilledPages.grow(n3);
        }
        final int extentLength = (int)(n - this.extentStart + 1L);
        if (extentLength > this.extentLength) {
            this.extentLength = extentLength;
        }
        this.freePages.clear(n2);
    }
    
    protected void deallocPage(final long n) throws StandardException {
        final int n2 = (int)(n - this.extentStart);
        this.freePages.set(n2);
        this.unFilledPages.clear(n2);
        this.setExtentFreePageStatus(true);
    }
    
    protected int compress(final BaseContainerHandle baseContainerHandle, final RawTransaction rawTransaction, final AllocPage allocPage) throws StandardException {
        int n = -1;
        int n2 = 0;
        for (int n3 = this.extentLength - 1; n3 >= 0 && this.freePages.isSet(n3); --n3) {
            n = n3;
            ++n2;
        }
        final int n4 = n - 1;
        if (n2 > 0) {
            baseContainerHandle.getAllocationActionSet().actionCompressSpaceOperation(rawTransaction, allocPage, n4, n2);
            return n;
        }
        return -1;
    }
    
    protected void compressPages(final int n, final int n2) {
        if (n + 1 >= 0) {
            this.freePages.shrink(n + 1);
            this.unFilledPages.shrink(n + 1);
            final int n3 = n + 1;
            this.extentLength = n3;
            this.preAllocLength = n3;
        }
    }
    
    protected void undoCompressPages(final int n, final int n2) {
        if (n >= 0) {
            this.freePages.shrink(n + 1);
            this.unFilledPages.shrink(n + 1);
            final int n3 = n + 1;
            this.extentLength = n3;
            this.preAllocLength = n3;
        }
    }
    
    protected long getExtentEnd() {
        return this.extentEnd;
    }
    
    protected long getFreePageNumber(final long n) {
        if (this.mayHaveFreePage()) {
            final int n2 = (n < this.extentStart) ? this.freePages.anySetBit() : this.freePages.anySetBit((int)(n - this.extentStart));
            if (n2 != -1) {
                return n2 + this.extentStart;
            }
            if (n < this.extentStart) {
                this.setExtentFreePageStatus(false);
            }
        }
        return this.extentStart + this.extentLength;
    }
    
    protected long getPageOffset(final long n, final int n2, final boolean b) throws StandardException {
        return n * n2;
    }
    
    protected boolean isRetired() {
        return (this.extentStatus & 0x8) != 0x0;
    }
    
    private boolean mayHaveFreePage() {
        return (this.extentStatus & 0x2) != 0x0;
    }
    
    private void setExtentFreePageStatus(final boolean b) {
        if (b) {
            this.extentStatus |= 0x2;
        }
        else {
            this.extentStatus &= 0xFFFFFFFD;
        }
    }
    
    protected boolean canAddFreePage(final long n) {
        if (this.extentStart + this.extentLength <= this.extentEnd) {
            return true;
        }
        if (!this.mayHaveFreePage()) {
            return false;
        }
        if (n < this.extentStart) {
            return this.freePages.anySetBit() != -1;
        }
        return this.freePages.anySetBit((int)(n - this.extentStart)) != -1;
    }
    
    protected int getPageStatus(final long n) {
        int n2;
        if (this.freePages.isSet((int)(n - this.extentStart))) {
            n2 = 2;
        }
        else {
            n2 = 0;
        }
        return n2;
    }
    
    protected long getFirstPagenum() {
        return this.extentStart;
    }
    
    protected long getLastPagenum() {
        return this.extentStart + this.extentLength - 1L;
    }
    
    protected long getPagenum(final int n) {
        return this.extentStart + n;
    }
    
    protected long getLastPreallocPagenum() {
        if (this.extentLength > this.preAllocLength) {
            this.preAllocLength = this.extentLength;
        }
        return this.extentStart + this.preAllocLength - 1L;
    }
    
    protected void setLastPreallocPagenum(long extentEnd) {
        if (extentEnd > this.extentEnd) {
            extentEnd = this.extentEnd;
        }
        this.preAllocLength = (int)(extentEnd - this.extentStart + 1L);
    }
    
    protected long getNextValidPageNumber(final long n) {
        final long lastPagenum = this.getLastPagenum();
        long extentStart;
        if (n < this.extentStart) {
            extentStart = this.extentStart;
        }
        else {
            extentStart = n + 1L;
        }
        while (extentStart <= lastPagenum && this.getPageStatus(extentStart) != 0) {
            ++extentStart;
        }
        if (extentStart > lastPagenum) {
            extentStart = -1L;
        }
        return extentStart;
    }
    
    protected long getLastValidPageNumber() {
        long lastPagenum;
        for (lastPagenum = this.getLastPagenum(); lastPagenum >= this.extentStart && this.getPageStatus(lastPagenum) != 0; --lastPagenum) {}
        if (lastPagenum < this.extentStart) {
            lastPagenum = -1L;
        }
        return lastPagenum;
    }
    
    private void checkInRange(final long n) {
    }
    
    protected void updateUnfilledPageInfo(final AllocExtent allocExtent) {
        this.unFilledPages = allocExtent.unFilledPages;
        if (this.unFilledPages.anySetBit() >= 0) {
            this.extentStatus |= 0x10;
        }
        else {
            this.extentStatus &= 0xFFFFFFEF;
        }
    }
    
    protected boolean trackUnfilledPage(final long n, final boolean b) {
        this.checkInRange(n);
        final int n2 = (int)(n - this.extentStart);
        if (b != this.unFilledPages.isSet(n2)) {
            if (b) {
                this.unFilledPages.set(n2);
                this.extentStatus |= 0x10;
            }
            else {
                this.unFilledPages.clear(n2);
            }
            return true;
        }
        return false;
    }
    
    protected long getUnfilledPageNumber(final long n) {
        if ((this.extentStatus & 0x10) == 0x0) {
            return -1L;
        }
        final int anySetBit = this.unFilledPages.anySetBit();
        if (anySetBit != -1) {
            if (anySetBit + this.extentStart != n) {
                return anySetBit + this.extentStart;
            }
            final int anySetBit2 = this.unFilledPages.anySetBit(anySetBit);
            if (anySetBit2 != -1) {
                return anySetBit2 + this.extentStart;
            }
        }
        return -1L;
    }
    
    protected int getAllocatedPageCount() {
        int extentLength = this.extentLength;
        if (!this.mayHaveFreePage()) {
            return extentLength;
        }
        final byte[] byteArray = this.freePages.getByteArray();
        for (int length = byteArray.length, i = 0; i < length; ++i) {
            if (byteArray[i] != 0) {
                for (int j = 0; j < 8; ++j) {
                    if ((1 << j & byteArray[i]) != 0x0) {
                        --extentLength;
                    }
                }
            }
        }
        return extentLength;
    }
    
    protected int getUnfilledPageCount() {
        int n = 0;
        final int size = this.freePages.size();
        for (int i = 0; i < this.unFilledPages.size(); ++i) {
            if (this.unFilledPages.isSet(i) && (i >= size || !this.freePages.isSet(i))) {
                ++n;
            }
        }
        return n;
    }
    
    protected int getTotalPageCount() {
        return this.extentLength;
    }
    
    protected String toDebugString() {
        return null;
    }
}
