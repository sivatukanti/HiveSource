// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Reader;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.util.TextBuffer;

public final class BranchingReaderSource extends ReaderSource
{
    TextBuffer mBranchBuffer;
    int mBranchStartOffset;
    boolean mConvertLFs;
    boolean mGotCR;
    
    public BranchingReaderSource(final ReaderConfig cfg, final String pubId, final SystemId sysId, final Reader r, final boolean realClose) {
        super(cfg, null, null, pubId, sysId, r, realClose);
        this.mBranchBuffer = null;
        this.mBranchStartOffset = 0;
        this.mConvertLFs = false;
        this.mGotCR = false;
    }
    
    @Override
    public int readInto(final WstxInputData reader) throws IOException, XMLStreamException {
        if (this.mBranchBuffer != null) {
            if (this.mInputLast > this.mBranchStartOffset) {
                this.appendBranched(this.mBranchStartOffset, this.mInputLast);
            }
            this.mBranchStartOffset = 0;
        }
        return super.readInto(reader);
    }
    
    @Override
    public boolean readMore(final WstxInputData reader, final int minAmount) throws IOException, XMLStreamException {
        if (this.mBranchBuffer != null) {
            final int ptr = reader.mInputPtr;
            final int currAmount = this.mInputLast - ptr;
            if (currAmount > 0) {
                if (ptr > this.mBranchStartOffset) {
                    this.appendBranched(this.mBranchStartOffset, ptr);
                }
                this.mBranchStartOffset = 0;
            }
        }
        return super.readMore(reader, minAmount);
    }
    
    public void startBranch(final TextBuffer tb, final int startOffset, final boolean convertLFs) {
        this.mBranchBuffer = tb;
        this.mBranchStartOffset = startOffset;
        this.mConvertLFs = convertLFs;
        this.mGotCR = false;
    }
    
    public void endBranch(final int endOffset) {
        if (this.mBranchBuffer != null) {
            if (endOffset > this.mBranchStartOffset) {
                this.appendBranched(this.mBranchStartOffset, endOffset);
            }
            this.mBranchBuffer = null;
        }
    }
    
    private void appendBranched(int startOffset, final int pastEnd) {
        if (this.mConvertLFs) {
            final char[] inBuf = this.mBuffer;
            char[] outBuf = this.mBranchBuffer.getCurrentSegment();
            int outPtr = this.mBranchBuffer.getCurrentSegmentSize();
            if (this.mGotCR && inBuf[startOffset] == '\n') {
                ++startOffset;
            }
            while (startOffset < pastEnd) {
                char c = inBuf[startOffset++];
                if (c == '\r') {
                    if (startOffset < pastEnd) {
                        if (inBuf[startOffset] == '\n') {
                            ++startOffset;
                        }
                    }
                    else {
                        this.mGotCR = true;
                    }
                    c = '\n';
                }
                outBuf[outPtr++] = c;
                if (outPtr >= outBuf.length) {
                    outBuf = this.mBranchBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
            }
            this.mBranchBuffer.setCurrentLength(outPtr);
        }
        else {
            this.mBranchBuffer.append(this.mBuffer, startOffset, pastEnd - startOffset);
        }
    }
}
