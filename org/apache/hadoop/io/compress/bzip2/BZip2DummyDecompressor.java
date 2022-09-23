// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.bzip2;

import java.io.IOException;
import org.apache.hadoop.io.compress.Decompressor;

public class BZip2DummyDecompressor implements Decompressor
{
    @Override
    public int decompress(final byte[] b, final int off, final int len) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void end() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean finished() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean needsDictionary() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean needsInput() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getRemaining() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void reset() {
    }
    
    @Override
    public void setDictionary(final byte[] b, final int off, final int len) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setInput(final byte[] b, final int off, final int len) {
        throw new UnsupportedOperationException();
    }
}
