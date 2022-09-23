// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import java.util.concurrent.ThreadLocalRandom;

public final class SpanId implements Comparable<SpanId>
{
    private static final int SPAN_ID_STRING_LENGTH = 32;
    private final long high;
    private final long low;
    public static SpanId INVALID;
    
    private static long nonZeroRand64() {
        long r;
        do {
            r = ThreadLocalRandom.current().nextLong();
        } while (r == 0L);
        return r;
    }
    
    public static SpanId fromRandom() {
        return new SpanId(nonZeroRand64(), nonZeroRand64());
    }
    
    public static SpanId fromString(final String str) {
        if (str.length() != 32) {
            throw new RuntimeException("Invalid SpanID string: length was not 32");
        }
        final long high = Long.parseLong(str.substring(0, 8), 16) << 32 | Long.parseLong(str.substring(8, 16), 16);
        final long low = Long.parseLong(str.substring(16, 24), 16) << 32 | Long.parseLong(str.substring(24, 32), 16);
        return new SpanId(high, low);
    }
    
    public SpanId(final long high, final long low) {
        this.high = high;
        this.low = low;
    }
    
    public long getHigh() {
        return this.high;
    }
    
    public long getLow() {
        return this.low;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof SpanId)) {
            return false;
        }
        final SpanId other = (SpanId)o;
        return other.high == this.high && other.low == this.low;
    }
    
    @Override
    public int compareTo(final SpanId other) {
        final int cmp = compareAsUnsigned(this.high, other.high);
        if (cmp != 0) {
            return cmp;
        }
        return compareAsUnsigned(this.low, other.low);
    }
    
    private static int compareAsUnsigned(long a, long b) {
        final boolean aSign = a < 0L;
        final boolean bSign = b < 0L;
        if (aSign != bSign) {
            if (aSign) {
                return 1;
            }
            return -1;
        }
        else {
            if (aSign) {
                a = -a;
                b = -b;
            }
            if (a < b) {
                return -1;
            }
            if (a > b) {
                return 1;
            }
            return 0;
        }
    }
    
    @Override
    public int hashCode() {
        return (int)(-1L & this.high >> 32) ^ (int)(-1L & this.high >> 0) ^ (int)(-1L & this.low >> 32) ^ (int)(-1L & this.low >> 0);
    }
    
    @Override
    public String toString() {
        return String.format("%08x%08x%08x%08x", 0xFFFFFFFFL & this.high >> 32, 0xFFFFFFFFL & this.high, 0xFFFFFFFFL & this.low >> 32, 0xFFFFFFFFL & this.low);
    }
    
    public boolean isValid() {
        return this.high != 0L || this.low != 0L;
    }
    
    public SpanId newChildId() {
        return new SpanId(this.high, nonZeroRand64());
    }
    
    static {
        SpanId.INVALID = new SpanId(0L, 0L);
    }
}
