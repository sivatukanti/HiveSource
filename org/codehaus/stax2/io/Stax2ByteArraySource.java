// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Stax2ByteArraySource extends Stax2BlockSource
{
    private static final String DEFAULT_ENCODING = "UTF-8";
    final byte[] mBuffer;
    final int mStart;
    final int mLength;
    
    public Stax2ByteArraySource(final byte[] mBuffer, final int mStart, final int mLength) {
        this.mBuffer = mBuffer;
        this.mStart = mStart;
        this.mLength = mLength;
    }
    
    @Override
    public Reader constructReader() throws IOException {
        String encoding = this.getEncoding();
        final InputStream constructInputStream = this.constructInputStream();
        if (encoding == null || encoding.length() == 0) {
            encoding = "UTF-8";
        }
        return new InputStreamReader(constructInputStream, encoding);
    }
    
    @Override
    public InputStream constructInputStream() throws IOException {
        return new ByteArrayInputStream(this.mBuffer, this.mStart, this.mLength);
    }
    
    public byte[] getBuffer() {
        return this.mBuffer;
    }
    
    public int getBufferStart() {
        return this.mStart;
    }
    
    public int getBufferLength() {
        return this.mLength;
    }
    
    public int getBufferEnd() {
        int mStart = this.mStart;
        if (this.mLength > 0) {
            mStart += this.mLength;
        }
        return mStart;
    }
}
