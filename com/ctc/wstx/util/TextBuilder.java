// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

public final class TextBuilder
{
    private static final int MIN_LEN = 60;
    private static final int MAX_LEN = 120;
    private char[] mBuffer;
    private int mBufferLen;
    private String mResultString;
    
    public TextBuilder(final int initialSize) {
        int charSize = initialSize << 4;
        if (charSize < 60) {
            charSize = 60;
        }
        else if (charSize > 120) {
            charSize = 120;
        }
        this.mBuffer = new char[charSize];
    }
    
    public void reset() {
        this.mBufferLen = 0;
        this.mResultString = null;
    }
    
    public boolean isEmpty() {
        return this.mBufferLen == 0;
    }
    
    public String getAllValues() {
        if (this.mResultString == null) {
            this.mResultString = new String(this.mBuffer, 0, this.mBufferLen);
        }
        return this.mResultString;
    }
    
    public char[] getCharBuffer() {
        return this.mBuffer;
    }
    
    public int getCharSize() {
        return this.mBufferLen;
    }
    
    public void append(final char c) {
        if (this.mBuffer.length == this.mBufferLen) {
            this.resize(1);
        }
        this.mBuffer[this.mBufferLen++] = c;
    }
    
    public void append(final char[] src, final int start, final int len) {
        if (len > this.mBuffer.length - this.mBufferLen) {
            this.resize(len);
        }
        System.arraycopy(src, start, this.mBuffer, this.mBufferLen, len);
        this.mBufferLen += len;
    }
    
    public void setBufferSize(final int newSize) {
        this.mBufferLen = newSize;
    }
    
    public char[] bufferFull(final int needSpaceFor) {
        this.mBufferLen = this.mBuffer.length;
        this.resize(needSpaceFor);
        return this.mBuffer;
    }
    
    @Override
    public String toString() {
        return new String(this.mBuffer, 0, this.mBufferLen);
    }
    
    private void resize(int needSpaceFor) {
        final char[] old = this.mBuffer;
        final int oldLen = old.length;
        int addition = oldLen >> 1;
        needSpaceFor -= oldLen - this.mBufferLen;
        if (addition < needSpaceFor) {
            addition = needSpaceFor;
        }
        System.arraycopy(old, 0, this.mBuffer = new char[oldLen + addition], 0, this.mBufferLen);
    }
}
