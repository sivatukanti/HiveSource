// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.error.StandardException;

class AllocationCache
{
    private int numExtents;
    private long[] lowRange;
    private long[] hiRange;
    private boolean[] isDirty;
    private AllocExtent[] extents;
    private long[] extentPageNums;
    private boolean isValid;
    
    protected AllocationCache() {
        this.numExtents = 0;
        this.isValid = false;
    }
    
    protected void reset() {
        this.numExtents = 0;
        this.isValid = false;
        if (this.lowRange != null) {
            for (int i = 0; i < this.lowRange.length; ++i) {
                this.lowRange[i] = -1L;
                this.hiRange[i] = -1L;
                this.extentPageNums[i] = -1L;
                this.extents[i] = null;
                this.isDirty[i] = false;
            }
        }
    }
    
    protected long getAllocPageNumber(final BaseContainerHandle baseContainerHandle, final long n, final long n2) throws StandardException {
        for (int i = 0; i < this.numExtents; ++i) {
            if (this.lowRange[i] <= n && n <= this.hiRange[i]) {
                return this.extentPageNums[i];
            }
        }
        if (!this.isValid) {
            this.validate(baseContainerHandle, n2);
            for (int j = 0; j < this.numExtents; ++j) {
                if (this.lowRange[j] <= n && n <= this.hiRange[j]) {
                    return this.extentPageNums[j];
                }
            }
        }
        return -1L;
    }
    
    protected long getLastPageNumber(final BaseContainerHandle baseContainerHandle, final long n) throws StandardException {
        if (!this.isValid) {
            this.validate(baseContainerHandle, n);
        }
        return this.hiRange[this.numExtents - 1];
    }
    
    protected void trackUnfilledPage(final long n, final boolean b) {
        if (!this.isValid || this.numExtents <= 0) {
            return;
        }
        int i = 0;
        while (i < this.numExtents) {
            if (this.lowRange[i] <= n && n <= this.hiRange[i]) {
                final AllocExtent allocExtent = this.extents[i];
                if (allocExtent != null && allocExtent.trackUnfilledPage(n, b) && this.extents[i] != null) {
                    this.isDirty[i] = true;
                    break;
                }
                break;
            }
            else {
                ++i;
            }
        }
    }
    
    protected long getUnfilledPageNumber(final BaseContainerHandle baseContainerHandle, final long n, final long n2) throws StandardException {
        if (!this.isValid) {
            this.validate(baseContainerHandle, n);
        }
        if (n2 == -1L) {
            for (int i = 0; i < this.numExtents; ++i) {
                if (this.extents[i] != null) {
                    return this.extents[i].getUnfilledPageNumber(n2);
                }
            }
        }
        else {
            for (int j = 0; j < this.numExtents; ++j) {
                if (n2 <= this.hiRange[j] && this.extents[j] != null) {
                    return this.extents[j].getUnfilledPageNumber(n2);
                }
            }
        }
        return -1L;
    }
    
    protected long getEstimatedPageCount(final BaseContainerHandle baseContainerHandle, final long n) throws StandardException {
        if (!this.isValid) {
            this.validate(baseContainerHandle, n);
        }
        long n2 = 0L;
        for (int i = 0; i < this.numExtents; ++i) {
            if (this.extents[i] != null) {
                n2 += this.extents[i].getAllocatedPageCount();
            }
        }
        return n2;
    }
    
    protected SpaceInformation getAllPageCounts(final BaseContainerHandle baseContainerHandle, final long n) throws StandardException {
        long n2 = 0L;
        long n3 = 0L;
        long n4 = 0L;
        if (!this.isValid) {
            this.validate(baseContainerHandle, n);
        }
        for (int i = 0; i < this.numExtents; ++i) {
            if (this.extents[i] != null) {
                final long n5 = this.extents[i].getAllocatedPageCount();
                n2 += n5;
                n4 += this.extents[i].getUnfilledPageCount();
                n3 += this.extents[i].getTotalPageCount() - n5;
            }
        }
        return new SpaceInformation(n2, n3, n4);
    }
    
    protected void invalidate() {
        for (int i = 0; i < this.numExtents; ++i) {
            this.isDirty[i] = false;
            this.extents[i] = null;
        }
        this.isValid = false;
    }
    
    protected void invalidate(final AllocPage allocPage, final long n) throws StandardException {
        this.isValid = false;
        if (this.numExtents == 0) {
            return;
        }
        for (int i = 0; i < this.numExtents; ++i) {
            if (this.extentPageNums[i] == n) {
                if (allocPage != null && this.extents[i] != null && this.isDirty[i]) {
                    allocPage.updateUnfilledPageInfo(this.extents[i]);
                    this.isDirty[i] = false;
                }
                this.extents[i] = null;
                return;
            }
        }
        if (n > this.hiRange[this.numExtents - 1]) {
            return;
        }
    }
    
    protected void invalidateLastExtent() {
        this.isValid = false;
        if (this.numExtents > 0) {
            this.extents[this.numExtents - 1] = null;
        }
    }
    
    protected long getLastValidPage(final BaseContainerHandle baseContainerHandle, final long n) throws StandardException {
        long lastValidPageNumber = -1L;
        if (!this.isValid) {
            this.validate(baseContainerHandle, n);
        }
        if (this.numExtents == 0) {
            return -1L;
        }
        for (int i = this.numExtents - 1; i >= 0; --i) {
            lastValidPageNumber = this.extents[i].getLastValidPageNumber();
            if (lastValidPageNumber != -1L) {
                break;
            }
        }
        return lastValidPageNumber;
    }
    
    protected long getNextValidPage(final BaseContainerHandle baseContainerHandle, final long n, final long n2) throws StandardException {
        if (!this.isValid) {
            this.validate(baseContainerHandle, n2);
        }
        if (this.numExtents == 0) {
            return -1L;
        }
        AllocExtent allocExtent = null;
        int i;
        for (i = 0; i < this.numExtents; ++i) {
            if (n < this.hiRange[i]) {
                allocExtent = this.extents[i];
                break;
            }
        }
        if (allocExtent == null) {
            return -1L;
        }
        long nextValidPageNumber = -1L;
        while (i < this.numExtents) {
            nextValidPageNumber = this.extents[i].getNextValidPageNumber(n);
            if (nextValidPageNumber != -1L) {
                break;
            }
            ++i;
        }
        return nextValidPageNumber;
    }
    
    protected int getPageStatus(final BaseContainerHandle baseContainerHandle, final long n, final long n2) throws StandardException {
        AllocExtent allocExtent = null;
        for (int i = 0; i < this.numExtents; ++i) {
            if (this.lowRange[i] <= n && n <= this.hiRange[i]) {
                allocExtent = this.extents[i];
                break;
            }
        }
        if (allocExtent == null) {
            if (!this.isValid) {
                this.validate(baseContainerHandle, n2);
            }
            for (int j = 0; j < this.numExtents; ++j) {
                if (this.lowRange[j] <= n && n <= this.hiRange[j]) {
                    allocExtent = this.extents[j];
                    break;
                }
            }
        }
        return allocExtent.getPageStatus(n);
    }
    
    private void validate(final BaseContainerHandle baseContainerHandle, final long n) throws StandardException {
        if (this.numExtents == 0) {
            long nextAllocPageNumber = n;
            while (!this.isValid) {
                this.growArrays(++this.numExtents);
                final AllocPage allocPage = (AllocPage)baseContainerHandle.getAllocPage(nextAllocPageNumber);
                this.setArrays(this.numExtents - 1, allocPage);
                if (allocPage.isLast()) {
                    this.isValid = true;
                }
                else {
                    nextAllocPageNumber = allocPage.getNextAllocPageNumber();
                }
                allocPage.unlatch();
            }
        }
        else {
            for (int i = 0; i < this.numExtents - 1; ++i) {
                if (this.extents[i] == null) {
                    final AllocPage allocPage2 = (AllocPage)baseContainerHandle.getAllocPage(this.extentPageNums[i]);
                    this.setArrays(i, allocPage2);
                    allocPage2.unlatch();
                }
            }
            long nextAllocPageNumber2 = this.extentPageNums[this.numExtents - 1];
            while (!this.isValid) {
                final AllocPage allocPage3 = (AllocPage)baseContainerHandle.getAllocPage(nextAllocPageNumber2);
                if (this.extents[this.numExtents - 1] == null) {
                    this.setArrays(this.numExtents - 1, allocPage3);
                }
                if (!allocPage3.isLast()) {
                    this.growArrays(++this.numExtents);
                    nextAllocPageNumber2 = allocPage3.getNextAllocPageNumber();
                }
                else {
                    this.isValid = true;
                }
                allocPage3.unlatch();
            }
        }
    }
    
    private void setArrays(final int n, final AllocPage allocPage) {
        final AllocExtent allocExtent = allocPage.getAllocExtent();
        this.extents[n] = allocExtent;
        this.lowRange[n] = allocExtent.getFirstPagenum();
        this.hiRange[n] = allocExtent.getLastPagenum();
        this.extentPageNums[n] = allocPage.getPageNumber();
    }
    
    private void growArrays(final int n) {
        int length;
        if (this.lowRange == null || this.lowRange.length == 0) {
            length = 0;
        }
        else {
            length = this.lowRange.length;
        }
        if (length >= n) {
            return;
        }
        final long[] lowRange = this.lowRange;
        final long[] hiRange = this.hiRange;
        final AllocExtent[] extents = this.extents;
        final boolean[] isDirty = this.isDirty;
        final long[] extentPageNums = this.extentPageNums;
        this.lowRange = new long[n];
        this.hiRange = new long[n];
        this.isDirty = new boolean[n];
        this.extents = new AllocExtent[n];
        this.extentPageNums = new long[n];
        if (length > 0) {
            System.arraycopy(lowRange, 0, this.lowRange, 0, lowRange.length);
            System.arraycopy(hiRange, 0, this.hiRange, 0, hiRange.length);
            System.arraycopy(isDirty, 0, this.isDirty, 0, isDirty.length);
            System.arraycopy(extents, 0, this.extents, 0, extents.length);
            System.arraycopy(extentPageNums, 0, this.extentPageNums, 0, extentPageNums.length);
        }
        for (int i = length; i < n; ++i) {
            this.lowRange[i] = -1L;
            this.hiRange[i] = -1L;
            this.isDirty[i] = false;
            this.extentPageNums[i] = -1L;
            this.extents[i] = null;
        }
    }
    
    protected void dumpAllocationCache() {
    }
}
