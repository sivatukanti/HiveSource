// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import javax.xml.stream.Location;

public final class CharArraySource extends BaseInputSource
{
    int mOffset;
    final Location mContentStart;
    
    protected CharArraySource(final WstxInputSource parent, final String fromEntity, final char[] chars, final int offset, final int len, final Location loc, final SystemId sysId) {
        super(parent, fromEntity, loc.getPublicId(), sysId);
        this.mBuffer = chars;
        this.mOffset = offset;
        this.mInputLast = offset + len;
        this.mContentStart = loc;
    }
    
    @Override
    public boolean fromInternalEntity() {
        return true;
    }
    
    @Override
    protected void doInitInputLocation(final WstxInputData reader) {
        reader.mCurrInputProcessed = this.mContentStart.getCharacterOffset();
        reader.mCurrInputRow = this.mContentStart.getLineNumber();
        reader.mCurrInputRowStart = -this.mContentStart.getColumnNumber() + 1;
    }
    
    @Override
    public int readInto(final WstxInputData reader) {
        if (this.mBuffer == null) {
            return -1;
        }
        final int len = this.mInputLast - this.mOffset;
        if (len < 1) {
            return -1;
        }
        reader.mInputBuffer = this.mBuffer;
        reader.mInputPtr = this.mOffset;
        reader.mInputEnd = this.mInputLast;
        this.mOffset = this.mInputLast;
        return len;
    }
    
    @Override
    public boolean readMore(final WstxInputData reader, final int minAmount) {
        if (reader.mInputPtr >= reader.mInputEnd) {
            final int len = this.mInputLast - this.mOffset;
            if (len >= minAmount) {
                return this.readInto(reader) > 0;
            }
        }
        return false;
    }
    
    @Override
    public void close() {
        this.mBuffer = null;
    }
    
    @Override
    public void closeCompletely() {
        this.close();
    }
}
