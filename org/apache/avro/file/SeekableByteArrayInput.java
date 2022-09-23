// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.io.IOException;
import java.io.ByteArrayInputStream;

public class SeekableByteArrayInput extends ByteArrayInputStream implements SeekableInput
{
    public SeekableByteArrayInput(final byte[] data) {
        super(data);
    }
    
    @Override
    public long length() throws IOException {
        return this.count;
    }
    
    @Override
    public void seek(final long p) throws IOException {
        this.reset();
        this.skip(p);
    }
    
    @Override
    public long tell() throws IOException {
        return this.pos;
    }
}
