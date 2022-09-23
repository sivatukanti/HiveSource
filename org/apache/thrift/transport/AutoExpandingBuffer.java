// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.transport;

public class AutoExpandingBuffer
{
    private byte[] array;
    private final double growthCoefficient;
    
    public AutoExpandingBuffer(final int initialCapacity, final double growthCoefficient) {
        if (growthCoefficient < 1.0) {
            throw new IllegalArgumentException("Growth coefficient must be >= 1.0");
        }
        this.array = new byte[initialCapacity];
        this.growthCoefficient = growthCoefficient;
    }
    
    public void resizeIfNecessary(final int size) {
        if (this.array.length < size) {
            final byte[] newBuf = new byte[(int)(size * this.growthCoefficient)];
            System.arraycopy(this.array, 0, newBuf, 0, this.array.length);
            this.array = newBuf;
        }
    }
    
    public byte[] array() {
        return this.array;
    }
}
