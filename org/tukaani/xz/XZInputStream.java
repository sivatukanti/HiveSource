// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XZInputStream extends InputStream
{
    private final int memoryLimit;
    private InputStream in;
    private SingleXZInputStream xzIn;
    private boolean endReached;
    private IOException exception;
    
    public XZInputStream(final InputStream inputStream) throws IOException {
        this(inputStream, -1);
    }
    
    public XZInputStream(final InputStream in, final int memoryLimit) throws IOException {
        this.endReached = false;
        this.exception = null;
        this.in = in;
        this.memoryLimit = memoryLimit;
        this.xzIn = new SingleXZInputStream(in, memoryLimit);
    }
    
    public int read() throws IOException {
        final byte[] array = { 0 };
        return (this.read(array, 0, 1) == -1) ? -1 : (array[0] & 0xFF);
    }
    
    public int read(final byte[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i < 0 || n + i > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (i == 0) {
            return 0;
        }
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.endReached) {
            return -1;
        }
        int n2 = 0;
        try {
            while (i > 0) {
                if (this.xzIn == null) {
                    this.prepareNextStream();
                    if (this.endReached) {
                        return (n2 == 0) ? -1 : n2;
                    }
                }
                final int read = this.xzIn.read(array, n, i);
                if (read > 0) {
                    n2 += read;
                    n += read;
                    i -= read;
                }
                else {
                    if (read != -1) {
                        continue;
                    }
                    this.xzIn = null;
                }
            }
        }
        catch (IOException exception) {
            this.exception = exception;
            if (n2 == 0) {
                throw exception;
            }
        }
        return n2;
    }
    
    private void prepareNextStream() throws IOException {
        final DataInputStream dataInputStream = new DataInputStream(this.in);
        final byte[] b = new byte[12];
        while (dataInputStream.read(b, 0, 1) != -1) {
            dataInputStream.readFully(b, 1, 3);
            if (b[0] != 0 || b[1] != 0 || b[2] != 0 || b[3] != 0) {
                dataInputStream.readFully(b, 4, 8);
                try {
                    this.xzIn = new SingleXZInputStream(this.in, this.memoryLimit, b);
                }
                catch (XZFormatException ex) {
                    throw new CorruptedInputException("Garbage after a valid XZ Stream");
                }
                return;
            }
        }
        this.endReached = true;
    }
    
    public int available() throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        return (this.xzIn == null) ? 0 : this.xzIn.available();
    }
    
    public void close() throws IOException {
        if (this.in != null) {
            try {
                this.in.close();
            }
            finally {
                this.in = null;
            }
        }
    }
}
