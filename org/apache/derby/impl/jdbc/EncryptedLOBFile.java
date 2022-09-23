// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.io.EOFException;
import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.io.FileNotFoundException;
import org.apache.derby.io.StorageFile;
import org.apache.derby.iapi.store.raw.data.DataFactory;

class EncryptedLOBFile extends LOBFile
{
    private final int blockSize;
    private final byte[] tail;
    private int tailSize;
    private long currentPos;
    private final DataFactory df;
    
    EncryptedLOBFile(final StorageFile storageFile, final DataFactory df) throws FileNotFoundException {
        super(storageFile);
        this.df = df;
        this.blockSize = df.getEncryptionBlockSize();
        this.tail = new byte[this.blockSize];
        this.tailSize = 0;
    }
    
    private byte[] getBlocks(final long n, final int value) throws IOException, StandardException {
        if (value < 0) {
            throw new IndexOutOfBoundsException(MessageService.getTextMessage("XJ071.S", new Integer(value)));
        }
        final long n2 = n - n % this.blockSize;
        final byte[] array = new byte[(int)((n + value + this.blockSize - 1L) / this.blockSize * this.blockSize - n2)];
        super.seek(n2);
        super.read(array, 0, array.length);
        return array;
    }
    
    long length() throws IOException {
        return super.length() + this.tailSize;
    }
    
    long getFilePointer() {
        return this.currentPos;
    }
    
    void seek(final long currentPos) throws IOException {
        final long length = super.length();
        if (currentPos > length + this.tailSize) {
            throw new IllegalArgumentException("Internal Error");
        }
        if (currentPos < length) {
            super.seek(currentPos);
        }
        this.currentPos = currentPos;
    }
    
    void write(final int n) throws IOException, StandardException {
        final long length = super.length();
        if (this.currentPos >= length) {
            final int n2 = (int)(this.currentPos - length);
            this.tail[n2] = (byte)n;
            if (n2 >= this.tailSize) {
                this.tailSize = n2 + 1;
            }
            if (this.tailSize == this.blockSize) {
                final byte[] array = new byte[this.blockSize];
                this.df.encrypt(this.tail, 0, this.tailSize, array, 0, false);
                super.seek(length);
                super.write(array);
                this.tailSize = 0;
            }
        }
        else {
            final byte[] blocks = this.getBlocks(this.currentPos, 1);
            final byte[] array2 = new byte[this.blockSize];
            this.df.decrypt(blocks, 0, this.blockSize, array2, 0);
            array2[(int)(this.currentPos % this.blockSize)] = (byte)n;
            this.df.encrypt(array2, 0, this.blockSize, blocks, 0, false);
            super.seek(this.currentPos - this.currentPos % this.blockSize);
            super.write(blocks);
        }
        ++this.currentPos;
    }
    
    void write(final byte[] array, int n, int n2) throws IOException, StandardException {
        final long length = super.length();
        if (this.currentPos < length) {
            final int n3 = (int)Math.max(0L, this.currentPos + n2 - length);
            final long currentPos = this.currentPos;
            final byte[] blocks = this.getBlocks(this.currentPos, n2 - n3);
            final byte[] array2 = new byte[blocks.length];
            for (int i = 0; i < blocks.length / this.blockSize; ++i) {
                this.df.decrypt(blocks, i * this.blockSize, this.blockSize, array2, i * this.blockSize);
            }
            System.arraycopy(array, n, array2, (int)(this.currentPos % this.blockSize), n2 - n3);
            for (int j = 0; j < blocks.length / this.blockSize; ++j) {
                this.df.encrypt(array2, j * this.blockSize, this.blockSize, blocks, j * this.blockSize, false);
            }
            super.seek(currentPos - currentPos % this.blockSize);
            super.write(blocks);
            this.currentPos = currentPos + blocks.length;
            if (n3 == 0) {
                return;
            }
            n = n + n2 - n3;
            n2 = n3;
            this.currentPos = length;
        }
        final int n4 = (int)(this.currentPos - length);
        final int n5 = n4 + n2;
        if (n5 < this.blockSize) {
            System.arraycopy(array, n, this.tail, n4, n2);
            this.tailSize = Math.max(this.tailSize, n4 + n2);
            this.currentPos += n2;
            return;
        }
        final int n6 = n5 - n5 % this.blockSize;
        final int tailSize = n5 % this.blockSize;
        final byte[] array3 = new byte[n6];
        System.arraycopy(this.tail, 0, array3, 0, n4);
        System.arraycopy(array, n, array3, n4, n6 - n4);
        final byte[] array4 = new byte[array3.length];
        for (int k = 0; k < array4.length; k += this.blockSize) {
            this.df.encrypt(array3, k, this.blockSize, array4, k, false);
        }
        super.seek(length);
        super.write(array4);
        System.arraycopy(array, n + n2 - tailSize, this.tail, 0, tailSize);
        this.tailSize = tailSize;
        this.currentPos = this.tailSize + length + array4.length;
    }
    
    void write(final byte[] array) throws IOException, StandardException {
        this.write(array, 0, array.length);
    }
    
    void close() throws IOException {
        super.close();
    }
    
    int readByte() throws IOException, StandardException {
        final long length = super.length();
        if (this.currentPos >= length + this.tailSize) {
            throw new EOFException();
        }
        if (this.currentPos >= length) {
            return this.tail[(int)(this.currentPos++ - length)] & 0xFF;
        }
        final byte[] blocks = this.getBlocks(this.currentPos, 1);
        final byte[] array = new byte[blocks.length];
        this.df.decrypt(blocks, 0, blocks.length, array, 0);
        return array[(int)(this.currentPos++ % this.blockSize)] & 0xFF;
    }
    
    int read(final byte[] array, final int n, final int n2) throws IOException, StandardException {
        final long length = super.length();
        if (this.currentPos < length) {
            final int a = (int)Math.max(0L, this.currentPos + n2 - length);
            final byte[] blocks = this.getBlocks(this.currentPos, n2 - a);
            final byte[] array2 = new byte[blocks.length];
            for (int i = 0; i < blocks.length; i += this.blockSize) {
                this.df.decrypt(blocks, i, this.blockSize, array2, i);
            }
            System.arraycopy(array2, (int)(this.currentPos % this.blockSize), array, n, n2 - a);
            if (a == 0) {
                this.currentPos += n2;
                return n2;
            }
            final int min = Math.min(a, this.tailSize);
            System.arraycopy(this.tail, 0, array, n + n2 - a, min);
            this.currentPos += n2 - a + min;
            return n2 - a + min;
        }
        else {
            final int n3 = (int)Math.min(this.tailSize - this.currentPos + length, n2);
            if (n3 == 0 && n2 != 0) {
                return -1;
            }
            System.arraycopy(this.tail, (int)(this.currentPos - length), array, n, n3);
            this.currentPos += n3;
            return n3;
        }
    }
    
    void setLength(final long n) throws IOException, StandardException {
        final long length = super.length();
        if (n > length + this.tailSize) {
            throw new IllegalArgumentException("Internal Error");
        }
        if (n < length) {
            final byte[] blocks = this.getBlocks(n, 1);
            super.setLength(n - n % this.blockSize);
            this.df.decrypt(blocks, 0, this.blockSize, this.tail, 0);
            this.tailSize = (int)(n % this.blockSize);
        }
        else {
            this.tailSize = (int)(n - length);
        }
    }
}
