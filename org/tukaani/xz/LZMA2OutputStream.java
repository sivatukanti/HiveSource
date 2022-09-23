// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.OutputStream;
import java.io.IOException;
import org.tukaani.xz.lzma.LZMAEncoder;
import org.tukaani.xz.rangecoder.RangeEncoder;
import org.tukaani.xz.lz.LZEncoder;
import java.io.DataOutputStream;

class LZMA2OutputStream extends FinishableOutputStream
{
    static final int COMPRESSED_SIZE_MAX = 65536;
    private FinishableOutputStream out;
    private final DataOutputStream outData;
    private final LZEncoder lz;
    private final RangeEncoder rc;
    private final LZMAEncoder lzma;
    private int props;
    private boolean dictResetNeeded;
    private boolean stateResetNeeded;
    private boolean propsNeeded;
    private int pendingSize;
    private boolean finished;
    private IOException exception;
    
    private static int getExtraSizeBefore(final int n) {
        return (65536 > n) ? (65536 - n) : 0;
    }
    
    static int getMemoryUsage(final LZMA2Options lzma2Options) {
        final int dictSize = lzma2Options.getDictSize();
        return 70 + LZMAEncoder.getMemoryUsage(lzma2Options.getMode(), dictSize, getExtraSizeBefore(dictSize), lzma2Options.getMatchFinder());
    }
    
    LZMA2OutputStream(final FinishableOutputStream finishableOutputStream, final LZMA2Options lzma2Options) {
        this.dictResetNeeded = true;
        this.stateResetNeeded = true;
        this.propsNeeded = true;
        this.pendingSize = 0;
        this.finished = false;
        this.exception = null;
        if (finishableOutputStream == null) {
            throw new NullPointerException();
        }
        this.out = finishableOutputStream;
        this.outData = new DataOutputStream(finishableOutputStream);
        this.rc = new RangeEncoder(65536);
        final int dictSize = lzma2Options.getDictSize();
        this.lzma = LZMAEncoder.getInstance(this.rc, lzma2Options.getLc(), lzma2Options.getLp(), lzma2Options.getPb(), lzma2Options.getMode(), dictSize, getExtraSizeBefore(dictSize), lzma2Options.getNiceLen(), lzma2Options.getMatchFinder(), lzma2Options.getDepthLimit());
        (this.lz = this.lzma.getLZEncoder()).setPresetDict(dictSize, lzma2Options.getPresetDict());
        this.props = (lzma2Options.getPb() * 5 + lzma2Options.getLp()) * 9 + lzma2Options.getLc();
    }
    
    public void write(final int n) throws IOException {
        this.write(new byte[] { (byte)n }, 0, 1);
    }
    
    public void write(final byte[] array, int n, int i) throws IOException {
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
                final int fillWindow = this.lz.fillWindow(array, n, i);
                n += fillWindow;
                i -= fillWindow;
                this.pendingSize += fillWindow;
                if (this.lzma.encodeForLZMA2()) {
                    this.writeChunk();
                }
            }
        }
        catch (IOException exception) {
            throw this.exception = exception;
        }
    }
    
    private void writeChunk() throws IOException {
        final int finish = this.rc.finish();
        int n = this.lzma.getUncompressedSize();
        if (finish + 2 < n) {
            this.writeLZMA(n, finish);
        }
        else {
            this.lzma.reset();
            n = this.lzma.getUncompressedSize();
            this.writeUncompressed(n);
        }
        this.pendingSize -= n;
        this.lzma.resetUncompressedSize();
        this.rc.reset();
    }
    
    private void writeLZMA(final int n, final int n2) throws IOException {
        int n3;
        if (this.propsNeeded) {
            if (this.dictResetNeeded) {
                n3 = 224;
            }
            else {
                n3 = 192;
            }
        }
        else if (this.stateResetNeeded) {
            n3 = 160;
        }
        else {
            n3 = 128;
        }
        this.outData.writeByte(n3 | n - 1 >>> 16);
        this.outData.writeShort(n - 1);
        this.outData.writeShort(n2 - 1);
        if (this.propsNeeded) {
            this.outData.writeByte(this.props);
        }
        this.rc.write(this.out);
        this.propsNeeded = false;
        this.stateResetNeeded = false;
        this.dictResetNeeded = false;
    }
    
    private void writeUncompressed(int i) throws IOException {
        while (i > 0) {
            final int min = Math.min(i, 65536);
            this.outData.writeByte(this.dictResetNeeded ? 1 : 2);
            this.outData.writeShort(min - 1);
            this.lz.copyUncompressed(this.out, i, min);
            i -= min;
            this.dictResetNeeded = false;
        }
        this.stateResetNeeded = true;
    }
    
    private void writeEndMarker() throws IOException {
        assert !this.finished;
        if (this.exception != null) {
            throw this.exception;
        }
        this.lz.setFinishing();
        try {
            while (this.pendingSize > 0) {
                this.lzma.encodeForLZMA2();
                this.writeChunk();
            }
            this.out.write(0);
        }
        catch (IOException exception) {
            throw this.exception = exception;
        }
        this.finished = true;
    }
    
    public void flush() throws IOException {
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished or closed");
        }
        try {
            this.lz.setFlushing();
            while (this.pendingSize > 0) {
                this.lzma.encodeForLZMA2();
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
