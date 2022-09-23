// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common;

import java.nio.ByteBuffer;

public class DiskRange
{
    protected long offset;
    protected long end;
    
    public DiskRange(final long offset, final long end) {
        this.offset = offset;
        this.end = end;
        if (end < offset) {
            throw new IllegalArgumentException("invalid range " + this);
        }
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        final DiskRange otherR = (DiskRange)other;
        return otherR.offset == this.offset && otherR.end == this.end;
    }
    
    @Override
    public int hashCode() {
        return (int)(this.offset ^ this.offset >>> 32) * 31 + (int)(this.end ^ this.end >>> 32);
    }
    
    @Override
    public String toString() {
        return "range start: " + this.offset + " end: " + this.end;
    }
    
    public long getOffset() {
        return this.offset;
    }
    
    public long getEnd() {
        return this.end;
    }
    
    public int getLength() {
        final long len = this.end - this.offset;
        assert len <= 2147483647L;
        return (int)len;
    }
    
    public boolean hasData() {
        return false;
    }
    
    public DiskRange sliceAndShift(final long offset, final long end, final long shiftBy) {
        throw new UnsupportedOperationException();
    }
    
    public ByteBuffer getData() {
        throw new UnsupportedOperationException();
    }
    
    protected boolean merge(final long otherOffset, final long otherEnd) {
        if (!overlap(this.offset, this.end, otherOffset, otherEnd)) {
            return false;
        }
        this.offset = Math.min(this.offset, otherOffset);
        this.end = Math.max(this.end, otherEnd);
        return true;
    }
    
    private static boolean overlap(final long leftA, final long rightA, final long leftB, final long rightB) {
        if (leftA <= leftB) {
            return rightA >= leftB;
        }
        return rightB >= leftA;
    }
}
