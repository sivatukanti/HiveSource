// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import java.util.List;
import java.util.ArrayList;

public class AdaptiveReceiveBufferSizePredictor implements ReceiveBufferSizePredictor
{
    static final int DEFAULT_MINIMUM = 64;
    static final int DEFAULT_INITIAL = 1024;
    static final int DEFAULT_MAXIMUM = 65536;
    private static final int INDEX_INCREMENT = 4;
    private static final int INDEX_DECREMENT = 1;
    private static final int[] SIZE_TABLE;
    private final int minIndex;
    private final int maxIndex;
    private int index;
    private int nextReceiveBufferSize;
    private boolean decreaseNow;
    
    private static int getSizeTableIndex(final int size) {
        if (size <= 16) {
            return size - 1;
        }
        int bits = 0;
        int v = size;
        do {
            v >>>= 1;
            ++bits;
        } while (v != 0);
        final int baseIdx = bits << 3;
        final int startIdx = baseIdx - 18;
        for (int endIdx = baseIdx - 25, i = startIdx; i >= endIdx; --i) {
            if (size >= AdaptiveReceiveBufferSizePredictor.SIZE_TABLE[i]) {
                return i;
            }
        }
        throw new Error("shouldn't reach here; please file a bug report.");
    }
    
    public AdaptiveReceiveBufferSizePredictor() {
        this(64, 1024, 65536);
    }
    
    public AdaptiveReceiveBufferSizePredictor(final int minimum, final int initial, final int maximum) {
        if (minimum <= 0) {
            throw new IllegalArgumentException("minimum: " + minimum);
        }
        if (initial < minimum) {
            throw new IllegalArgumentException("initial: " + initial);
        }
        if (maximum < initial) {
            throw new IllegalArgumentException("maximum: " + maximum);
        }
        final int minIndex = getSizeTableIndex(minimum);
        if (AdaptiveReceiveBufferSizePredictor.SIZE_TABLE[minIndex] < minimum) {
            this.minIndex = minIndex + 1;
        }
        else {
            this.minIndex = minIndex;
        }
        final int maxIndex = getSizeTableIndex(maximum);
        if (AdaptiveReceiveBufferSizePredictor.SIZE_TABLE[maxIndex] > maximum) {
            this.maxIndex = maxIndex - 1;
        }
        else {
            this.maxIndex = maxIndex;
        }
        this.index = getSizeTableIndex(initial);
        this.nextReceiveBufferSize = AdaptiveReceiveBufferSizePredictor.SIZE_TABLE[this.index];
    }
    
    public int nextReceiveBufferSize() {
        return this.nextReceiveBufferSize;
    }
    
    public void previousReceiveBufferSize(final int previousReceiveBufferSize) {
        if (previousReceiveBufferSize <= AdaptiveReceiveBufferSizePredictor.SIZE_TABLE[Math.max(0, this.index - 1 - 1)]) {
            if (this.decreaseNow) {
                this.index = Math.max(this.index - 1, this.minIndex);
                this.nextReceiveBufferSize = AdaptiveReceiveBufferSizePredictor.SIZE_TABLE[this.index];
                this.decreaseNow = false;
            }
            else {
                this.decreaseNow = true;
            }
        }
        else if (previousReceiveBufferSize >= this.nextReceiveBufferSize) {
            this.index = Math.min(this.index + 4, this.maxIndex);
            this.nextReceiveBufferSize = AdaptiveReceiveBufferSizePredictor.SIZE_TABLE[this.index];
            this.decreaseNow = false;
        }
    }
    
    static {
        final List<Integer> sizeTable = new ArrayList<Integer>();
        for (int i = 1; i <= 8; ++i) {
            sizeTable.add(i);
        }
        for (int i = 4; i < 32; ++i) {
            long v = 1L << i;
            final long inc = v >>> 4;
            v -= inc << 3;
            for (int j = 0; j < 8; ++j) {
                v += inc;
                if (v > 2147483647L) {
                    sizeTable.add(Integer.MAX_VALUE);
                }
                else {
                    sizeTable.add((int)v);
                }
            }
        }
        SIZE_TABLE = new int[sizeTable.size()];
        for (int i = 0; i < AdaptiveReceiveBufferSizePredictor.SIZE_TABLE.length; ++i) {
            AdaptiveReceiveBufferSizePredictor.SIZE_TABLE[i] = sizeTable.get(i);
        }
    }
}
