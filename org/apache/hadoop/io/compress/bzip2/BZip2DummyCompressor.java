// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.bzip2;

import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import org.apache.hadoop.io.compress.Compressor;

public class BZip2DummyCompressor implements Compressor
{
    @Override
    public int compress(final byte[] b, final int off, final int len) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void end() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void finish() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean finished() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long getBytesRead() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long getBytesWritten() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean needsInput() {
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
    
    @Override
    public void reinit(final Configuration conf) {
    }
}
