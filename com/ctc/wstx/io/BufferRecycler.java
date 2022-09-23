// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

public final class BufferRecycler
{
    private char[] mSmallCBuffer;
    private char[] mMediumCBuffer;
    private char[] mFullCBuffer;
    private byte[] mFullBBuffer;
    
    public BufferRecycler() {
        this.mSmallCBuffer = null;
        this.mMediumCBuffer = null;
        this.mFullCBuffer = null;
        this.mFullBBuffer = null;
    }
    
    public char[] getSmallCBuffer(final int minSize) {
        char[] result = null;
        if (this.mSmallCBuffer != null && this.mSmallCBuffer.length >= minSize) {
            result = this.mSmallCBuffer;
            this.mSmallCBuffer = null;
        }
        return result;
    }
    
    public void returnSmallCBuffer(final char[] buffer) {
        this.mSmallCBuffer = buffer;
    }
    
    public char[] getMediumCBuffer(final int minSize) {
        char[] result = null;
        if (this.mMediumCBuffer != null && this.mMediumCBuffer.length >= minSize) {
            result = this.mMediumCBuffer;
            this.mMediumCBuffer = null;
        }
        return result;
    }
    
    public void returnMediumCBuffer(final char[] buffer) {
        this.mMediumCBuffer = buffer;
    }
    
    public char[] getFullCBuffer(final int minSize) {
        char[] result = null;
        if (this.mFullCBuffer != null && this.mFullCBuffer.length >= minSize) {
            result = this.mFullCBuffer;
            this.mFullCBuffer = null;
        }
        return result;
    }
    
    public void returnFullCBuffer(final char[] buffer) {
        this.mFullCBuffer = buffer;
    }
    
    public byte[] getFullBBuffer(final int minSize) {
        byte[] result = null;
        if (this.mFullBBuffer != null && this.mFullBBuffer.length >= minSize) {
            result = this.mFullBBuffer;
            this.mFullBBuffer = null;
        }
        return result;
    }
    
    public void returnFullBBuffer(final byte[] buffer) {
        this.mFullBBuffer = buffer;
    }
}
