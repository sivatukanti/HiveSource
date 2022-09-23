// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.OutputStream;
import java.io.IOException;
import java.io.DataOutputStream;

class UncompressedLZMA2OutputStream extends FinishableOutputStream
{
    private FinishableOutputStream out;
    private final DataOutputStream outData;
    private final byte[] uncompBuf;
    private int uncompPos;
    private boolean dictResetNeeded;
    private boolean finished;
    private IOException exception;
    
    static int getMemoryUsage() {
        return 70;
    }
    
    UncompressedLZMA2OutputStream(final FinishableOutputStream finishableOutputStream) {
        this.uncompBuf = new byte[65536];
        this.uncompPos = 0;
        this.dictResetNeeded = true;
        this.finished = false;
        this.exception = null;
        if (finishableOutputStream == null) {
            throw new NullPointerException();
        }
        this.out = finishableOutputStream;
        this.outData = new DataOutputStream(finishableOutputStream);
    }
    
    public void write(final int n) throws IOException {
        this.write(new byte[] { (byte)n }, 0, 1);
    }
    
    public void write(final byte[] array, final int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i < 0 || n + i > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished or closed");
        }
        try {
            while (i > 0) {
                final int min = Math.min(this.uncompBuf.length - this.uncompPos, i);
                System.arraycopy(array, n, this.uncompBuf, this.uncompPos, min);
                i -= min;
                this.uncompPos += min;
                if (this.uncompPos == this.uncompBuf.length) {
                    this.writeChunk();
                }
            }
        }
        catch (IOException exception) {
            throw this.exception = exception;
        }
    }
    
    private void writeChunk() throws IOException {
        this.outData.writeByte(this.dictResetNeeded ? 1 : 2);
        this.outData.writeShort(this.uncompPos - 1);
        this.outData.write(this.uncompBuf, 0, this.uncompPos);
        this.uncompPos = 0;
        this.dictResetNeeded = false;
    }
    
    private void writeEndMarker() throws IOException {
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished or closed");
        }
        try {
            if (this.uncompPos > 0) {
                this.writeChunk();
            }
            this.out.write(0);
        }
        catch (IOException exception) {
            throw this.exception = exception;
        }
    }
    
    public void flush() throws IOException {
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished or closed");
        }
        try {
            if (this.uncompPos > 0) {
                this.writeChunk();
            }
            this.out.flush();
        }
        catch (IOException exception) {
            throw this.exception = exception;
        }
    }
    
    public void finish() throws IOException {
        if (!this.finished) {
            this.writeEndMarker();
            try {
                this.out.finish();
            }
            catch (IOException exception) {
                throw this.exception = exception;
            }
            this.finished = true;
        }
    }
    
    public void close() throws IOException {
        if (this.out != null) {
            if (!this.finished) {
                try {
                    this.writeEndMarker();
                }
                catch (IOException ex) {}
            }
            try {
                this.out.close();
            }
            catch (IOException exception) {
                if (this.exception == null) {
                    this.exception = exception;
                }
            }
            this.out = null;
        }
        if (this.exception != null) {
            throw this.exception;
        }
    }
}
