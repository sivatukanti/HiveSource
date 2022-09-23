// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import javax.xml.stream.Location;
import com.ctc.wstx.exc.WstxException;
import java.io.Reader;
import com.ctc.wstx.api.ReaderConfig;

public class ReaderSource extends BaseInputSource
{
    final ReaderConfig mConfig;
    protected Reader mReader;
    final boolean mDoRealClose;
    int mInputProcessed;
    int mInputRow;
    int mInputRowStart;
    
    public ReaderSource(final ReaderConfig cfg, final WstxInputSource parent, final String fromEntity, final String pubId, final SystemId sysId, final Reader r, final boolean realClose) {
        super(parent, fromEntity, pubId, sysId);
        this.mInputProcessed = 0;
        this.mInputRow = 1;
        this.mInputRowStart = 0;
        this.mConfig = cfg;
        this.mReader = r;
        this.mDoRealClose = realClose;
        final int bufSize = cfg.getInputBufferLength();
        this.mBuffer = cfg.allocFullCBuffer(bufSize);
    }
    
    public void setInputOffsets(final int proc, final int row, final int rowStart) {
        this.mInputProcessed = proc;
        this.mInputRow = row;
        this.mInputRowStart = rowStart;
    }
    
    @Override
    protected void doInitInputLocation(final WstxInputData reader) {
        reader.mCurrInputProcessed = this.mInputProcessed;
        reader.mCurrInputRow = this.mInputRow;
        reader.mCurrInputRowStart = this.mInputRowStart;
    }
    
    @Override
    public boolean fromInternalEntity() {
        return false;
    }
    
    @Override
    public int readInto(final WstxInputData reader) throws IOException, XMLStreamException {
        if (this.mBuffer == null) {
            return -1;
        }
        final int count = this.mReader.read(this.mBuffer, 0, this.mBuffer.length);
        if (count >= 1) {
            reader.mInputBuffer = this.mBuffer;
            reader.mInputPtr = 0;
            this.mInputLast = count;
            return reader.mInputEnd = count;
        }
        this.mInputLast = 0;
        reader.mInputPtr = 0;
        reader.mInputEnd = 0;
        if (count == 0) {
            throw new WstxException("Reader (of type " + this.mReader.getClass().getName() + ") returned 0 characters, even when asked to read up to " + this.mBuffer.length, this.getLocation());
        }
        return -1;
    }
    
    @Override
    public boolean readMore(final WstxInputData reader, int minAmount) throws IOException, XMLStreamException {
        if (this.mBuffer == null) {
            return false;
        }
        final int ptr = reader.mInputPtr;
        int currAmount = this.mInputLast - ptr;
        reader.mCurrInputProcessed += ptr;
        reader.mCurrInputRowStart -= ptr;
        if (currAmount > 0) {
            System.arraycopy(this.mBuffer, ptr, this.mBuffer, 0, currAmount);
            minAmount -= currAmount;
        }
        reader.mInputBuffer = this.mBuffer;
        reader.mInputPtr = 0;
        this.mInputLast = currAmount;
        while (minAmount > 0) {
            final int amount = this.mBuffer.length - currAmount;
            final int actual = this.mReader.read(this.mBuffer, currAmount, amount);
            if (actual < 1) {
                if (actual == 0) {
                    throw new WstxException("Reader (of type " + this.mReader.getClass().getName() + ") returned 0 characters, even when asked to read up to " + amount, this.getLocation());
                }
                final int n = currAmount;
                this.mInputLast = n;
                reader.mInputEnd = n;
                return false;
            }
            else {
                currAmount += actual;
                minAmount -= actual;
            }
        }
        final int n2 = currAmount;
        this.mInputLast = n2;
        reader.mInputEnd = n2;
        return true;
    }
    
    @Override
    public void close() throws IOException {
        if (this.mBuffer != null) {
            this.closeAndRecycle(this.mDoRealClose);
        }
    }
    
    @Override
    public void closeCompletely() throws IOException {
        if (this.mReader != null) {
            this.closeAndRecycle(true);
        }
    }
    
    private void closeAndRecycle(final boolean fullClose) throws IOException {
        final char[] buf = this.mBuffer;
        if (buf != null) {
            this.mBuffer = null;
            this.mConfig.freeFullCBuffer(buf);
        }
        if (this.mReader != null) {
            if (this.mReader instanceof BaseReader) {
                ((BaseReader)this.mReader).freeBuffers();
            }
            if (fullClose) {
                final Reader r = this.mReader;
                this.mReader = null;
                r.close();
            }
        }
    }
}
