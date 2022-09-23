// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io.vfmem;

public class BlockedByteArray
{
    private static final int _4K = 4096;
    private static final int _8K = 8192;
    private static final int _16K = 16384;
    private static final int _32K = 32768;
    private static final int DEFAULT_BLOCKSIZE = 4096;
    private static final int INITIAL_BLOCK_HOLDER_SIZE = 1024;
    private static final int MIN_HOLDER_GROWTH = 1024;
    private byte[][] blocks;
    private int blockSize;
    private int allocatedBlocks;
    private long length;
    
    public BlockedByteArray() {
        this.blocks = new byte[1024][];
    }
    
    public synchronized int read(final long n) {
        if (n < this.length) {
            return this.blocks[(int)(n / this.blockSize)][(int)(n % this.blockSize)] & 0xFF;
        }
        return -1;
    }
    
    public synchronized int read(final long n, final byte[] array, final int n2, int index) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (n >= this.length) {
            return -1;
        }
        index = (int)Math.min(index, this.length - n);
        int n3 = (int)(n / this.blockSize);
        int n4 = (int)(n % this.blockSize);
        int i;
        int min;
        for (i = 0; i < index; i += min, ++n3, n4 = 0) {
            min = Math.min(index - i, this.blockSize - n4);
            System.arraycopy(this.blocks[n3], n4, array, n2 + i, min);
        }
        return i;
    }
    
    public synchronized long length() {
        return this.length;
    }
    
    public synchronized void setLength(final long n) {
        if (this.blockSize == 0) {
            this.checkBlockSize((int)Math.min(2147483647L, n));
        }
        final long n2 = this.allocatedBlocks * (long)this.blockSize;
        if (n > n2) {
            this.increaseCapacity(n);
        }
        else if (n < n2) {
            if (n <= 0L) {
                this.allocatedBlocks = 0;
                this.blocks = new byte[1024][];
            }
            else {
                int i;
                int b;
                for (b = (i = (int)(n / this.blockSize) + 1); i <= this.allocatedBlocks; ++i) {
                    this.blocks[i] = null;
                }
                this.allocatedBlocks = Math.min(this.allocatedBlocks, b);
            }
        }
        this.length = Math.max(0L, n);
    }
    
    public synchronized int writeBytes(final long n, final byte[] array, int n2, final int index) {
        if (this.blockSize == 0) {
            this.checkBlockSize(index);
        }
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        this.increaseCapacity(n + index);
        int n3 = (int)(n / this.blockSize);
        int n4 = (int)(n % this.blockSize);
        int i = 0;
        while (i < index) {
            final int min = Math.min(index - i, this.blockSize - n4);
            System.arraycopy(array, n2, this.blocks[n3], n4, min);
            i += min;
            n2 += min;
            if (i < index) {
                ++n3;
                n4 = 0;
            }
            else {
                n4 += min;
            }
        }
        this.length = Math.max(this.length, n + index);
        return i;
    }
    
    public synchronized int writeByte(final long n, final byte b) {
        if (this.blockSize == 0) {
            this.checkBlockSize(0);
        }
        this.increaseCapacity(n);
        this.blocks[(int)(n / this.blockSize)][(int)(n % this.blockSize)] = b;
        this.length = Math.max(this.length, n + 1L);
        return 1;
    }
    
    synchronized BlockedByteArrayInputStream getInputStream() {
        return new BlockedByteArrayInputStream(this, 0L);
    }
    
    synchronized BlockedByteArrayOutputStream getOutputStream(final long lng) {
        if (lng < 0L) {
            throw new IllegalArgumentException("Position cannot be negative: " + lng);
        }
        return new BlockedByteArrayOutputStream(this, lng);
    }
    
    synchronized void release() {
        this.blocks = null;
        final int allocatedBlocks = -1;
        this.allocatedBlocks = allocatedBlocks;
        this.length = allocatedBlocks;
    }
    
    private void checkBlockSize(final int blockSize) {
        if (blockSize == 4096 || blockSize == 8192 || blockSize == 16384 || blockSize == 32768) {
            this.blockSize = blockSize;
        }
        else {
            this.blockSize = 4096;
        }
    }
    
    private void increaseCapacity(final long n) {
        if (n < this.allocatedBlocks * (long)this.blockSize) {
            return;
        }
        final int allocatedBlocks = (int)(n / this.blockSize) + 1;
        if (allocatedBlocks > this.blocks.length) {
            System.arraycopy(this.blocks, 0, this.blocks = new byte[Math.max(this.blocks.length + this.blocks.length / 3, allocatedBlocks + 1024)][], 0, this.allocatedBlocks);
        }
        for (int i = this.allocatedBlocks; i < allocatedBlocks; ++i) {
            this.blocks[i] = new byte[this.blockSize];
        }
        this.allocatedBlocks = allocatedBlocks;
    }
}
