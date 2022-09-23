// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.CharArrayReader;
import java.io.Reader;

public class Stax2CharArraySource extends Stax2BlockSource
{
    final char[] mBuffer;
    final int mStart;
    final int mLength;
    
    public Stax2CharArraySource(final char[] mBuffer, final int mStart, final int mLength) {
        this.mBuffer = mBuffer;
        this.mStart = mStart;
        this.mLength = mLength;
    }
    
    @Override
    public Reader constructReader() throws IOException {
        return new CharArrayReader(this.mBuffer, this.mStart, this.mLength);
    }
    
    @Override
    public InputStream constructInputStream() throws IOException {
        return null;
    }
    
    public char[] getBuffer() {
        return this.mBuffer;
    }
    
    public int getBufferStart() {
        return this.mStart;
    }
    
    public int getBufferLength() {
        return this.mLength;
    }
}
