// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.io.IOException;
import java.io.Closeable;

public interface SeekableInput extends Closeable
{
    void seek(final long p0) throws IOException;
    
    long tell() throws IOException;
    
    long length() throws IOException;
    
    int read(final byte[] p0, final int p1, final int p2) throws IOException;
}
